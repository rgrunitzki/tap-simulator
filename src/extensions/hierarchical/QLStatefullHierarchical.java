/* 
 * Copyright (C) 2017 Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package extensions.hierarchical;

import driver.learning.reward.AbstractRewardFunction;
import driver.learning.exploration.EpsilonDecreasing;
import driver.Driver;
import static driver.learning.exploration.EpsilonDecreasing.EPSILON_DECAY;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;
import scenario.network.AbstractEdge;
import simulation.Params;
import simulation.Simulation;

/**
 * *
 * @author Ricardo Grunitzki
 */
public class QLStatefullHierarchical extends Driver<QLStatefullHierarchical, List<AbstractEdge>> {

    /**
     * Contains the available nodes/states per Neighborhood.
     */
    private static final Map<String, Set<String>> VERTICES_PER_NEIGHBORHOOD = new ConcurrentHashMap<>();

    /**
     * List of terminal nodes.
     */
    public static Set<String> TERMINAL_VERTICES;

    public static Set<String> TERMINAL_VERTICES_COPY;

    /**
     * Collection of existing neighborhood on network.
     */
    private static final Queue<String> NEIGHBORHOODS_QUEUE = new PriorityBlockingQueue<>();

    private static final Queue<String> NEIGHBORHOODS_QUEUE_COPY = new PriorityBlockingQueue<>();

    /**
     * Markov decision process.
     */
    private HierarchicalMDP mdp = new HierarchicalMDP();

    /**
     * Learning rate parameter of Q-Learning.
     */
    public static double ALPHA = 0.9;

    /**
     * Discount factor parameter of Q-Learning.
     */
    public static double GAMMA = 0.99;
    /**
     * Origin of the trip.
     */
    private final String absoluteOrigin;
    /**
     * Destination of the trip.
     */
    private final String absoluteDestination;

    /**
     * Reward function.
     */
    private final AbstractRewardFunction rewardFunction = new HierarchicalStetefullRewardFunction(graph);

    /**
     * Neighborhood in which the agent is situated at current moment.
     */
    private static String CURRENT_NEIGHBORHOOD = null;

    /**
     * Flag indicating in which level the agent is learning.
     */
    public static boolean FIRST_LEVEL = true;
    /**
     * Current action/option taken at the second level MDP.
     */
    private LinkedList<AbstractEdge> secondLevelAction;
    /**
     * Index of the current action/option taken at the second level MDP.
     */
    private int secondaryActionInternalIndex = 0;

    /**
     * Reward of the action/option at the second level.
     */
    private double secondaryReward = 0.0;
    /**
     * Current state at the second level MDP.
     */
    private String currentSecondaryState = "";

    /**
     * Relative delta value used as stopping criteria for low level MDPs.
     */
    public static double DELTA_FIRST_LEVEL = 0.0001;

    /**
     * Flag used to identify the moment in which the current MDP must be
     * changed.
     */
    private static boolean CHANGE_MDP = false;

    /**
     * Certifies the method {@code AFTER_EPISODE_GENERAL_UPDATE()} is run once
     * per episode.
     */
    private static boolean AFTER_UPDATE = false;
    /**
     * Certifies the method {@code beforeEpisodeMDPsUpdate()} is run once per
     * episode.
     */
    private static boolean BEFORE_UPDATE = true;

    /**
     * Creates an QLStatefull driver according to its specifications.
     *
     * @param id Driver identifier
     * @param origin Origin node
     * @param destination Destination node
     * @param graph Road network
     */
    public QLStatefullHierarchical(int id, String origin, String destination, Graph graph) {
        super(id, origin, destination, graph);
        this.absoluteDestination = destination;
        this.absoluteOrigin = origin;
    }

    private static synchronized void updateQueue() {
        if (NEIGHBORHOODS_QUEUE.isEmpty() && !NEIGHBORHOODS_QUEUE_COPY.isEmpty()) {
            NEIGHBORHOODS_QUEUE.addAll(NEIGHBORHOODS_QUEUE_COPY);
        }

        TERMINAL_VERTICES = Collections.synchronizedSet(TERMINAL_VERTICES_COPY);
    }

