/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import driver.Driver;
import extensions.c2i.EdgeC2I;
import org.jgrapht.Graph;
import scenario.AbstractEdge;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class StatefullRewardFunctionC2I extends AbstractRewardFunction<Driver> {

    public static Double[] alpha = {-1.0, -1.0, -1.0, -1.0};

    public StatefullRewardFunctionC2I(Graph graph) {
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
        EdgeC2I edge = (EdgeC2I) driver.getCurrentEdge();
        return -edge.getInformation(edge).getValue();
    }

    @Override
    public Double getDifferenceRewards(Driver driver) {
        return 0.0;
    }
}
