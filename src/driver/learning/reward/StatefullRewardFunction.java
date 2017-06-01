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
import driver.learning.QLStateless;
import java.util.List;
import org.jgrapht.Graph;
import scenario.network.AbstractEdge;
import simulation.Params;

/**
 * Reward function for the Statefull implementation of learning drivers.
 *
 * @author Ricardo Grunitzki
 */
public class StatefullRewardFunction extends AbstractRewardFunction<Driver> {

    /**
     * Creates the reward function object
     *
     * @param graph
     */
    public StatefullRewardFunction(Graph graph) {
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
        int type = 1;
        switch (type) {
            //Difference rewards signal considering the whole system

            //TODO
            //this calculus does not work well because it considers the previous actions taken by the agent at the current time step.
            case 0: {
                //global system performance
                double gz = 0;
                //system performance of an theoretical system without the contribuition of agent i.
                double gz_i = 0;

                //route of agent i.
                List<AbstractEdge> route = (List<AbstractEdge>) driver.getRoute();

                //percours the links of the graph
                for (AbstractEdge edge : Params.USED_TAP.getGraph().edgeSet()) {

                    //contribution of the edge for the system performance considering agent i.
                    if (edge.getTotalFlow() > 0) {
                        gz += edge.getCost() * edge.getTotalFlow();
                    } else {
                        gz += edge.getCost();
                    }

                    //contribution of the edge for the system performance without considering agent i.
                    if (route.contains(edge)) {
                        gz_i += edge.getCostFunction().evalDesirableCost(edge, edge.getTotalFlow() - 1) * (edge.getTotalFlow() - 1);
                    } else {
                        if (edge.getTotalFlow() > 0) {
                            gz_i += edge.getCost() * edge.getTotalFlow();
                        } else {
                            gz_i += edge.getCost();
                        }
                    }
                }
                gz /= Params.USED_TAP.demandSize();
                gz_i /= (Params.USED_TAP.demandSize() - 1);
                //difference rewards signal
                double d_i = -Math.abs(gz - gz_i);
                return d_i; //difference rewards

            }
            //Difference rewards signal considering just the current link
            default: {
                Double gz = driver.getCurrentEdge().getCost() * driver.getCurrentEdge().getTotalFlow();
                Double gz_i = (double) driver.getCurrentEdge().getCostFunction().evalDesirableCost(driver.getCurrentEdge(), (driver.getCurrentEdge().getTotalFlow() - 1)) * (driver.getCurrentEdge().getTotalFlow() - 1);
                gz /= Params.USED_TAP.demandSize();
                gz_i /= (Params.USED_TAP.demandSize() - 1);

                double d_i = -Math.abs(gz - gz_i);
                return d_i;
            }
        }
    }
}
