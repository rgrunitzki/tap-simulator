/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

/**
 * *
 * @author Ricardo Grunitzki
 */
public class QLStatefullHierarchical extends Driver<QLStatefullHierarchical, List<AbstractEdge>> {

    /**
     * Contains the available nodes/states per Neighborhood.
     */
    public static Map<String, Set<String>> VERTICES_PER_NEIGHBORHOOD = new ConcurrentHashMap<>();

    /**
     * List of terminal nodes.
     */
    public static Set<String> TERMINAL_VERTICES;

    public static Set<String> TERMINAL_VERTICES_COPY;

    /**
     * Collection of existing neighborhood on network.
     */
    public static Queue<String> NEIGHBORHOODS_QUEUE = new PriorityBlockingQueue<>();

    public static Queue<String> NEIGHBORHOODS_QUEUE_COPY = new PriorityBlockingQueue<>();

    /**
     * Markov decision process.
     */
    private HierarchicalMDP mdp = new HierarchicalMDP();

    /**
     * Learning rate parameter of Q-Learning.
     */
    public static double ALPHA = 0.5;

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
    public static String CURRENT_NEIGHBORHOOD = null;

    /**
     * Flag indicating in which level the agent is learning.
     */
    static boolean FIRST_LEVEL = true;
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

    @Override
    public void beforeSimulation() {
        if (NEIGHBORHOODS_QUEUE.isEmpty() && !NEIGHBORHOODS_QUEUE_COPY.isEmpty()) {
            NEIGHBORHOODS_QUEUE.addAll(NEIGHBORHOODS_QUEUE_COPY);
        }
        TERMINAL_VERTICES = Collections.synchronizedSet(TERMINAL_VERTICES_COPY);
        //Low level MDPs
        Map<String, Map<String, Map<AbstractEdge, Double>>> lowLevelMDPs = new ConcurrentHashMap<>();
        /*
         *
         * Creates LowLevelMDPs
         *
         */
        //Set of states
        Set<String> nodes = graph.vertexSet();
        //identify neighborhoods
        for (String node : nodes) {
            //Neighborhood name
            String neighborhood = node.substring(0, 1);
            if (!VERTICES_PER_NEIGHBORHOOD.containsKey(neighborhood)) {
                VERTICES_PER_NEIGHBORHOOD.put(neighborhood, new HashSet<>());
                NEIGHBORHOODS_QUEUE.add(neighborhood);
                NEIGHBORHOODS_QUEUE_COPY.add(neighborhood);
            }
            VERTICES_PER_NEIGHBORHOOD.get(neighborhood).add(node);
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
    public void afterSimulation() {
//        System.out.println(id + "\t" + this.route);
    }

    @Override
    public synchronized void beforeEpisode() {
        //Certifies that the agents will learn first on neighborhood A
        beforeEpisodeMDPsUpdate();
        //In first level MDPs, generates several OD-MATRIX
        if (FIRST_LEVEL) {
            //random origin (nonterminal vertex) from current neighborhood

            if (CURRENT_NEIGHBORHOOD.equalsIgnoreCase("B")) {
                boolean bol = true;
            }
            //random destination (terminal vertex) from the current neighborhood
            this.destination = getRandomDestinationNode();
            this.currentVertex = getRandomOrigin();

            //sets the correct MDP
            this.mdp.setMdp(mdp.lowLevelMDPs.get(this.destination));
            this.currentEdge = null;
            this.travelTime = 0;
            this.route = new LinkedList<>();
        } else {
            //I have to update the second level MDP with the knowledge acquiride in the first level mdps
            if (Params.CURRENT_EPISODE == 1) {
                /**
                 * creates a High level MDP
                 *
                 * @TODO: implement
                 */
                //creates the set of states that connects the states of a neighborhood to the terminal states of the neighborhood
                Map highLevelMDP = new ConcurrentHashMap<>();
                for (String neighborhood : VERTICES_PER_NEIGHBORHOOD.keySet()) {
                    for (String state : VERTICES_PER_NEIGHBORHOOD.get(neighborhood)) {
                        Map actions = new ConcurrentHashMap();
                        for (String terminalState : TERMINAL_VERTICES) {
                            if (!TERMINAL_VERTICES.contains(state)
                                    && getNeighborhood(state).equalsIgnoreCase(getNeighborhood(terminalState))) {
                                Entry e = getOptimalPolicyForNeighborhood(state, terminalState);
                                actions.put(e.getKey(), e.getValue());
//                                System.out.println(state + "-" + terminalState + "\t" + e);
                            }

                        }
                        highLevelMDP.put(state, actions);
                    }
                }

                //creates the set of pairs state-actions that connects intermediate states to the other neighborhood
                for (String state : TERMINAL_VERTICES) {
                    Map actions = new ConcurrentHashMap();
                    Set<AbstractEdge> edgs = graph.edgesOf(state);
                    for (AbstractEdge edge : edgs) {
                        if (edge.getSourceVertex().equalsIgnoreCase(state)
                                && !getNeighborhood(edge.getTargetVertex()).equalsIgnoreCase(getNeighborhood(state))) {
                            List<AbstractEdge> option = new LinkedList<>();
                            option.add(edge);
                            //Cost fixed by default in the road network
                            Double qValue = -edge.getCost();
                            actions.put(option, qValue);
                        }
                    }
                    highLevelMDP.put(state, actions);
                }

                this.mdp.setHighLevelMDP(highLevelMDP);

//                //prints mdp
//                synchronized (this) {
//                    String output = "";
//                    for (int i = 1; i <= 7; i++) {
//                        String state = "A" + i;
//                        output += ("\n" + id + ") " + state + ": " + this.mdp.highLevelMDP.get(state).toString());
//                    }
//                    System.out.println("\n" + output);
//                }
//                //
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
     * Computes the options of each low level mdp.
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
        if (id == 1) {
//            System.out.print("\n"+initialState + "-"+terminalState+ "\t-->\t");
        }
        do {
            entry = Collections.max(this.mdp.lowLevelMDPs.get(terminalState).get(currentState).entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));
            option.add(entry.getKey());
            qValue += entry.getValue();
            if (!entry.getKey().getTargetVertex().equalsIgnoreCase(terminalState)) {
                currentState = entry.getKey().getTargetVertex();
            } else {
                currentState = "";
            }
            if (id == 1) {
//                System.out.print(entry.getKey() + " ");
            }

            if (option.size() > 8) {
                if (id == 1) {
//                    System.err.println("option muito grande!, terminalStateOnNeighborhood: " + terminalState);
//                    System.out.println();
                }
//                System.exit(0);
                break;
            }
        } while (!currentState.equalsIgnoreCase(""));
        return new AbstractMap.SimpleEntry(option, qValue);

    }

    private Entry<List<AbstractEdge>, Double> generateOption(String initialState, String terminalStateOnNeighborhood) {
        LinkedList<AbstractEdge> option = new LinkedList<>();
        String currentState = initialState;
        Double qValue = 0.0;
        Entry<AbstractEdge, Double> entry;
        do {

            LinkedList<Entry<AbstractEdge, Double>> actions = new LinkedList<>(this.mdp.lowLevelMDPs.get(terminalStateOnNeighborhood).get(currentState).entrySet());
            Collections.sort(actions, (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));
            if (!option.isEmpty() && option.getLast().getSourceVertex().equals(actions.getFirst().getKey().getTargetVertex())) {
                System.err.println("we have a problem");
                actions.pollLast();
                option.add(actions.pollLast().getKey());
            }
            option.add(actions.get(0).getKey());
            System.out.println("action " + option.getLast().getName());

            currentState = option.getLast().getTargetVertex();

        } while (!option.getLast().getTargetVertex().equalsIgnoreCase(terminalStateOnNeighborhood));

        return new AbstractMap.SimpleEntry(option, qValue);

    }

    @Override
    public synchronized void afterEpisode() {
        afterEpisodeMDPsUpdate();
    }

    static synchronized void afterEpisodeMDPsUpdate() {
        //should I change the MDP?
        if (Params.CURRENT_EPISODE == Params.MAX_EPISODES - 1) {
            //which level
            if (NEIGHBORHOODS_QUEUE.size() > 0) {
                //first level
                FIRST_LEVEL = true;
                CURRENT_NEIGHBORHOOD = NEIGHBORHOODS_QUEUE.poll();
                if (Params.PRINT_ON_TERMINAL) {
                    System.out.println("");
                    System.out.println("Learning first level mdp " + CURRENT_NEIGHBORHOOD);
                }
                Params.CURRENT_EPISODE = 1;
            } else if (!CURRENT_NEIGHBORHOOD.equals("Z")) {
                //second level
                FIRST_LEVEL = false;
                CURRENT_NEIGHBORHOOD = "Z";
                if (Params.PRINT_ON_TERMINAL) {
                    System.out.println("");
                    System.out.println("Learning sencond level MDP " + CURRENT_NEIGHBORHOOD);
                }
                Params.CURRENT_EPISODE = 0;
                Params.MAX_EPISODES = 50;
                EpsilonDecreasing.EPSILON_DECAY = 0.5f;
            } else {
//                System.out.println("Agent finished ");
            }
        }
    }

    static synchronized void beforeEpisodeMDPsUpdate() {
        if (CURRENT_NEIGHBORHOOD == null) {
            CURRENT_NEIGHBORHOOD = NEIGHBORHOODS_QUEUE.poll();
            if (Params.PRINT_ON_TERMINAL) {
                System.out.println("Learning neighborhood " + CURRENT_NEIGHBORHOOD);
            }
        }
    }

    @Override
    public void beforeStep() {
        if (FIRST_LEVEL) {
            currentEdge = mdp.getAction(mdp.getMdp().get(currentVertex));
            this.route.add(currentEdge);
            this.currentVertex = currentEdge.getTargetVertex();
        } else {

            //verifies if the agent is performing an action
            if ((secondLevelAction == null || secondLevelAction.size() == secondaryActionInternalIndex)) {
                //case not, choose a new one using e-decreasing strategy   
                //I have to implement e-greedy
                try {
                    if (currentVertex.equals("B7") || currentVertex.equals("B9")) {
//                        System.out.println("mdp actions: " + mdp.highLevelMDP.get(currentVertex));
                    }

                    //verifies if the agents is choosing an option that leads it to its destination
                    if (getNeighborhood(absoluteDestination).equals(getNeighborhood(currentVertex))) {
                        //remove other terminal nodes
                        for (List<AbstractEdge> opt : mdp.highLevelMDP.get(currentVertex).keySet()) {
                            if (opt.get(opt.size() - 1).getTargetVertex().equalsIgnoreCase(absoluteDestination)) {
                                secondLevelAction = (LinkedList< AbstractEdge>) opt;
                                break;
                            }
                        }
                    } else {
                        float random = Params.RANDOM.nextFloat();
                        double epsilon = 1 * Math.pow(EPSILON_DECAY, Params.CURRENT_EPISODE);
                        if (random <= epsilon) {
                            List options = new ArrayList<>(mdp.highLevelMDP.get(currentVertex).keySet());
                            Collections.shuffle(options, Params.RANDOM);
                            secondLevelAction = (LinkedList<AbstractEdge>) options.get(0);
                        } else {
                            secondLevelAction = (LinkedList<AbstractEdge>) Collections.max(mdp.highLevelMDP.get(currentVertex).entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue())).getKey();
                        }
                    }
//                    System.out.println("mdp actions: " + mdp.highLevelMDP.get(currentVertex));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

//                System.out.println("choosed action: " + secondLevelAction);
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
    public void afterStep() {
        this.travelTime += currentEdge.getCost();

        if (FIRST_LEVEL) {
            //update q-table
            double qa = this.mdp.getValue(currentEdge);
            double r = this.rewardFunction.getReward(this);

            double maxQa = 0.0;

            if (!currentEdge.getTargetVertex().equals(destination)
                    && !this.mdp.getMdp().get(currentEdge.getTargetVertex()).keySet().isEmpty()) {
                Map<AbstractEdge, Double> mdp2 = this.mdp.getMdp().get(currentEdge.getTargetVertex());
                maxQa = Collections.max(mdp2.entrySet(), (entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).getValue();
            }
            qa = (1 - ALPHA) * qa + ALPHA * (r + GAMMA * maxQa);

            this.mdp.setValue(currentEdge, qa);
        } else {
            secondaryReward += this.rewardFunction.getReward(this);
            if (secondLevelAction.size() == secondaryActionInternalIndex) {
                //update qvalue
                double qa = this.mdp.highLevelMDP.get(this.currentSecondaryState).get(secondLevelAction);
                double r = secondaryReward;
                double maxQa = 0.0;

                if (!this.currentEdge.getTargetVertex().equals(this.absoluteDestination)
                        && !this.mdp.highLevelMDP.get(this.currentEdge.getTargetVertex()).keySet().isEmpty()) {
                    Map<List<AbstractEdge>, Double> mdp2 = this.mdp.highLevelMDP.get(this.currentEdge.getTargetVertex());
                    maxQa = Collections.max(mdp2.entrySet(), (entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).getValue();
                }
                qa = (1 - ALPHA) * qa + ALPHA * (r + GAMMA * maxQa);

                this.mdp.highLevelMDP.get(this.currentSecondaryState).put(secondLevelAction, qa);
                secondaryReward = 0.0;
            }

        }
    }

    @Override
    public void resetAll() {
        this.mdp = new HierarchicalMDP();
        FIRST_LEVEL = true;
        this.currentSecondaryState = "";
        this.secondLevelAction = null;
        this.secondaryActionInternalIndex = 0;
        this.secondaryReward = 0;
        CURRENT_NEIGHBORHOOD = null;
        //reset low level mdps
//        for (String key : mdp.lowLevelMDPs.keySet()) {
////            System.out.println("MDP: " + key);
//            for (String state : mdp.lowLevelMDPs.get(key).keySet()) {
////                System.out.println(mdp.lowLevelMDPs.get(key).get(state).keySet());
//                for (AbstractEdge action : mdp.lowLevelMDPs.get(key).get(state).keySet()) {
////                    System.out.println(action);                    
//                }
//            }
//        }
//        //remove high level mdp
//        mdp.highLevelMDP = null;
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

    /**
     *
     *
     *
     *
     * NEW METHODS FOR HIERALQUICAL QL
     *
     *
     *
     *
     */
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

    public synchronized Map.Entry<AbstractEdge, Double> getExpectedRewardPerState(String state) {
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
