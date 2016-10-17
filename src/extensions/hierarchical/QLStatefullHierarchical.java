/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions.hierarchical;

import driver.learning.reward.AbstractRewardFunction;
import driver.learning.exploration.EpsilonDecreasing;
import driver.Driver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;
import scenario.network.AbstractEdge;
import scenario.network.StandardEdge;
import simulation.Params;

/**
 * Implementation of Q-Learning node by node for TAP.
 *
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
    public static Set<String> TERMINAL_VERTICES = Collections.synchronizedSet(new HashSet<String>());
    
    public static Queue<String> NEIGHBORHOODS = new PriorityBlockingQueue<>();
    
    private HierarchicalMDP mdp = new HierarchicalMDP();

    /**
     * Learning rate parameter of Q-Learning.
     */
    public static double ALPHA = 0.5;

    /**
     * Discount factor parameter of Q-Learning.
     */
    public static double GAMMA = 0.99;
    
    private final AbstractRewardFunction rewardFunction = new HierarchicalStetefullRewardFunction(graph);

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
    }
    
    @Override
    public void beforeSimulation() {
        //Low level MDPs
        Map<String, Map<String, Map<AbstractEdge, Double>>> lowLevelMDPs = new ConcurrentHashMap<>();
        //High level MDP
        Map highLevelMDP = new ConcurrentHashMap<>();

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
                NEIGHBORHOODS.add(neighborhood);
            }
            VERTICES_PER_NEIGHBORHOOD.get(neighborhood).add(node);
        }
        //Set of actions
        Set<AbstractEdge> edges = graph.edgeSet();

        //Identifies terminal nodes and creates its low level MDP
        for (AbstractEdge edge : edges) {
            //Check if is a terminal state
            if ((edge.getSourceVertex().charAt(0) != edge.getTargetVertex().charAt(0)
                    && !TERMINAL_VERTICES.contains(edge.getSourceVertex()))) {
                //increments the list of terminal nodes
                TERMINAL_VERTICES.add(edge.getSourceVertex());
            } else {
                boolean isTerminal = true;
                for (Object e : graph.edgesOf(edge.getTargetVertex())) {
                    if (((AbstractEdge) e).getSourceVertex().equalsIgnoreCase(edge.getTargetVertex())) {
                        isTerminal = false;
                    }
                }
                if (isTerminal) {
                    TERMINAL_VERTICES.add(edge.getTargetVertex());
                }
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
                        actions.put(edg, 0.0);
                    }
                }
                states.put(vertex, actions);
            }
            lowLevelMDPs.put(terminalState, states);
        }
        
        this.mdp.setLowLevelMDPs(lowLevelMDPs);

        /**
         * creates a High level MDP
         *
         * @TODO: implement
         */
        //creates the set of states that connects the states of a neighborhood to the terminal states of the neighborhood
//        Map states = new ConcurrentHashMap<>();
        for (String neighborhood : NEIGHBORHOODS) {
            for (String state : VERTICES_PER_NEIGHBORHOOD.get(neighborhood)) {
                Map actions = new ConcurrentHashMap();
                for (String terminalStateOnNeighborhood : TERMINAL_VERTICES) {
                    if (!TERMINAL_VERTICES.contains(state)
                            && getNeighborhood(state).equalsIgnoreCase(terminalStateOnNeighborhood)) {
                        //May I should use some datastructure provided by JGrapht to represent a path
                        List<AbstractEdge> option = new LinkedList<>();
                        //THIS SHOULD BE CHANGED
                        option.add(new StandardEdge(null));
                        actions.put(option, 0.0);
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
                if (edge.getSourceVertex().equalsIgnoreCase(state)) {
                    List<AbstractEdge> option = new LinkedList<>();
                    option.add(edge);
                    actions.put(option, 0.0);
                }
            }
            highLevelMDP.put(state, actions);
        }
        
        this.mdp.setHighLevelMDP(highLevelMDP);

        /**
         * @TODO: IMPLEMENT CLONING FOR SPEEDING UP THE MDP CREATION PROCESS.
         */
    }
    
    @Override
    public void afterSimulation() {
    }
    
    public static String CURRENT_NEIGHBORHOOD = null;
    
    @Override
    public synchronized void beforeEpisode() {
        //Certifies that the agents will learn first on neighborhood A
        beforeEpisodeMDPsUpdate();
        
        this.currentVertex = getRandomOrigin();
        
        this.destination = getRandomDestinationNode();
        if (this.currentVertex.equalsIgnoreCase("B1") && this.destination.equalsIgnoreCase("B4")
                || this.currentVertex.equalsIgnoreCase("B3") && this.destination.equalsIgnoreCase("B6")) {
//            System.out.println("origin: " + currentVertex + "; destino: " + destination);
        }
        this.mdp.setMdp(mdp.lowLevelMDPs.get(this.destination));
        this.currentEdge = null;
        this.travelTime = 0;
        this.route = new LinkedList<>();
    }
    
    @Override
    public synchronized void afterEpisode() {
        afterEpisodeMDPsUpdate();
    }
    
    static synchronized void afterEpisodeMDPsUpdate() {
        if (Params.CURRENT_EPISODE == Params.MAX_EPISODES - 1
                && (NEIGHBORHOODS.size() > 0)) {
            CURRENT_NEIGHBORHOOD = NEIGHBORHOODS.poll();
            System.out.println("Learning neighborhood " + CURRENT_NEIGHBORHOOD);
            Params.CURRENT_EPISODE = 0;
        }
    }
    
    static synchronized void beforeEpisodeMDPsUpdate() {
        if (CURRENT_NEIGHBORHOOD == null) {
            CURRENT_NEIGHBORHOOD = NEIGHBORHOODS.poll();
            System.out.println("Learning neighborhood " + CURRENT_NEIGHBORHOOD);
        }
    }
    
    @Override
    public void beforeStep() {
        currentEdge = mdp.getAction(mdp.getMdp().get(currentVertex));
        this.route.add(currentEdge);
        this.currentVertex = currentEdge.getTargetVertex();
        
    }
    
    @Override
    public void afterStep() {
        this.travelTime += currentEdge.getCost();

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
    }
    
    @Override
    public void resetAll() {
        for (String action : this.mdp.getMdp().keySet()) {
            for (AbstractEdge e : this.mdp.getMdp().get(action).keySet()) {
                this.mdp.getMdp().get(action).put(e, 0.0);
            }
        }
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
    public String getRandomOrigin() {
        List<String> originNodes = new ArrayList<>(VERTICES_PER_NEIGHBORHOOD.get(CURRENT_NEIGHBORHOOD));
        while (true) {
            int index = new Random().nextInt(originNodes.size());
            String node = originNodes.get(index);
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
    public String getRandomDestinationNode() {
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
    public String getNeighborhood(String vertex) {
        return vertex.substring(0, 1);
    }
    
    public synchronized Map.Entry<AbstractEdge, Double> getExpectedRewardPerState(String state) {
        return Collections.max(this.mdp.getMdp().get(state).entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));
    }
}
