/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions.coadaptation;

import driver.Driver;
import driver.learning.reward.AbstractRewardFunction;
import org.jgrapht.Graph;
import simulation.Params;

/**
 *
 * @author rgrunitzki
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
        Double gz_zi = (double) driver.getCurrentEdge().getCostFunction().evalDesirableCost(driver.getCurrentEdge(), driver.getCurrentEdge().getTotalFlow() - 1 * Params.PROPORTION);
        return -(gz - gz_zi);
    }
}
