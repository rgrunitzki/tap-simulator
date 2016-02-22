package driver.learning;

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
import scenario.AbstractEdge;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
@SuppressWarnings("rawtypes")
public class QLStateless extends Driver<QLStateless, GraphPath> {

    public static Map<String, StatelessMDP> mdpPerOD = new ConcurrentHashMap<>();
    public static Map<String, KShortestPaths> ksps = new ConcurrentHashMap<>();
    public static float ALPHA = 0.1f;
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
                this.mdp = (StatelessMDP) (QLStateless.mdpPerOD.get(this.origin + "-" + this.destination)).clone();
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
        this.route = mdp.getAction(null);
        this.currentEdge = (AbstractEdge) this.route.getEdgeList().get(0);

        if (!AbstractRewardFunction.rewards.isEmpty()) {
            AbstractRewardFunction.rewards.clear();
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
        this.reset();
        this.mdp.reset();
    }

    public int getSize() {
        return this.mdp.mdp.size();
    }

    @Override
    public GraphPath getRoute() {
        return this.route;
    }

    @Override
    public List<Pair> getParameters() {

        List<Pair> list = new ArrayList<>();
        list.add(Pair.of(this.getClass().getSimpleName().toLowerCase(), ""));
        list.add(Pair.of("epsilon", Params.E_DECAY_RATE));
        list.add(Pair.of("k", QLStateless.K));
        list.add(Pair.of("alpha", QLStateless.ALPHA));
        return list;
    }

}