    private static synchronized void addToQueue(String neighborhood) {
        if (!NEIGHBORHOODS_QUEUE.contains(neighborhood)) {
            NEIGHBORHOODS_QUEUE.add(neighborhood);
            NEIGHBORHOODS_QUEUE_COPY.add(neighborhood);
        }
    }

    @Override
    public void afterSimulation() {
    }

    @Override
    public synchronized void beforeSimulation() {
        updateQueue();
        //Low level MDPs
        Map<String, Map<String, Map<AbstractEdge, Double>>> lowLevelMDPs = new ConcurrentHashMap<>();
        /*
         * Creates LowLevelMDPs
         */
        //Set of states
        Set<String> nodes = graph.vertexSet();
        //identify neighborhoods
        for (String node : nodes) {
            //Neighborhood name
            String neighborhood = node.substring(0, 1);

            synchronized (this) {
                if (!VERTICES_PER_NEIGHBORHOOD.containsKey(neighborhood)) {
                    VERTICES_PER_NEIGHBORHOOD.put(neighborhood, new HashSet<>());
                    addToQueue(neighborhood);
                }
                VERTICES_PER_NEIGHBORHOOD.get(neighborhood).add(node);
            }
        }

        for (String terminalState : TERMINAL_VERTICES) {
            //neighborhood name
            String neighborhood = getNeighborhood(terminalState);
            //low level mdp
            Map states = new ConcurrentHashMap<>();
            for (String vertex : VERTICES_PER_NEIGHBORHOOD.get(neighborhood)) {
                Set<AbstractEdge> actionsPerState = graph.edgesOf(vertex);
                Map actions = new ConcurrentHashMap();
                for (AbstractEdge edg : actionsPerState) {
                    //verifies if the edge is outgoing and belongs to the same neighborhood
                    if (edg.getSourceVertex().equalsIgnoreCase(vertex)
                            && getNeighborhood(edg.getTargetVertex()).equalsIgnoreCase(neighborhood)) {
                        actions.put(edg, new Random().nextDouble() * 0.1);
                    }
                }
                states.put(vertex, actions);
            }
            lowLevelMDPs.put(terminalState, states);
        }
        this.mdp.setLowLevelMDPs(lowLevelMDPs);

    }

    @Override
    public synchronized void afterEpisode() {
        AFTER_EPISODE_GENERAL_UPDATE();
    }

    @Override
    public synchronized void beforeEpisode() {
        this.mdp.resetDetaQ();
        //Certifies that the agents will learn first on neighborhood A
        BEFORE_EPISODE_GENERAL_UPDATE();
        //In first level MDPs, generates several OD-MATRIX
        if (FIRST_LEVEL) {
            //random destination (terminal vertex) from the current neighborhood
            this.destination = getRandomDestinationNode();
            //random origin (nonterminal vertex) from current neighborhood
            this.currentVertex = getRandomOrigin();

            //sets the correct MDP
            this.mdp.setMdp(mdp.lowLevelMDPs.get(this.destination));
            this.currentEdge = null;
            this.travelTime = 0;
            this.route = new LinkedList<>();
        } else {
            //I have to update the second level MDP with the knowledge acquiride in the first level mdps
            if (Params.CURRENT_EPISODE == 0) {
                //creates a High level MDP
                createHighLevelMDP();
            }

            //sets the fixed origin defined on OD-Matrix
            this.currentVertex = this.absoluteOrigin;
            //sets the fixed destination defined on OD-Matrix
            this.destination = this.absoluteDestination;
            this.currentEdge = null;
            this.travelTime = 0;
            this.route = new LinkedList<>();
        }

    }

    /**
     * This method performs an internal update of common variables that are used
     * to control the hierarchical methods. The updates are run after each
     * episode start. The method runs only once per episode.
     */
    static synchronized void AFTER_EPISODE_GENERAL_UPDATE() {
        if (AFTER_UPDATE) {
            //learning process is on first level mdps?
            if (FIRST_LEVEL) {
                //stopping criterion has been reached?
                if (Params.CURRENT_EPISODE == Params.MAX_EPISODES - 2
                        || Simulation.stopCriterion.stop(DELTA_FIRST_LEVEL)) {
                    Simulation.stopCriterion.setConstraint(true);
                    CHANGE_MDP = true;
                }
            } else {
                //Learning on second level
                //removes the constraint in order to enable the 
                if (Simulation.stopCriterion.isConstraint()) {
                    Simulation.stopCriterion.setConstraint(false);
                }
            }

            AFTER_UPDATE = false;
            BEFORE_UPDATE = true;
        }

    }

