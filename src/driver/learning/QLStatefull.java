/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import driver.Driver;
import java.util.ArrayList;
import java.util.Collections;
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
public class QLStatefull extends Driver<QLStatefull, List<AbstractEdge>> {

    private StatefullMDP mdp = new StatefullMDP();

//    public static StatefullMDP staticMdp;
    public static double ALPHA = 0.5;
    public static double GAMMA = 0.99;

    private final AbstractRewardFunction rewardFunction = new StatefullRewardFunction(graph);
//    private final AbstractRewardFunction rewardFunction = new StatefullRewardFunctionATT(graph);

    public QLStatefull(int id, String origin, String destination, Graph graph) {
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
                Logger.getLogger(QLStatefull.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                this.mdp = (StatefullMDP) StatefullMDP.staticMdp.clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(QLStatefull.class.getName()).log(Level.SEVERE, null, ex);
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
        this.travelTime = 0;
        this.route = new LinkedList<>();
    }

    @Override
    public void afterEpisode() {
    }

    @Override
    public void beforeStep() {
        currentEdge = mdp.getAction(mdp.mdp.get(currentVertex));
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
                &&!this.mdp.mdp.get(currentEdge.getTargetVertex()).keySet().isEmpty()) {
            Map<AbstractEdge, Double> mdp2 = this.mdp.mdp.get(currentEdge.getTargetVertex());
            maxQa = Collections.max(mdp2.entrySet(), (entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).getValue();
        }
        qa = (1 - ALPHA) * qa + ALPHA * (r + GAMMA * maxQa);

        this.mdp.setValue(currentEdge, qa);
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
        list.add(Pair.of("alpha", QLStatefull.ALPHA));
        list.add(Pair.of("gamma", QLStatefull.GAMMA));
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
