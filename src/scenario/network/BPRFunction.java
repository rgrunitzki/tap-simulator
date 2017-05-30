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
 * Implementation of the Volume-Delay Function (VDF) proposed by Bureau of
 * Public Roads. This VDF represents the travel time in minutes. It is defined
 * by t=fftime(1+ alpha(flow/capacity)^beta), where:
 * <ul>
 * <li>t = travel time in minutes;
 * <li>fftime = free-flow travel time;
 * <li>capacity = nominal capacity of the edge
 * <li>flow = flow of vehicles
 * <li>alpha and beta: parameters defined in the problem.
 * </ul>
 *
 * @author Ricardo Grunitzki
 */
public class BPRFunction extends AbstractCostFunction {

    @Override
    public double evalCost(AbstractEdge edge) {
        return (stringToDouble((String) edge.getParams().get("fftime"))
                * (1 + stringToDouble((String) edge.getParams().get("alpha"))
                * (float) Math.pow(edge.getTotalFlow() / stringToDouble((String) edge.getParams().get("capacity")),
                        stringToDouble((String) edge.getParams().get("beta")))));
    }

    @Override
    public double evalDesirableCost(AbstractEdge edge, double desirableFlow) {
        return (stringToDouble((String) edge.getParams().get("fftime"))
                * (1 + stringToDouble((String) edge.getParams().get("alpha"))
                * (float) Math.pow(desirableFlow / stringToDouble((String) edge.getParams().get("capacity")),
                        stringToDouble((String) edge.getParams().get("beta")))));
    }
}
