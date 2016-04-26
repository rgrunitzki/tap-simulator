/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

/**
 *
 * @author Ricardo Grunitzki
 */
public enum RewardFunction {

    DIFFERENCE_REWARDS("Difference Rewards"),
    REWARD_SHAPING("Reward Shaping"),
    STANDARD_REWARD("Standard Reward");

    private final String value;

    private RewardFunction(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