    /**
     * This method performs an internal update of common variables that are used
     * to control the hierarchical methods. The updates are run before each
     * episode start. The method runs only once per episode.
     */
    private static synchronized void BEFORE_EPISODE_GENERAL_UPDATE() {
        if (BEFORE_UPDATE) {
            if (CURRENT_NEIGHBORHOOD == null || CHANGE_MDP) {
                CHANGE_MDP = false;
                Params.CURRENT_EPISODE = 0;
                if (NEIGHBORHOODS_QUEUE.size() > 0) {
                    FIRST_LEVEL = true;
                    setNeighborhood(pullFromNeighborhoodsQueue());
                    Simulation.stopCriterion.reset();
                    Simulation.stopCriterion.setConstraint(true);
                } else {
                    FIRST_LEVEL = false;
                    setNeighborhood("Z");
                    Simulation.stopCriterion.reset();
                }
                //CURRENT_NEIGHBORHOOD = NEIGHBORHOODS_QUEUE.poll();
                if (Params.PRINT_ON_TERMINAL) {
                    System.out.println("Learning on mdp " + CURRENT_NEIGHBORHOOD);
                }
            }

            BEFORE_UPDATE = false;
            AFTER_UPDATE = true;
        }
    }

    @Override
    public void afterStep() {
        this.travelTime += currentEdge.getCost();

        if (FIRST_LEVEL) {
            //update q-table
            double qa = this.mdp.getValue(currentEdge);
            double qa_old = qa;
            double r = this.rewardFunction.getReward(this);

            double maxQa = 0.0;

            if (!currentEdge.getTargetVertex().equals(destination)
                    && !this.mdp.getMdp().get(currentEdge.getTargetVertex()).keySet().isEmpty()) {
                Map<AbstractEdge, Double> mdp2 = this.mdp.getMdp().get(currentEdge.getTargetVertex());
                maxQa = Collections.max(mdp2.entrySet(), (entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).getValue();
            }
            qa = (1 - ALPHA) * qa + ALPHA * (r + GAMMA * maxQa);

            this.mdp.setValue(currentEdge, qa);

            this.mdp.updateDetalQ(qa - qa_old);
        } else {
            secondaryReward += this.rewardFunction.getReward(this);
            if (secondLevelAction.size() == secondaryActionInternalIndex) {
                //update qvalue
                double qa = this.mdp.highLevelMDP.get(this.currentSecondaryState).get(secondLevelAction);
                double qa_old = qa;
                double r = secondaryReward;
                double maxQa = 0.0;

                if (!this.currentEdge.getTargetVertex().equals(this.absoluteDestination)
                        && !this.mdp.highLevelMDP.get(this.currentEdge.getTargetVertex()).keySet().isEmpty()) {
                    Map<List<AbstractEdge>, Double> mdp2 = this.mdp.highLevelMDP.get(this.currentEdge.getTargetVertex());
                    maxQa = Collections.max(mdp2.entrySet(), (entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).getValue();
                }
                qa = (1 - ALPHA) * qa + ALPHA * (r + GAMMA * maxQa);

                this.mdp.highLevelMDP.get(this.currentSecondaryState).put(secondLevelAction, qa);
                this.mdp.updateDetalQ(qa - qa_old);
                secondaryReward = 0.0;
            }

        }
    }

    @Override
    public void beforeStep() {
        if (FIRST_LEVEL) {
            currentEdge = mdp.getAction(mdp.getMdp().get(currentVertex));
            this.route.add(currentEdge);
            this.currentVertex = currentEdge.getTargetVertex();
            this.learningEffort++;
        } else {

            //verifies if the agent is performing an action
            if ((secondLevelAction == null || secondLevelAction.size() == secondaryActionInternalIndex)) {
                //case not, choose a new one using e-decreasing strategy   
                try {
                    float random = Params.RANDOM.nextFloat();
                    double epsilon = 1 * Math.pow(EPSILON_DECAY, Params.CURRENT_EPISODE);
                    if (random <= epsilon) {
                        List options = new ArrayList<>(mdp.highLevelMDP.get(currentVertex).keySet());
                        Collections.shuffle(options, Params.RANDOM);
                        secondLevelAction = (LinkedList<AbstractEdge>) options.get(0);
                    } else {
                        secondLevelAction = (LinkedList<AbstractEdge>) Collections.max(mdp.highLevelMDP.get(currentVertex).entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue())).getKey();
                    }

                    learningEffort++;
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }

                this.currentSecondaryState = secondLevelAction.getFirst().getSourceVertex();
                secondaryActionInternalIndex = 0;
            }
            this.currentEdge = this.secondLevelAction.get(secondaryActionInternalIndex);
            secondaryActionInternalIndex++;
            this.route.add(currentEdge);
            this.currentVertex = currentEdge.getTargetVertex();
        }
    }

    @Override
    public void resetAll() {
//        System.out.println(Params.CURRENT_EPISODE + "\t" + Params.CURRENT_STEP + "\t");
        this.learningEffort = 0;
        this.mdp = new HierarchicalMDP();
        FIRST_LEVEL = true;
        this.currentSecondaryState = "";
        this.secondLevelAction = null;
        this.secondaryActionInternalIndex = 0;
        this.secondaryReward = 0;
        CURRENT_NEIGHBORHOOD = null;
    }

    @Override
    public boolean hasArrived() {
        return ((this.currentEdge != null) && (this.currentEdge.getTargetVertex().equals(destination) && this.stepA));
    }

    @Override
    public List<AbstractEdge> getRoute() {
        return this.route;
    }

    @Override
    public List<Pair> getParameters() {
        List<Pair> list = new ArrayList<>();
        list.add(Pair.of(this.getClass().getSimpleName().toLowerCase(), ""));
        list.add(Pair.of("epsilon", EpsilonDecreasing.EPSILON_INITIAL));
        list.add(Pair.of("epsilon-decay", EpsilonDecreasing.EPSILON_DECAY));
        list.add(Pair.of("alpha", QLStatefullHierarchical.ALPHA));
        list.add(Pair.of("gamma", QLStatefullHierarchical.GAMMA));
        return list;
    }

    @Override
    public double getTravelTime() {
        double cost = 0;
        for (AbstractEdge e : this.route) {
            cost += e.getCost();
        }
        return cost;
    }

    /*
     * METHODS FOR HIERALQUICAL QL.
     */
    /**
     * Computes the options of each low level MDP.
     *
     * @param initialState
     * @param terminalState
     * @return an option.
     */
    private Entry<List<AbstractEdge>, Double> getOptimalPolicyForNeighborhood(String initialState, String terminalState) {
        LinkedList<AbstractEdge> option = new LinkedList<>();
        String currentState = initialState;
        Double qValue = 0.0;
        Entry<AbstractEdge, Double> entry;
        //counter for the discont factor
        int cont = 0;
        String lastState = "";
        do {
            //take the best action
            entry = Collections.max(this.mdp.lowLevelMDPs.get(terminalState).get(currentState).entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));
            //verifies if this action represents  a "turn around"
//            if(cont>6){
//                System.out.println(entry.getKey().getTargetVertex());
//            }
            if (entry.getKey().getTargetVertex().equalsIgnoreCase(lastState)) {
                List<Entry<AbstractEdge, Double>> entrs = new ArrayList<>(this.mdp.lowLevelMDPs.get(terminalState).get(currentState).entrySet());
                Collections.sort(entrs, (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));
                entry = entrs.get(1);
            }

            option.add(entry.getKey());
            //update lastVisitedNode
            lastState = entry.getKey().getSourceVertex();
            //increments the reward according to the discount factor
            qValue += entry.getValue() * Math.pow(GAMMA, cont);
            if (!entry.getKey().getTargetVertex().equalsIgnoreCase(terminalState)) {
                currentState = entry.getKey().getTargetVertex();
            } else {
                currentState = "";
            }
            cont++;
            //TODO: in the future, this parameter could be changed.
            if (cont > 10) {
                System.err.println("Could not find a route in 1st level MDP." + option);
                System.exit(0);
//                System.out.println(this.mdp.lowLevelMDPs.get(terminalState));
            }
        } while (!currentState.equalsIgnoreCase(""));
        return new AbstractMap.SimpleEntry(option, qValue);

    }

