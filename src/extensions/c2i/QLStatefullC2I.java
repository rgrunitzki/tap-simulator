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
package extensions.c2i;

import driver.Driver;
import driver.learning.reward.AbstractRewardFunction;
import driver.learning.exploration.EpsilonDecreasing;
import driver.learning.mdp.StatefullMDP;
import driver.learning.reward.StatefullRewardFunction;
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
import scenario.network.AbstractEdge;
import simulation.Params;

/**
 *
 * @author Ricardo Grunitzki
 */
public class QLStatefullC2I extends Driver<QLStatefullC2I, List<AbstractEdge>> {

    private StatefullMDP mdp = new StatefullMDP();

    public static double ALPHA = 0.5;
    public static double GAMMA = 0.99;
    public static double COMMUNICATION_RATE = 0.3;
    public static InformationType INFORMATION_TYPE = InformationType.None;
    public AbstractEdge previousEdge = null;

    private final AbstractRewardFunction rewardFunction = new StatefullRewardFunction(graph);

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
            mdpForOD.setMdp(states);
            initializedMDPPerOD.put(origin + "-" + destination, mdpForOD);

//            StatefullMDP.staticMdp = new StatefullMDP();
//            StatefullMDP.staticMdp.mdp = states;
            try {
//                this.mdp = (StatefullMDP) StatefullMDP.staticMdp.clone();
                this.mdp = (StatefullMDP) initializedMDPPerOD.get(origin + "-" + destination).getClone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(QLStatefullC2I.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                this.mdp = (StatefullMDP) initializedMDPPerOD.get(origin + "-" + destination).getClone();
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
        double decay = Math.pow(EpsilonDecreasing.EPSILON_DECAY, Params.CURRENT_EPISODE);
         //        double epsilon = 0.5 - Params.CURRENT_EPISODE * 0.001;

        //e-greedy policy
        //        if (random <= 0.2 * epsilon) {
        if (random <= EpsilonDecreasing.EPSILON_INITIAL * decay) {

            //exploration
            List<AbstractEdge> actions = new ArrayList<>(mdp.getMdp().get(currentVertex).keySet());
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
            if ((COMMUNICATION_RATE > Params.RANDOM.nextDouble())//){
                    && newQa > (mdp.getMdp().get(currentVertex).get((AbstractEdge) c2iPath.getEdgeList().get(0)))
                    && Params.CURRENT_EPISODE < Params.MAX_EPISODES / 2) {
                mdp.getMdp().get(currentVertex).put((AbstractEdge) c2iPath.getEdgeList().get(0), newQa);
            }

            //exploitation
            currentEdge = Collections.max(mdp.getMdp().get(currentVertex).entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue())).getKey();

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
                && !this.mdp.getMdp().get(currentEdge.getTargetVertex()).keySet().isEmpty()) {
            Map<AbstractEdge, Double> mdp2 = this.mdp.getMdp().get(currentEdge.getTargetVertex());
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
        this.learningEffort = 0;
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
        list.add(Pair.of("alpha", QLStatefullC2I.ALPHA));
        list.add(Pair.of("gamma", QLStatefullC2I.GAMMA));
        list.add(Pair.of("communication-rate", QLStatefullC2I.COMMUNICATION_RATE));
//        list.add(Pair.of("information", QLStatefullC2I.INFORMATION_TYPE.toString()));
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
