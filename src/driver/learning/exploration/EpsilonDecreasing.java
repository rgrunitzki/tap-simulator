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
     * Decreasing factor of e-decreasing. Default value is {@code 0.99}.s
     */
    public static float EPSILON_DECAY = 0.99F; //
    /**
     * Initial exploration of e-decreasing policy. Default value is {@code 1.0}.
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