    private Entry<List<AbstractEdge>, Double> getReversePolicyForNeighborhood(Entry<List<AbstractEdge>, Double> entry) {
        List<AbstractEdge> option = entry.getKey();
        List<AbstractEdge> reverseOption = new LinkedList<>();
        for (int i = option.size() - 1; i >= 0; i--) {
            reverseOption.add((AbstractEdge) graph.getEdge(option.get(i).getTargetVertex(), option.get(i).getSourceVertex()));
        }
        return new AbstractMap.SimpleEntry(reverseOption, entry.getValue() * 2);
    }

    /**
     * Selects a random origin node. Here, a origin node is one of the nodes of
     * a neighborhood that is not terminal.
     *
     * @return a String representing a vertex object.
     */
    private String getRandomOrigin() {
        List<String> originNodes = new ArrayList<>(VERTICES_PER_NEIGHBORHOOD.get(CURRENT_NEIGHBORHOOD));
        while (true) {
            int index = new Random().nextInt(originNodes.size());
            String node = originNodes.get(index);

            //PRECISO REMOVER O B1 E B4 DESSA LISTA
            if (!TERMINAL_VERTICES.contains(node)) {
                return node;
            }
        }

    }

    /**
     * Selects a random destination node. Here, a destination node is one of the
     * terminal nodes of a neighborhood.
     *
     * @return a String representing a vertex object.
     */
    private String getRandomDestinationNode() {
        List<String> terminalNodes = new LinkedList<>();
        for (String node : TERMINAL_VERTICES) {
            if (getNeighborhood(node).equalsIgnoreCase(CURRENT_NEIGHBORHOOD)) {
                terminalNodes.add(node);
            }
        }
        int index = new Random().nextInt(terminalNodes.size());
        return terminalNodes.get(index);
    }

