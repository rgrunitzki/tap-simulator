/* 
 * Copyright (C) 2017 Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package driver.learning.exploration;

import java.util.Map;

/**
 * Exploration strategy used by the drivers in their action choice.
 *
 * @author Ricardo Grunitzki
 * @param <State> state type
 * @param <Action> action type
 * @param <Value> pair state-action value type
 */
public abstract class ExplorationStrategy<State, Action, Value extends Comparable> {

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
