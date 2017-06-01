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

import driver.learning.QLStateless;
import scenario.network.AbstractEdge;
import simulation.Params;

/**
 * Stateless implementation of AbstractRewardFunction.
 *
 * @author Ricardo Grunitzki
 */
public class StatelessRewardFunction extends AbstractRewardFunction<QLStateless> {

    @Override
    public Double getStandardReward(QLStateless driver) {
        Double cost = 0.0;
        for (Object edge : driver.getRoute().getEdgeList()) {
            AbstractEdge e = (AbstractEdge) edge;
            cost -= e.getCostFunction().evalCost(e);
        }
        return cost;
    }

    @Override
    public Double getRewardShaping(QLStateless driver) {
        Double cost = 0.0;
        for (Object edge : driver.getRoute().getEdgeList()) {
            AbstractEdge e = (AbstractEdge) edge;
            if (e.getTotalFlow() > 0) {
                cost -= e.getCost() * e.getTotalFlow();
            } else {
                cost -= e.getCost();
            }
        }

        return cost;
    }

    @Override
    public Double getDifferenceRewards(QLStateless driver) {
        //global system's performance
        double gz = 0;
        //global system's performance of a theretical system without agent i
        double gz_i = 0;
        for (Object edg : driver.getRoute().getGraph().edgeSet()) {
            AbstractEdge edge = (AbstractEdge) edg;

            if (edge.getTotalFlow() > 0) {
                gz -= edge.getCost() * edge.getTotalFlow();
            } else {
                gz -= edge.getCost();
            }

            if (driver.getRoute().getEdgeList().contains(edge)) {
                if (edge.getTotalFlow() > 0) {
                    gz_i -= edge.getCostFunction().evalDesirableCost(edge, edge.getTotalFlow() - 1) * (edge.getTotalFlow() - 1);
                } else {
                    gz_i -= edge.getCostFunction().evalDesirableCost(edge, edge.getTotalFlow() - 1);
                }
            } else {
                if (edge.getTotalFlow() > 0) {
                    gz_i -= edge.getCost() * edge.getTotalFlow();
                } else {
                    gz_i -= edge.getCost();
                }
            }

        }
        return (gz - gz_i) / Params.USED_TAP.demandSize();
    }
}
