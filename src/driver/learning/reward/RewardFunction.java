package driver.learning.reward;

/**
 *
 *
 * @author Ricardo Grunitzki
 */
public enum RewardFunction {

    /**
     * Difference Rewards function
     */
    DIFFERENCE_REWARDS("Difference Rewards"),
    /**
     * Reward shaping function
     */
    REWARD_SHAPING("Reward Shaping"),
    /**
     * Default reward function
     */
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
