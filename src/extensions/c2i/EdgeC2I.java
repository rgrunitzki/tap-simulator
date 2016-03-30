/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions.c2i;

import driver.Driver;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jgrapht.Graph;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import scenario.AbstractCostFunction;
import scenario.AbstractEdge;

/**
 *
 * @author rgrunitzki
 */
public class EdgeC2I extends AbstractEdge {

    public static FloydWarshallShortestPaths SHORTEST_PATHS;

    public static boolean C2I_WEIGHT = false;

    private final Map<AbstractEdge, MessageC2I> knowledgeBase = new ConcurrentHashMap<>();
    
    private MessageC2I knowledgeC2I = new MessageC2I();
    
    public EdgeC2I(AbstractCostFunction costFunction) {
        super(costFunction);
    }

    public synchronized MessageC2I getInformation(AbstractEdge key) {
        return knowledgeBase.get(key);
    }

    private synchronized void updateInformation(AbstractEdge key, Double value) {

        if (!knowledgeBase.containsKey(key)) {
            knowledgeBase.put(key, new MessageC2I());
        }

        knowledgeBase.get(key).addValue(value);
    }

    @Override
    protected synchronized double getWeight() {
        if (C2I_WEIGHT) {
            if (this.getInformation(this) == null) {
                return this.getCostFunction().evalDesirableCost(this, 0);
            } else {
                return this.getInformation(this).getValue();
            }
        } else {
            return super.getWeight();
        }
    }

    @Override
    public synchronized void proccess(Driver driver) {
        //update flow counters
        super.proccess(driver);
        this.updateInformation(this, this.getCost());
    }

    public static synchronized void afterEpisode(Graph graph) {
        if (SHORTEST_PATHS != null) {
            SHORTEST_PATHS = null;
        }
    }

    public static synchronized void beforeEpisode(Graph graph) {
        if (SHORTEST_PATHS == null) {
//        if (SHORTEST_PATHS == null && C2IEpsilonGreedy.superExploration()) {
            EdgeC2I.C2I_WEIGHT = true;
            SHORTEST_PATHS = new FloydWarshallShortestPaths<>(graph);
            EdgeC2I.C2I_WEIGHT = false;
        }
    }
}
