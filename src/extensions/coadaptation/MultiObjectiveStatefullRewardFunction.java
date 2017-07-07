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
package extensions.coadaptation;

import driver.Driver;
import driver.learning.reward.AbstractRewardFunction;
import org.jgrapht.Graph;
import simulation.Params;

/**
 *
 * @author Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
 */
public class MultiObjectiveStatefullRewardFunction extends AbstractRewardFunction<Driver> {

    /**
     * Creates the reward function object
     *
     * @param graph
     */
    public MultiObjectiveStatefullRewardFunction(Graph graph) {
        this.graph = graph;
    }

    Graph graph;

    @Override
    public Double getReward(Driver driver) {
        switch (Params.REWARD_FUNCTION) {
            case DIFFERENCE_REWARDS:
                return getDifferenceRewards(driver);
            case STANDARD_REWARD:
                return getStandardReward(driver);
        }
        return getStandardReward(driver);
    }

    @Override
    public Double getStandardReward(Driver driver) {
        return -driver.getCurrentEdge().getCost();
    }

    @Override
    public Double getRewardShaping(Driver driver) {
        return 0.0;
    }

    @Override
    public Double getDifferenceRewards(Driver driver) {
        Double gz = driver.getCurrentEdge().getCost();
        Double gz_zi = (double) driver.getCurrentEdge().getCostFunction().evalDesirableCost(driver.getCurrentEdge(), driver.getCurrentEdge().getTotalFlow() - 1);
        return -(gz - gz_zi);
    }
}
