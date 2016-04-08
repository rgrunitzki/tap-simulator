/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import driver.Driver;
import extensions.c2i.EdgeC2I;
import extensions.c2i.InformationType;
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
import org.jgrapht.GraphPath;
import scenario.AbstractEdge;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class QLStatefullC2I extends Driver<QLStatefullC2I, List<AbstractEdge>> {

//    private StatefullC2IMDP mdp = new StatefullC2IMDP();
    private StatefullMDP mdp = new StatefullMDP();

    public static double ALPHA = 0.5;
    public static double GAMMA = 0.99;
    public static InformationType INFORMATION_TYPE = InformationType.None;
    public AbstractEdge previousEdge = null;

    private final AbstractRewardFunction rewardFunction = new StatefullRewardFunctionC2I(graph);

    public QLStatefullC2I(int id, String origin, String destination, Graph graph) {
        super(id, origin, destination, graph);
    }

    public static Map<String, StatefullMDP> initializedMDPPerOD = new ConcurrentHashMap<>();

    @Override
    public void beforeSimulation() {

        if (initializedMDPPerOD.get(origin + "-" + destination) == null) {
//        if (StatefullMDP.staticMdp == null) {
            Set<String> vertices = graph.vertexSet();

            Map states = new ConcurrentHashMap<>();
            for (String vertex : vertices) {
                Map actions = new ConcurrentHashMap();
                Set<AbstractEdge> edges = graph.edgesOf(vertex);
                for (AbstractEdge edge : edges) {
                    if (edge.getSourceVertex().equalsIgnoreCase(vertex)) {
                        Double qvalue = EdgeC2I.getPath(edge.getTargetVertex(), destination, graph);
                        actions.put(edge, qvalue);
                    }
                }
                states.put(vertex, actions);
            }

            StatefullMDP mdpForOD = new StatefullMDP();
            mdpForOD.mdp = states;
            initializedMDPPerOD.put(origin + "-" + destination, mdpForOD);

//            StatefullMDP.staticMdp = new StatefullMDP();
//            StatefullMDP.staticMdp.mdp = states;
            try {
//                this.mdp = (StatefullMDP) StatefullMDP.staticMdp.clone();
                this.mdp = (StatefullMDP) initializedMDPPerOD.get(origin + "-" + destination).clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(QLStatefullC2I.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                this.mdp = (StatefullMDP) initializedMDPPerOD.get(origin + "-" + destination).clone();
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
        EdgeC2I.beforeEpisode(graph);

        this.previousEdge = null;
        this.currentVertex = origin;
        this.currentEdge = null;
        this.travelTime = 0;
        this.route = new LinkedList<>();
    }

    @Override
    public void afterEpisode() {
        EdgeC2I.afterEpisode(graph);
    }

    @Override
    public void beforeStep() {
//        /*

         //ESSAS LINHAS FORAM APAGADAS PARA RESPONDER AOS QUESTIONAMENTOS DA ANA
        //Update last experienced edge;
        previousEdge = currentEdge;
        //random number
        double random = Params.RANDOM.nextDouble();
        //current epsilon
        double decay = Math.pow(Params.E_DECAY_RATE, Params.CURRENT_EPISODE);
         //        double epsilon = 0.5 - Params.CURRENT_EPISODE * 0.001;

         //e-greedy policy
        //        if (random <= 0.2 * epsilon) {
        if (random <= Params.EPSILON * decay) {

            //exploration
            List<AbstractEdge> actions = new ArrayList<>(mdp.mdp.get(currentVertex).keySet());
            Collections.shuffle(actions, Params.RANDOM);
            currentEdge = actions.get(0);
        } else {

            GraphPath c2iPath;
            synchronized (EdgeC2I.SHORTEST_PATHS) {
                EdgeC2I.C2I_WEIGHT = true;
                c2iPath = EdgeC2I.SHORTEST_PATHS.getShortestPath(currentVertex, destination);
                EdgeC2I.C2I_WEIGHT = false;
            }

            double newQa = EdgeC2I.PATHS_WEIGHT.get(currentVertex + "-" + destination);

            //            if (newQa > (mdp.mdp.get(currentVertex).get((AbstractEdge) c2iPath.getEdgeList().get(0)))) {
            if ((Params.RANDOM.nextDouble() > 0.7)//){
                    && newQa > (mdp.mdp.get(currentVertex).get((AbstractEdge) c2iPath.getEdgeList().get(0)))
                    && Params.CURRENT_EPISODE < Params.EPISODES / 2) {
                mdp.mdp.get(currentVertex).put((AbstractEdge) c2iPath.getEdgeList().get(0), newQa);
            }

            //exploitation
            currentEdge = Collections.max(mdp.mdp.get(currentVertex).entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue())).getKey();

        }

//         */
//        //Select the next action
//        currentEdge = mdp.getAction(mdp.mdp.get(currentVertex));
//        //use the C2I information to get the best action
//        if (currentEdge == null) {
//            synchronized (EdgeC2I.SHORTEST_PATHS) {
//                currentEdge = (AbstractEdge) EdgeC2I.SHORTEST_PATHS.getShortestPath(currentVertex, destination).getEdgeList().get(0);
//            }
//        }
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
        double r = this.rewardFunction.getStandardReward(this);

        double maxQa = 0.0;

        if (!currentEdge.getTargetVertex().equals(destination)
                && !this.mdp.mdp.get(currentEdge.getTargetVertex()).keySet().isEmpty()) {
            Map<AbstractEdge, Double> mdp2 = this.mdp.mdp.get(currentEdge.getTargetVertex());
            maxQa = Collections.max(mdp2.entrySet(), (entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).getValue();
        }

        //update q-value
        qa = (1 - ALPHA) * qa + ALPHA * (r + GAMMA * maxQa);

        //new qvalue entry
        //QValueC2I qvalue = new QValueC2I(qa, r, Params.CURRENT_EPISODE, true);
        //set qvalue entry  
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
        list.add(Pair.of("epsilon-decay", Params.E_DECAY_RATE));
        list.add(Pair.of("epsilon", Params.EPSILON));
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

    public StatefullMDP getMdp() {
        return mdp;
    }

    public void setMdp(StatefullMDP mdp) {
        this.mdp = mdp;
    }

    public AbstractEdge getPreviousEdge() {
        return previousEdge;
    }

    @Override
    public AbstractEdge getCurrentEdge() {
        try {
            return super.getCurrentEdge();
        } catch (Exception e) {
            return null;
        }
    }
}
