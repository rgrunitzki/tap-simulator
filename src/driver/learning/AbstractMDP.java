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
 * This basic structure models the Markov Decision Process (MDP) of the drivers.
 * This structure must be extended in order to implement new features or
 * algorithms for Multi-agent reinforcement learning.
 *
 * @author Ricardo Grunitzki
 * @param <State> State type
 * @param <Action> Action type
 * @param <Value> Value type
 */
public abstract class AbstractMDP<State, Action, Value extends Comparable> implements Serializable, Cloneable {

    /**
     * creates a new object.
     */
    public AbstractMDP() {
        try {
            this.explorationPolicy = (ExplorationPolicy) Params.EXPLORATION_POLICY.getConstructor().newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(AbstractMDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Exploration policy used by the agents.
     */
    protected ExplorationPolicy explorationPolicy;

    /**
     * Collection of state-action pairs that represents the MDP.
     */
    protected Map<State, Map<Action, Value>> mdp = new ConcurrentHashMap<>();

    /**
     * Sets a value for an specific state-action pair.
     *
     * @param action state-action pair
     * @param value value of the pair state-action
     */
    public abstract void setValue(Action action, Value value);

    /**
     * Gets the value of an state-action pair.
     *
     * @param actionKey key of the state-action pair
     * @return value of the state-action pair
     */
    public abstract double getValue(Action actionKey);

    /**
     * Creates an MDP according to the specifications.
     *
     * @param actions collection of state-action pair
     */
    public abstract void createMDP(List<Action> actions);

    /**
     * Gets the next action according to the exploration policy.
     *
     * @param mdp collection of state-action pair
     * @return the action key
     */
    public Action getAction(Map<Action, Value> mdp) {
        return (Action) explorationPolicy.getAction(mdp);
    }

    /**
     * Resets the MDP.
     */
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

    /**
     * Returns the MDP of the driver.
     *
     * @return MDP object
     */
    public Map<State, Map<Action, Value>> getMdp() {
        return mdp;
    }

}
