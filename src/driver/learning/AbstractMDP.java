/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 * @param <State>
 * @param <Action>
 * @param <Value>
 */
public abstract class AbstractMDP<State, Action, Value> implements Serializable, Cloneable {

    public AbstractMDP() {
    }

    protected Map<State, Map<Action, Double>> mdp = new ConcurrentHashMap<>();

    public abstract void setValue(Action action, Value value);

    public abstract double getValue(Action actionKey);

    public abstract void createMDP(List<Action> actions);

    public abstract Action getAction(State key);

    public abstract void reset();

    @Override
    protected Object clone() throws CloneNotSupportedException {
        AbstractMDP other = (AbstractMDP) super.clone();
        other.mdp = new ConcurrentHashMap<>();
        //state
        for (State action : mdp.keySet()) {
            other.mdp.put(action, new ConcurrentHashMap<>());
            //add actions to state
            ((Map<Action, Double>) other.mdp.get(action)).putAll(new ConcurrentHashMap<>(mdp.get(action)));
        }
        return other;
    }

    protected double getEpsilon() {
        double epsilon = 1 * Math.pow(Params.E_DECAY_RATE, Params.CURRENT_EPISODE);
        return epsilon;
    }
}
