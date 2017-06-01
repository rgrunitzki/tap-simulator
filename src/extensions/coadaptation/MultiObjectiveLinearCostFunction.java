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

import scenario.network.AbstractCostFunction;
import scenario.network.AbstractEdge;

/**
 *
 * @author Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
 */
public class MultiObjectiveLinearCostFunction extends AbstractCostFunction {

    @Override
    public double evalCost(AbstractEdge edge) {
        return getTravelCost(edge, edge.getTotalFlow()) + getMonetaryCost(edge, edge.getTotalFlow());
//        return getTravelCost(edge, edge.getTotalFlow());
//        return getMonetaryCost(edge, edge.getTotalFlow());
    }

    @Override
    public double evalDesirableCost(AbstractEdge edge, double desirableFlow) {
        //return (stringToDouble((String) edge.getParams().get("fftime")) + (stringToDouble((String) edge.getParams().get("alpha"))) * desirableFlow);
        return getTravelCost(edge, desirableFlow)
                + getMonetaryCost(edge, desirableFlow);
    }

    public final double getMonetaryCost(AbstractEdge edge, double flow) {
        double cost = 0;
        if (edge instanceof LearnerEdge) {
            return ((LearnerEdge) edge).getpMax() * ((LearnerEdge) edge).getPriceProportion();
        } else {
            return cost;
        }
    }

    public final double getTravelCost(AbstractEdge edge, double flow) {
        return (stringToDouble((String) edge.getParams().get("fftime")) + (stringToDouble((String) edge.getParams().get("alpha"))) * flow);
    }
}
