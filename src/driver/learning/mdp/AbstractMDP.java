package driver.learning.mdp;

import driver.learning.exploration.ExplorationStrategy;
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
            this.explorationPolicy = (ExplorationStrategy) Params.EXPLORATION_POLICY.getConstructor().newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(AbstractMDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Exploration policy used by the agents.
     */
    protected ExplorationStrategy explorationPolicy;

    /**
     * Collection of state-action pairs that represents the MDP.
     */
    protected Map<State, Map<Action, Value>> mdp = new ConcurrentHashMap<>();

    /**
     * Collection of z-values used in Eligibility Traces.
     */
    protected Map<State, Map<Action, Value>> zTable = new ConcurrentHashMap<>();

    /**
     * The maximum variation of the Q_values
     */
    private double deltaQ;

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
        other.zTable = new ConcurrentHashMap<>();
        //Q-table
        for (State state : this.mdp.keySet()) {
            other.mdp.put(state, new ConcurrentHashMap<>());
            //add actions to state
            ((Map<Action, Value>) other.mdp.get(state)).putAll(new ConcurrentHashMap<>(this.mdp.get(state)));
        }
        //Z-table Eligibility Traces
        if (!this.zTable.isEmpty()) {
            for (State state : zTable.keySet()) {
                other.zTable.put(state, new ConcurrentHashMap<>());
                //add actions to state
                ((Map<Action, Value>) other.zTable.get(state)).putAll(new ConcurrentHashMap<>(this.zTable.get(state)));
            }
        }
        return other;
    }

    public Object getClone() throws CloneNotSupportedException {
        return this.clone();
    }

    public void updateDetalQ(double qvalue) {
        if (this.deltaQ > qvalue) {
            this.deltaQ = qvalue;
        }
    }

    public void resetDetaQ() {
        this.deltaQ = 0.0;
    }

    /**
     * Returns the MDP of the driver.
     *
     * @return MDP object
     */
    public Map<State, Map<Action, Value>> getMdp() {
        return mdp;
    }

    public void setMdp(Map<State, Map<Action, Value>> mdp) {
        this.mdp = mdp;
    }

    public Map<State, Map<Action, Value>> getzTable() {
        return zTable;
    }

    public void setzTable(Map<State, Map<Action, Value>> zTable) {
        this.zTable = zTable;
    }

    public double getDeltaQ() {
        return deltaQ;
    }

}
