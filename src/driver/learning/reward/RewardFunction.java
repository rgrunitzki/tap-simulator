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
