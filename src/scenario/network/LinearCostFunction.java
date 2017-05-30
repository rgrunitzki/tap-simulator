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
package scenario.network;

/**
 * A linear cost function that represents the travel time in minutes. This
 * function is defined by t=fftime+flow*alpha, where:
 * <ul>
 * <li>t = travel time in minutes;
 * <li>fftime = travel time under free-flow condition;
 * <li>flow = flow of drivers;
 * <li>alpha = constant factor.
 * </ul>
 * This function is incremented linearly by c for each unit of flow (f).
 *
 * @author Ricardo Grunitzki
 */
public class LinearCostFunction extends AbstractCostFunction {

    @Override
    public double evalCost(AbstractEdge edge) {
        return (stringToDouble((String) edge.getParams().get("fftime")) + (stringToDouble((String) edge.getParams().get("alpha"))) * edge.getTotalFlow());
    }

    @Override
    public double evalDesirableCost(AbstractEdge edge, double desirableFlow) {
        return (stringToDouble((String) edge.getParams().get("fftime")) + (stringToDouble((String) edge.getParams().get("alpha"))) * desirableFlow);
    }
}
