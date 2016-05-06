/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.jgrapht.GraphPath;

/**
 * MDP of the drivers that use a set of precomputed routes.
 *
 * @author Ricardo Grunitzki
 */
public class StatelessMDP extends AbstractMDP<String, GraphPath, Double> {

    @Override
    public void setValue(GraphPath action, Double value) {
        mdp.get((String) action.getStartVertex()).put(action, value);
    }

    @Override
    public double getValue(GraphPath actionKey) {
        return mdp.get((String) actionKey.getStartVertex()).get(actionKey);
    }

    @Override
    public void createMDP(List<GraphPath> actions) {
        for (GraphPath path : actions) {
            if (mdp.get((String) path.getStartVertex()) == null) {
                mdp.put((String) path.getStartVertex(), new ConcurrentHashMap<>());
            }
            mdp.get((String) path.getStartVertex()).put(path, 0.0);
        }
    }

    @Override
    public void reset() {
        mdp.get(mdp.keySet().iterator().next()).entrySet().parallelStream().forEach((entrySet) -> {
            entrySet.setValue(0.0);
        });
    }
}
