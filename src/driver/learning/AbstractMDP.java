/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 * @param <State>
 * @param <Action>
 * @param <Value>
 */
public abstract class AbstractMDP<State, Action, Value extends Comparable> implements Serializable, Cloneable {

    public AbstractMDP() {
        try {
            this.explorationPolicy = (ExplorationPolicy) Params.EXPLORATION_POLICY.getConstructor().newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(AbstractMDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected ExplorationPolicy explorationPolicy;

    protected Map<State, Map<Action, Value>> mdp = new ConcurrentHashMap<>();

    public abstract void setValue(Action action, Value value);

    public abstract double getValue(Action actionKey);

    public abstract void createMDP(List<Action> actions);

    public Action getAction(Map<Action, Value> mdp) {
        return (Action) explorationPolicy.getAction(mdp);
    }

    public abstract void reset();

    @Override
    protected Object clone() throws CloneNotSupportedException {
        AbstractMDP other = (AbstractMDP) super.clone();
        other.mdp = new ConcurrentHashMap<>();
        //state
        for (State action : mdp.keySet()) {
            other.mdp.put(action, new ConcurrentHashMap<>());
            //add actions to state
            ((Map<Action, Value>) other.mdp.get(action)).putAll(new ConcurrentHashMap<>(mdp.get(action)));
        }
        return other;
    }

    protected double getEpsilon() {
        double epsilon = 1 * Math.pow(Params.E_DECAY_RATE, Params.CURRENT_EPISODE);
        return epsilon;
    }

    public Map<State, Map<Action, Value>> getMdp() {
        return mdp;
    }

}
