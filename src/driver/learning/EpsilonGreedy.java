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
import simulation.Params;

/**
 *
 * @author rgrunitzki
 * @param <State>
 * @param <Action>
 * @param <Value>
 */
public class EpsilonGreedy<State, Action, Value extends Comparable> extends ExplorationPolicy<State, Action, Value>{
    
    @Override
    public Action getAction(Map<Action, Value> mdp) {
        float random = Params.RANDOM.nextFloat();
        if (random <= getEpsilon()) {
            List actions = new ArrayList<>(mdp.keySet());
            Collections.shuffle(actions, Params.RANDOM);
            return (Action) actions.get(0);
        } else {
            return Collections.max(mdp.entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue())).getKey();
        }
    }

    @Override
    public void episodeUpdate() {}

    @Override
    public void reset() {}
    
    private double getEpsilon() {
        double epsilon = 1 * Math.pow(Params.EPSILON_DECAY, Params.CURRENT_EPISODE);
        return epsilon;
    }
    
}