    /**
     * Returns the neighborhood of the an vertex.
     *
     * @param vertex
     * @return Identifier of vertex neighborhood.
     */
    private String getNeighborhood(String vertex) {
        return vertex.substring(0, 1);
    }

    @Override
    public double getDeltaQ() {
        return this.mdp.getDeltaQ();
    }

    private void createHighLevelMDP() {

        /**
         * creates a High level MDP
         *
         * @TODO: implement
         */
        //MDP object
        Map highLevelMDP = new ConcurrentHashMap<>();
        /*
         *1) *VALIDADO* - creates options from agent's origin to terminal nodes of agent's origin neighborhood
         */
        Map acts = new ConcurrentHashMap();

        for (String terminalState : TERMINAL_VERTICES) {
            if (getNeighborhood(this.absoluteOrigin).equalsIgnoreCase(getNeighborhood(terminalState))) {
                //creates the option from state to terminalState
                Entry<List<AbstractEdge>, Double> goingOption = getOptimalPolicyForNeighborhood(this.absoluteOrigin, terminalState);
                acts.put(goingOption.getKey(), goingOption.getValue());

                //creates the option from terminalState to state
                Entry outgoingOption = getReversePolicyForNeighborhood(goingOption);
                //the state of the reverse option
                String lastState = goingOption.getKey().get(goingOption.getKey().size() - 1).getTargetVertex();
                Map reverseActions = new ConcurrentHashMap();
                highLevelMDP.put(lastState, reverseActions);
                reverseActions.put(outgoingOption.getKey(), outgoingOption.getValue());
            }
        }
        highLevelMDP.put(this.absoluteOrigin, acts);
        /*
         *2) - creates the options that connects two neighborhoods
         */
        //list of intermediate states
        List<String> intermediateStates = new ArrayList<>();

        //creates the set of pairs state-actions that connects intermediate states to the other neighborhood
        for (String state : TERMINAL_VERTICES) {
            Map actions = new ConcurrentHashMap();
            Set<AbstractEdge> edgs = graph.edgesOf(state);
            for (AbstractEdge edge : edgs) {
                if (edge.getSourceVertex().equalsIgnoreCase(state)
                        && !getNeighborhood(edge.getTargetVertex()).equalsIgnoreCase(getNeighborhood(state))) {
                    List<AbstractEdge> option = new LinkedList<>();
                    option.add(edge);
                    //update intermediate states list
                    intermediateStates.add(edge.getTargetVertex());
                    //Cost fixed by default in the road network
                    Double qValue = -edge.getCost();
                    actions.put(option, qValue);
                }
            }
            highLevelMDP.put(state, actions);
        }

        //states of the neighborhood
        for (String state : intermediateStates) {
            Map actions = new ConcurrentHashMap();
            for (String terminalState : TERMINAL_VERTICES) {
                if (!TERMINAL_VERTICES.contains(state)
                        && getNeighborhood(state).equalsIgnoreCase(getNeighborhood(terminalState))) {
                    //creates the option from state to terminalState
                    Entry<List<AbstractEdge>, Double> goingOption = getOptimalPolicyForNeighborhood(state, terminalState);
                    actions.put(goingOption.getKey(), goingOption.getValue());

                    //creates the option from terminalState to state
                    Entry outgoingOption = getReversePolicyForNeighborhood(goingOption);
                    Map reverseActions;
                    //@TODO: AQUI TEM QUE MEXER. ELE ESTÁ CRIANDO OPTIONS DE VOLTA PARA ESTADOS INVÁLIDOS.
                    String lastState = goingOption.getKey().get(goingOption.getKey().size() - 1).getTargetVertex();
                    if (TERMINAL_VERTICES.contains(lastState)) {
                        if (highLevelMDP.containsKey(lastState)) {
                            reverseActions = (Map) highLevelMDP.get(lastState);
                        } else {
                            reverseActions = new ConcurrentHashMap();
                            highLevelMDP.put(lastState, reverseActions);
                        }
                        reverseActions.put(outgoingOption.getKey(), outgoingOption.getValue());
                    }

                }

            }
            highLevelMDP.put(state, actions);
        }
        this.mdp.setHighLevelMDP(highLevelMDP);
    }

