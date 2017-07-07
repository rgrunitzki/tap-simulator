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

import driver.learning.mdp.StatelessMDP;
import driver.learning.reward.AbstractRewardFunction;
import driver.learning.reward.StatelessRewardFunction;
import driver.learning.exploration.EpsilonDecreasing;
import driver.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import scenario.network.AbstractEdge;

/**
 *
 * @author Ricardo Grunitzki
 */
@SuppressWarnings("rawtypes")
public class QLStateless extends Driver<QLStateless, GraphPath> {

    public static Map<String, StatelessMDP> mdpPerOD = new ConcurrentHashMap<>();
    public static Map<String, KShortestPaths> ksps = new ConcurrentHashMap<>();
    /**
     * Learning rate parameter of Q-Learning. The default value is {@code 0.5}.
     */
    public static float ALPHA = 0.5f;
    /**
     * Number of available routes used in the MDP. The default value is
     * {@code 4}.
     */
    public static int K = 4;

    private int edgeIndex = 0;

    private StatelessMDP mdp = new StatelessMDP();
    private final AbstractRewardFunction rewardFunction = new StatelessRewardFunction();

    public QLStateless(int id, String origin, String destination, Graph graph) {
        super(id, origin, destination, graph);
    }

    @Override
    public void beforeSimulation() {

        //initialize MDP
        if (QLStateless.mdpPerOD.containsKey(this.origin + "-" + this.destination)) {
            try {
                this.mdp = (StatelessMDP) (QLStateless.mdpPerOD.get(this.origin + "-" + this.destination)).getClone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(QLStateless.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            List paths;
            if (QLStateless.ksps.containsKey(this.origin)) {
                paths = QLStateless.ksps.get(this.origin).getPaths(this.destination);

            } else {
                KShortestPaths ksp = new KShortestPaths(graph, this.origin, QLStateless.K);
                paths = ksp.getPaths(this.destination);
                QLStateless.ksps.put(this.origin, ksp);
            }
            this.mdp.createMDP(paths);
            QLStateless.mdpPerOD.put(this.origin + "-" + this.destination, this.mdp);
        }
    }

    @Override
    public void afterSimulation() {
    }

    @Override
    public void beforeEpisode() {
        reset();
        this.route = mdp.getAction(this.mdp.getMdp().get(this.origin));
        this.currentEdge = (AbstractEdge) this.route.getEdgeList().get(0);

        if (!AbstractRewardFunction.COMPUTED_REWARDS.isEmpty()) {
            AbstractRewardFunction.COMPUTED_REWARDS.clear();
        }
    }

    @Override
    public void afterEpisode() {
        double qa = this.mdp.getValue(route);
        double r = rewardFunction.getReward(this);
        double f = 0.0f;
//        f = rewardFunction.getRewardShaping(this);
        this.mdp.setValue(this.route, (qa + QLStateless.ALPHA * (r - qa + f)));
    }

    @Override
    public void beforeStep() {
        this.currentEdge = (AbstractEdge) this.route.getEdgeList().get(this.edgeIndex);
        this.edgeIndex++;
    }

    @Override
    public void afterStep() {
        this.travelTime += currentEdge.getCostFunction().evalCost(currentEdge);
        if (this.currentEdge.getTargetVertex().equals(this.destination)) {
            this.currentEdge = null;
        }
    }

    public void reset() {
        this.edgeIndex = 0;
        this.currentEdge = null;
        this.route = null;
        this.travelTime = 0;
    }

    @Override
    public void resetAll() {
        this.learningEffort = 0;
        this.reset();
        this.mdp.reset();
    }

    public int getSize() {
        return this.mdp.getMdp().size();
    }

    @Override
    public GraphPath getRoute() {
        return this.route;
    }

    @Override
    public List<Pair> getParameters() {

        List<Pair> list = new ArrayList<>();
        list.add(Pair.of(this.getClass().getSimpleName().toLowerCase(), ""));
        list.add(Pair.of("epsilon", EpsilonDecreasing.EPSILON_DECAY));
        list.add(Pair.of("k", QLStateless.K));
        list.add(Pair.of("alpha", QLStateless.ALPHA));
        return list;
    }

}
