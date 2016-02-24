/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import driver.Driver;
import extensions.c2i.EdgeC2I;
import extensions.c2i.InformationType;
import extensions.c2i.QValueC2I;
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
public class QLStatefullC2I extends Driver<QLStatefullC2I, List<AbstractEdge>> {

    private StatefullC2IMDP mdp = new StatefullC2IMDP();

    public static double ALPHA = 0.5;
    public static double GAMMA = 0.99;
    public static InformationType INFORMATION_TYPE = InformationType.None;

    private final AbstractRewardFunction rewardFunction = new StatefullRewardFunction(graph);

    public QLStatefullC2I(int id, String origin, String destination, Graph graph) {
        super(id, origin, destination, graph);
    }

    @Override
    public void beforeSimulation() {

        if (StatefullC2IMDP.staticMdp == null) {
            Set<String> vertices = graph.vertexSet();

            Map states = new ConcurrentHashMap<>();
            for (String vertex : vertices) {
                Map actions = new ConcurrentHashMap();
                Set<AbstractEdge> edges = graph.edgesOf(vertex);
                for (AbstractEdge edge : edges) {
                    if (edge.getSourceVertex().equalsIgnoreCase(vertex)) {
                        QValueC2I qvalue = new QValueC2I();
                        actions.put(edge, qvalue);
                    }
                }
                states.put(vertex, actions);
            }
            StatefullC2IMDP.staticMdp = new StatefullC2IMDP();
            StatefullC2IMDP.staticMdp.mdp = states;
            try {
                this.mdp = (StatefullC2IMDP) StatefullC2IMDP.staticMdp.clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(QLStatefullC2I.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                this.mdp = (StatefullC2IMDP) StatefullC2IMDP.staticMdp.clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(QLStatefullC2I.class.getName()).log(Level.SEVERE, null, ex);
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

        //Communicate to the infrastructure and update the next q-values
        if (QLStatefullC2I.INFORMATION_TYPE != InformationType.None) {
            for (AbstractEdge action : mdp.mdp.get(currentVertex).keySet()) {

                //estimated reward on link
                Double currentValue = mdp.mdp.get(currentVertex).get(action).getReward();

                EdgeC2I edge = (EdgeC2I) action;

                //if the information present on knowledge base is better, update agent's MDP
                if (edge.getInformation(action) != null && edge.getInformation(action).getValue() < currentValue) {
                    //update q-value
                    mdp.mdp.get(currentVertex).get(action).updateByMessage(edge.getInformation(action));
                }
            }
        }

        //Select the next action
        currentEdge = mdp.getAction(currentVertex);
        //update the travaled route
        this.route.add(currentEdge);
        //update the current vertex
        this.currentVertex = currentEdge.getTargetVertex();
    }

    @Override
    public void afterStep() {

        //update the travel time
        this.travelTime += currentEdge.getCost();

        //current q-value
        double qa = this.mdp.getValue(currentEdge);
        //reward
        double r = this.rewardFunction.getReward(this);

        double maxQa = 0.0;
        if (!this.mdp.mdp.get(currentEdge.getTargetVertex()).keySet().isEmpty()) {
            Map<AbstractEdge, QValueC2I> mdp2 = this.mdp.mdp.get(currentEdge.getTargetVertex());
            maxQa = Collections.max(mdp2.entrySet(), (entry1, entry2) -> entry1.getValue().getValue() > entry2.getValue().getValue() ? 1 : -1).getValue().getValue();
        }

        //update q-value
        qa = (1 - ALPHA) * qa + ALPHA * (r + GAMMA * maxQa);

        //new qvalue entry
        QValueC2I qvalue = new QValueC2I(qa, r, Params.CURRENT_EPISODE, true);

        //set qvalue entry  
        this.mdp.setValue(currentEdge, qvalue);

    }

    @Override
    public void resetAll() {
        for (String action : this.mdp.mdp.keySet()) {
            for (AbstractEdge e : this.mdp.mdp.get(action).keySet()) {
                this.mdp.mdp.get(action).put(e, new QValueC2I());
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
        list.add(Pair.of("epsilon", Params.E_DECAY_RATE));
        list.add(Pair.of("alpha", QLStatefullC2I.ALPHA));
        list.add(Pair.of("gamma", QLStatefullC2I.GAMMA));
        list.add(Pair.of("information", QLStatefullC2I.INFORMATION_TYPE.toString()));
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

    public StatefullC2IMDP getMdp() {
        return mdp;
    }

    public void setMdp(StatefullC2IMDP mdp) {
        this.mdp = mdp;
    }

    
}
