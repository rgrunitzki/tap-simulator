package driver.learning;

import java.util.Map;

/**
 * Exploration policy used by the drivers in their action choice.
 *
 * @author Ricardo Grunitzki
 * @param <State> state type
 * @param <Action> action type
 * @param <Value> pair state-action value type
 */
public abstract class ExplorationPolicy<State, Action, Value extends Comparable> {

    /**
     * Returns the next action.
     *
     * @param mdp mdp of the agent
     * @return next action
     */
    public abstract Action getAction(Map<Action, Value> mdp);

    /**
     * Executes the updates needed at each episode.
     */
    public abstract void episodeUpdate();

    /**
     * Resets the exploration policy.
     */
    public abstract void reset();

}
