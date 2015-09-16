/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jgrapht.GraphPath;
import simulation.Params;

/**
 *
 * @author rgrunitzki
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
    public GraphPath getAction(String key) {
        Map<GraphPath, Double> mdp2 = mdp.get(mdp.keySet().iterator().next());
        float random = Params.RANDOM.nextFloat();
        if (random <= getEpsilon()) {
            List l = new ArrayList<>(mdp2.keySet());
            Collections.shuffle(l, Params.RANDOM);
            return (GraphPath) l.get(0);
        } else {
            return Collections.max(mdp2.entrySet(), (entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).getKey();
        }
    }

    @Override
    public void reset() {
        mdp.get(mdp.keySet().iterator().next()).entrySet().parallelStream().forEach((entrySet) -> {
            entrySet.setValue(0.0);
        });
    }

//    @Override
//    public void setValue(GraphPath key, Double value) {
//        this.mdp.put(key, value);
//    }
//
//    @Override
//    public double getValue(GraphPath key) {
//        return this.mdp.get(key);
//    }
//
//    @Override
//    public void createMDP(List<GraphPath> states) {
//        states.parallelStream().forEach((state) -> {
//            mdp.put(state, 0.0);
//        });
//    }
//
//    @Override
//    public GraphPath getAction(String key) {
//        float random = Params.RANDOM.nextFloat();
//        if (random <= getEpsilon()) {
//            List l = new ArrayList<>(mdp.keySet());
//            Collections.shuffle(l, Params.RANDOM);
//            return (GraphPath) l.get(0);
//        } else {
//            return Collections.max(mdp.entrySet(), (entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).getKey();
//        }
//    }
//
//    @Override
//    public void reset() {
//        mdp.entrySet().parallelStream().forEach((entrySet) -> {
//            entrySet.setValue(0.0);
//        });
//    }
}
