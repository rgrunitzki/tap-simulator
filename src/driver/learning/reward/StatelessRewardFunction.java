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
    public Double getDifferenceRewards(QLStateless driver
    ) {
        double soma_gz = 0;
        double gz_zi = 0;
        for (Object edge : driver.getRoute().getGraph().edgeSet()) {
            AbstractEdge e = (AbstractEdge) edge;

            if (e.getTotalFlow() > 0) {
                soma_gz -= e.getCost() * e.getTotalFlow();
            } else {
                soma_gz -= e.getCost();
            }

            if (driver.getRoute().getEdgeList().contains(e)) {
                if (e.getTotalFlow() > 0) {
                    gz_zi -= e.getCostFunction().evalDesirableCost(e, e.getTotalFlow() - 1 * Params.PROPORTION) * (e.getTotalFlow() - 1 * Params.PROPORTION);
                } else {
                    gz_zi -= e.getCostFunction().evalDesirableCost(e, e.getTotalFlow() - 1 * Params.PROPORTION);
                }
            } else {
                if (e.getTotalFlow() > 0) {
                    gz_zi -= e.getCost() * e.getTotalFlow();
                } else {
                    gz_zi -= e.getCost();
                }
            }

        }
        return (soma_gz - gz_zi) / Params.USED_TAP.getDrivers().size();
    }
}
