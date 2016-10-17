/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning.exploration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import simulation.Params;

/**
 * Epsilon-greed policy choice implementation. This policy starts with an
 * initial exploration rate and is decreased along the episodes by an constant
 * factor.
 *
 * @author Ricardo Grunitzki
 * @param <State>
 * @param <Action>
 * @param <Value>
 */
public class EpsilonDecreasing<State, Action, Value extends Comparable> extends ExplorationStrategy<State, Action, Value> {
    
    /**
     * Decreasing factor of e-decreasing
     */
    public static float EPSILON_DECAY = 0.99F; //
    /**
     * Initial exploration of e-decreasing policy
     */
    public static float EPSILON_INITIAL = 1.0F;

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
    public void episodeUpdate() {
    }

    @Override
    public void reset() {
    }

    private double getEpsilon() {
        double epsilon = 1 * Math.pow(EPSILON_DECAY, Params.CURRENT_EPISODE);
        return epsilon;
    }

}
