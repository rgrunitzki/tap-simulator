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
package driver.learning;

import driver.learning.mdp.StatefullMDP;
import driver.learning.reward.StatefullRewardFunction;
import driver.learning.reward.AbstractRewardFunction;
import driver.learning.exploration.EpsilonDecreasing;
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
import scenario.network.AbstractEdge;

/**
 * Implementation of Q-Learning node by node for TAP.
 *
 * @author Ricardo Grunitzki
 */
public class QLStatefull extends Driver<QLStatefull, List<AbstractEdge>> {

    private StatefullMDP mdp = new StatefullMDP();

    /**
     * Learning rate parameter of Q-Learning. The default value is {@code 0.5}.
     */
    public static double ALPHA = 0.5;

    /**
     * Discount factor parameter of Q-Learning. The default value is
     * {@code 0.99}.
     */
    public static double GAMMA = 0.99;

    /**
     * Reward function
     */
    private AbstractRewardFunction rewardFunction = new StatefullRewardFunction(graph);

    /**
     * Creates an QLStatefull driver according to its specifications.
     *
     * @param id          Driver identifier
     * @param origin      Origin node
     * @param destination Destination node
     * @param graph       Road network
     */
    public QLStatefull(int id, String origin, String destination, Graph graph) {
        super(id, origin, destination, graph);
    }

    @Override
    public void beforeSimulation() {

        if (StatefullMDP.staticMdp == null) {
            @SuppressWarnings("unchecked")
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
            StatefullMDP.staticMdp.setMdp(states);
            try {
                this.mdp = (StatefullMDP) StatefullMDP.staticMdp.getClone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(QLStatefull.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                this.mdp = (StatefullMDP) StatefullMDP.staticMdp.getClone();
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
        this.mdp.resetDetaQ();
    }

    @Override
    public void afterEpisode() {
        this.learningEffort += this.route.size();
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

    @Override
    public double getDeltaQ() {
        return this.mdp.getDeltaQ();
    }

    public AbstractRewardFunction getRewardFunction() {
        return rewardFunction;
    }

    public void setRewardFunction(AbstractRewardFunction rewardFunction) {
        this.rewardFunction = rewardFunction;
    }

}
