/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import driver.Driver;
import static driver.learning.StatelessRewardFunction.rewards;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 * @param <T>
 */
public abstract class AbstractRewardFunction <T extends Driver>{

    public static Map<String, Double> rewards = new ConcurrentHashMap<>();

    public Double getReward(T driver) {
        String route = driver.getRoute().toString();
        if (rewards.containsKey(route)) {
            return rewards.get(route);
        } else {
            switch (Params.REWARDFUNCTION) {
                case DifferenceRewards:
                    rewards.put(route, getDifferenceRewards(driver));
                    break;
                case StandardReward:
                    rewards.put(route, getStandardReward(driver));
                    break;
                case RewardShaping:
                    rewards.put(route, getRewardShaping(driver));
                    break;
                default:
                    throw new AssertionError(Params.REWARDFUNCTION.name());
            }
        }
        return rewards.get(route);
    }

    public abstract Double getStandardReward(T driver);

    public abstract Double getRewardShaping(T driver);

    public abstract Double getDifferenceRewards(T driver);

}
