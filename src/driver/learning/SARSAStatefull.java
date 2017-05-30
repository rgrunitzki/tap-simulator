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
public class SARSAStatefull extends Driver<SARSAStatefull, List<AbstractEdge>> {

    private StatefullMDP mdp = new StatefullMDP();

    /**
     * Learning rate parameter of SARSA algorithm.
     */
    public static double ALPHA = 0.5;

    /**
     * Discount rate parameter of SARSA algorithm.
     */
    public static double GAMMA = 0.99;

    private final AbstractRewardFunction rewardFunction = new StatefullRewardFunction(graph);

    /**
     * Creates an SARSAStatefull driver according to its specifications.
     *
     * @param id Driver identifier
     * @param origin Origin node
     * @param destination Destination node
     * @param graph Road network
     */
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
                        if (edge.getTargetVertex().equalsIgnoreCase(this.destination)) {
                            actions.put(edge, 0.0);
                        } else {
                            actions.put(edge, -Params.RANDOM.nextDouble());
                        }
                    }
                }
                states.put(vertex, actions);
            }
            StatefullMDP.staticMdp = new StatefullMDP();
            StatefullMDP.staticMdp.setMdp(states);
            try {
                this.mdp = (StatefullMDP) StatefullMDP.staticMdp.getClone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(SARSAStatefull.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                this.mdp = (StatefullMDP) StatefullMDP.staticMdp.getClone();
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
        this.currentEdge = null;
        this.travelTime = 0;
        this.route = new LinkedList<>();

        //Choose S
        this.currentVertex = origin;
        //Choose A
        this.currentEdge = mdp.getAction(mdp.getMdp().get(currentVertex));
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
        double r_t1 = this.rewardFunction.getReward(this);
        this.travelTime += currentEdge.getCost();
        this.route.add(currentEdge);

        //Observe S'       
        String nextVertex = currentEdge.getTargetVertex();
        AbstractEdge nextEdge;
        double qa_t1;

        //Take action A' from S' using exploration-exploitation strategy
        nextEdge = mdp.getAction(mdp.getMdp().get(nextVertex));
        if (currentEdge.getTargetVertex().equals(this.destination)) {
            qa_t1 = 0.;
        } else {
            qa_t1 = this.mdp.getValue(nextEdge);
        }

        //update q-table
        double qa_t0 = this.mdp.getValue(currentEdge);
        //SARSA Algorithm: Q(S,A)←Q(S,A)+α[R+γQ(S',A')−Q(S,A)]
        qa_t0 = qa_t0 + ALPHA * (r_t1 + GAMMA * qa_t1 - qa_t0);

        this.mdp.setValue(currentEdge, qa_t0);

        //S ←S'
        currentEdge = nextEdge;
        // A←A';
        currentVertex = nextVertex;
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
