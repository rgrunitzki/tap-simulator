/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

/**
 *
 * @author rgrunitzki
 */
public enum RewardFunction {

    DifferenceRewards("Difference-Rewards"),
    StandardReward("Standard Reward");

    private final String value;

    private RewardFunction(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
