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
package extensions.hierarchical;

import driver.Driver;
import driver.learning.reward.AbstractRewardFunction;
import java.util.Set;
import org.jgrapht.Graph;
import scenario.network.AbstractEdge;
import simulation.Params;

/**
 * Provides a reward function's object
 *
 * @author Ricardo Grunitzki
 */
public class HierarchicalStetefullRewardFunction extends AbstractRewardFunction<Driver> {

    private Graph graph;

    /**
     * Creates the reward function object
     *
     * @param graph
     */
    public HierarchicalStetefullRewardFunction(Graph graph) {
        this.graph = graph;
    }

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
        boolean check = false;

        if (driver.getCurrentEdge().getTargetVertex().equalsIgnoreCase(driver.getDestination())) {
            check = true;
        } else {
            Set<AbstractEdge> edges = graph.edgesOf(driver.getCurrentVertex());
            for (AbstractEdge edge : edges) {
                if (edge.getSourceVertex().equalsIgnoreCase(driver.getCurrentEdge().getTargetVertex())) {
                    check = true;
                }
            }
        }

        if (check) {
            return -driver.getCurrentEdge().getCost();
        } else {
            return -100.; //penality for going to a situation in which there is no end
        }
    }

    @Override
    public Double getRewardShaping(Driver driver) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Double getDifferenceRewards(Driver driver) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
