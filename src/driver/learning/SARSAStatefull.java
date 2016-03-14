/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

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
import scenario.AbstractEdge;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class SARSAStatefull extends Driver<SARSAStatefull, List<AbstractEdge>> {

    private StatefullMDP mdp = new StatefullMDP();

//    public static StatefullMDP staticMdp;
    public static double ALPHA = 0.5;
    public static double GAMMA = 0.99;
    private AbstractEdge nextEdge = null;
    private final AbstractRewardFunction rewardFunction = new StatefullRewardFunction(graph);
//    private final AbstractRewardFunction rewardFunction = new StatefullRewardFunctionATT(graph);

    public SARSAStatefull(int id, String origin, String destination, Graph graph) {
        super(id, origin, destination, graph);
    }

    @Override
    public void beforeSimulation() {

        if (StatefullMDP.staticMdp == null) {
            Set<String> vertices = graph.vertexSet();

            Map states = new ConcurrentHashMap<>();
            for (String vertex : vertices) {
                Map actions = new ConcurrentHashMap();
                Set<AbstractEdge> edges = graph.edgesOf(vertex);
                for (AbstractEdge edge : edges) {
                    if (edge.getSourceVertex().equalsIgnoreCase(vertex)) {
                        actions.put(edge, 0.0);
                    }
                }
                states.put(vertex, actions);
            }
            StatefullMDP.staticMdp = new StatefullMDP();
            StatefullMDP.staticMdp.mdp = states;
            try {
                this.mdp = (StatefullMDP) StatefullMDP.staticMdp.clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(SARSAStatefull.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                this.mdp = (StatefullMDP) StatefullMDP.staticMdp.clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(SARSAStatefull.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void afterSimulation() {
    }

    @Override
    public void beforeEpisode() {
        this.currentVertex = origin;
        this.currentEdge = null;
        this.nextEdge = null;
        this.travelTime = 0;
        this.route = new LinkedList<>();
    }

    @Override
    public void afterEpisode() {
    }

    @Override
    public void beforeStep() {
        if (nextEdge == null) {
            currentEdge = mdp.getAction(mdp.mdp.get(currentVertex));
        }else{
            currentEdge = nextEdge;
        }
        this.route.add(currentEdge);
        this.currentVertex = currentEdge.getTargetVertex();
    }

    @Override
    public void afterStep() {
        this.travelTime += currentEdge.getCost();

        //update q-table
        double qa_t0 = this.mdp.getValue(currentEdge);
        double r_t1 = this.rewardFunction.getReward(this);

        double qa_t1 = 0;
        nextEdge = mdp.getAction(mdp.mdp.get(currentVertex));
        
        
        if (nextEdge.getSourceVertex().equals(destination)) {
            qa_t1 = 0.0;
        } else {
            qa_t1 = mdp.getValue(nextEdge);
        }

        qa_t0 = (1 - ALPHA) * qa_t0 + ALPHA * (r_t1 + GAMMA * qa_t1);

        this.mdp.setValue(currentEdge, qa_t0);
    }

    @Override
    public void resetAll() {
        for (String action : this.mdp.mdp.keySet()) {
            for (AbstractEdge e : this.mdp.mdp.get(action).keySet()) {
                this.mdp.mdp.get(action).put(e, 0.0);
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
        list.add(Pair.of("reward", Params.REWARD_FUNCTION.toString()));
        list.add(Pair.of("epsilon", Params.E_DECAY_RATE));
        list.add(Pair.of("alpha", SARSAStatefull.ALPHA));
        list.add(Pair.of("gamma", SARSAStatefull.GAMMA));
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