    private static synchronized void setNeighborhood(String neighborhood) {
        CURRENT_NEIGHBORHOOD = neighborhood;
    }

    private static synchronized String pullFromNeighborhoodsQueue() {
        return NEIGHBORHOODS_QUEUE.poll();
    }

    /**
     * methods that are not used anymore. TODO: MUST BE REMOVED IN THE FEATURE;
     *
     */
    private synchronized Map.Entry<AbstractEdge, Double> getExpectedRewardPerState(String state) {
        return Collections.max(this.mdp.getMdp().get(state).entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));
    }

    /**
     * This method should identify automatically the set of terminal nodes
     * present in the network. At this time, we will assume that it will be
     * given by the problem
     *
     * @return
     */
    private Set<String> getTerminalVertices() {
        //Set of actions
        Set<AbstractEdge> edges = graph.edgeSet();
        Set<String> vertices = new HashSet<>();

        //Identifies terminal nodes and creates its low level MDP
        for (AbstractEdge edge : edges) {
            //Check if is a terminal state
            if ((edge.getSourceVertex().charAt(0) != edge.getTargetVertex().charAt(0)
                    && !vertices.contains(edge.getSourceVertex()))) {
                //increments the list of terminal nodes
                vertices.add(edge.getSourceVertex());
            } else {
                boolean isTerminal = true;
                for (Object e : graph.edgesOf(edge.getTargetVertex())) {
                    if (((AbstractEdge) e).getSourceVertex().equalsIgnoreCase(edge.getTargetVertex())) {
                        isTerminal = false;
                    }
                }
                if (isTerminal) {
                    vertices.add(edge.getTargetVertex());
                }
            }
        }
        return vertices;
    }

}
