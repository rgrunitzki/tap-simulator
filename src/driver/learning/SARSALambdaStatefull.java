/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import driver.learning.mdp.StatefullMDP;
import driver.learning.reward.StatefullRewardFunction;
import driver.learning.reward.AbstractRewardFunction;
import driver.learning.exploration.EpsilonDecreasing;
import driver.Driver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;
import scenario.network.AbstractEdge;
import simulation.Params;

/**
 * Implementation of SARSA node by node for TAP.
 *
 * @author Ricardo Grunitzki
 */
public class SARSALambdaStatefull extends Driver<SARSALambdaStatefull, List<AbstractEdge>> {

    private StatefullMDP mdp = new StatefullMDP();

    /**
     * Learning rate parameter of SARSA algorithm.
     */
    public static double ALPHA = 0.5;

    /**
     * Discount rate parameter of SARSA algorithm.
     */
    public static double GAMMA = 0.99;

    public static double LAMBDA = 0.9;

    private Map<String, Map<AbstractEdge, Double>> zTable;

    private final AbstractRewardFunction rewardFunction = new StatefullRewardFunction(graph);

    /**
     * Creates an SARSAStatefull driver according to its specifications.
     *
     * @param id Driver identifier
     * @param origin Origin node
     * @param destination Destination node
     * @param graph Road network
     */
    public SARSALambdaStatefull(int id, String origin, String destination, Graph graph) {
        super(id, origin, destination, graph);
    }

    @Override
    public void beforeSimulation() {

        if (StatefullMDP.staticMdp == null) {
            Set<String> vertices = graph.vertexSet();
            //Q-table
            Map states = new ConcurrentHashMap();
            //Z-table
            Map zstates = new ConcurrentHashMap();
            for (String vertex : vertices) {
                //Q-values
                Map actions = new ConcurrentHashMap();
                //Z-values
                Map zactions = new ConcurrentHashMap();
                Set<AbstractEdge> edges = graph.edgesOf(vertex);
                for (AbstractEdge edge : edges) {
                    if (edge.getSourceVertex().equalsIgnoreCase(vertex)) {
                        if (edge.getTargetVertex().equalsIgnoreCase(this.destination)) {
                            actions.put(edge, 0.0);
                        } else {
                            actions.put(edge, -Params.RANDOM.nextDouble());
                        }
                        zactions.put(edge, 0.);
                    }
                }

                states.put(vertex, actions);
                zstates.put(vertex, zactions);
            }

            StatefullMDP.staticMdp = new StatefullMDP();
            StatefullMDP.staticMdp.setMdp(states);
            StatefullMDP.staticMdp.setzTable(zstates);

            try {
                this.mdp = (StatefullMDP) StatefullMDP.staticMdp.getClone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(SARSALambdaStatefull.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                this.mdp = (StatefullMDP) StatefullMDP.staticMdp.getClone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(SARSALambdaStatefull.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void afterSimulation() {
    }

    @Override
    public void beforeEpisode() {
        this.currentEdge = null;
        this.travelTime = 0;
        this.route = new LinkedList<>();

        //Choose S
        this.currentVertex = origin;
        //Choose A
        this.currentEdge = mdp.getAction(mdp.getMdp().get(currentVertex));
        //Z(s, a) = 0, for all s ∈ S, a ∈ A(s)
        for (String s : this.mdp.getzTable().keySet()) {
            for (AbstractEdge a : this.mdp.getzTable().get(s).keySet()) {
                this.mdp.getzTable().get(s).put(a, 0.);
            }
        }
    }

    @Override
    public void afterEpisode() {
    }

    @Override
    public void beforeStep() {
        //Take action A
    }

    @Override
    public void afterStep() {
        //Observe R
        double r = this.rewardFunction.getReward(this);
        this.travelTime += currentEdge.getCost();
        this.route.add(currentEdge);

        //Observe S'       
        String nextVertex = currentEdge.getTargetVertex();
        AbstractEdge nextEdge;
        double q_si_ai;

        //Take action A' from S' using exploration-exploitation strategy
        nextEdge = mdp.getAction(mdp.getMdp().get(nextVertex));
        if (currentEdge.getTargetVertex().equals(this.destination)) {
            q_si_ai = 0.;
        } else {
            q_si_ai = this.mdp.getValue(nextEdge);
        }

        //Q(S,A)
        double q_s_a = this.mdp.getValue(currentEdge);
        //δ ←R+γQ(S',A')−Q(S,A)
        double delta = r + GAMMA * q_si_ai - q_s_a;
        //Z(S,A)
        Double z_s_a = this.mdp.getzTable().get(currentVertex).get(currentEdge);
        //Z(S,A)←Z(S,A)+1
        this.mdp.getzTable().get(currentVertex).put(currentEdge, z_s_a + 1.);
        //For all s ∈ S, a ∈ A(s):
        for (String s : this.mdp.getMdp().keySet()) {
            for (AbstractEdge a : this.mdp.getMdp().get(s).keySet()) {
                //Q(s, a)←Q(s, a)+αδZ(s, a)
                this.mdp.setValue(a,
                        this.mdp.getValue(a) + 
                                ALPHA * delta * this.mdp.getzTable().get(s).get(a));
                //Z(s, a)←γλZ(s, a)
                this.mdp.getzTable().get(s).put(a,
                        GAMMA * LAMBDA * this.mdp.getzTable().get(s).get(a));
            }
        }
        //S ←S'
        currentEdge = nextEdge;
        // A←A';
        currentVertex = nextVertex;
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
        return ((this.currentEdge != null) && (this.currentVertex.equals(destination) && this.stepA));
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
        list.add(Pair.of("alpha", SARSALambdaStatefull.ALPHA));
        list.add(Pair.of("gamma", SARSALambdaStatefull.GAMMA));
        list.add(Pair.of("lambda", SARSALambdaStatefull.LAMBDA));
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

}
