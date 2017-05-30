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
 * Cost function of Braess Paradox network with 6 trips defined in:
 * <ul>
 * <li>K. Tumer and D. Wolpert, “Collective intelligence and Braess’ paradox,”
 * in Proceedings of the National Conference on Artificial Intelligence, 2000,
 * pp. 104–109.
 * </ul>
 *
 * @author Ricardo Grunitzki
 */
public class BraessParadoxCostFunction extends AbstractCostFunction {

    @Override
    public double evalCost(AbstractEdge edge) {
        return eval(((String) edge.getParams().get("operation")), edge.getTotalFlow(), (stringToDouble((String) edge.getParams().get("fftime"))));
    }

    @Override
    public double evalDesirableCost(AbstractEdge edge, double desirableFlow) {
        return eval(((String) edge.getParams().get("operation")), desirableFlow, (stringToDouble((String) edge.getParams().get("fftime"))));
    }

    private double eval(String operation, double flow, double ffttime) {
        if (operation.equalsIgnoreCase("*")) {
            if (flow < 1) {
                return ffttime;
            } else {
                return flow * ffttime;
            }
        } else {
            return flow + ffttime;
        }
    }
}
