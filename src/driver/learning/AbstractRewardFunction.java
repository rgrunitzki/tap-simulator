/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import driver.Driver;
import static driver.learning.StatelessRewardFunction.COMPUTED_REWARDS;
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
 * The enum 'Reward Function' describes which must be used.
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
