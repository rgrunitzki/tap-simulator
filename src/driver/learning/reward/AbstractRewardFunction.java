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
package driver.learning.reward;

import driver.Driver;
import static driver.learning.reward.StatelessRewardFunction.COMPUTED_REWARDS;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import simulation.Params;

/**
 * This class describes the basic structure of reward functions. There are three
 * possible implementations for each reward function:
 * <ul>
 * <li> Default reward function;
 * <li> Difference Rewards;
 * <li> Reward Shaping.
 * </ul>
 *
 * The enum "Reward Function" describes which must be used.
 *
 * @author Ricardo Grunitzki
 * @param <DriverType> Class of the driver.
 */
public abstract class AbstractRewardFunction<DriverType extends Driver> {

    /**
     * Collection of the common rewards of the current episode. It enables to
     * speed-up their computing.
     */
    public static Map<String, Double> COMPUTED_REWARDS = new ConcurrentHashMap<>();

    /**
     * @TODO: THE EPISODIC STORAGE OF COMPUTED_REWARDS must be improved and
     * generalized for all cases.
     */
    /**
     * Gets the reward value for an specific driver.
     *
     * @param driver Driver which will receive the reward
     * @return reward value
     */
    public Double getReward(DriverType driver) {
        String route = driver.getRoute().toString();
        if (COMPUTED_REWARDS.containsKey(route)) {
            return COMPUTED_REWARDS.get(route);
        } else {
            switch (Params.REWARD_FUNCTION) {
                case DIFFERENCE_REWARDS:
                    COMPUTED_REWARDS.put(route, getDifferenceRewards(driver));
                    break;
                case STANDARD_REWARD:
                    COMPUTED_REWARDS.put(route, getStandardReward(driver));
                    break;
                case REWARD_SHAPING:
                    COMPUTED_REWARDS.put(route, getRewardShaping(driver));
                    break;
                default:
                    throw new AssertionError(Params.REWARD_FUNCTION.name());
            }
        }
        return COMPUTED_REWARDS.get(route);
    }

    /**
     * The standard implementation of the current Reward Function.
     *
     * @param driver Driver will receive the reward
     * @return reward value
     */
    public abstract Double getStandardReward(DriverType driver);

    /**
     * The reward shaping implementation of the current Reward Function.
     *
     * @param driver Driver will receive the reward
     * @return reward value
     */
    public abstract Double getRewardShaping(DriverType driver);

    /**
     *
     * The difference rewards implementation for the current Reward Function.
     *
     * @param driver Driver will receive the reward
     * @return reward value
     */
    public abstract Double getDifferenceRewards(DriverType driver);

}
