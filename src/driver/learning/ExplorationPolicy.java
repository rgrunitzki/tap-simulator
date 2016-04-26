/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import java.util.Map;

/**
 *
 * @author Ricardo Grunitzki
 * @param <State> state type
 * @param <Action> action type
 * @param <Value> pair state-action value type
 */
public abstract class ExplorationPolicy<State, Action, Value extends Comparable> {

    public abstract Action getAction(Map<Action, Value> mdp);

    public abstract void episodeUpdate();

    public abstract void reset();

}
