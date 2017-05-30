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
 * This object represents the cost function of the edge.
 *
 * @author Ricardo Grunitzki
 */
public abstract class AbstractCostFunction {

    /**
     * Returns the the cost function of the edge.
     *
     * @param edge evaluated edge
     * @return cost value
     */
    public abstract double evalCost(AbstractEdge edge);

    /**
     * Returns the cost function of the edge for a specific flow.
     *
     * @param edge evaluated edge
     * @param desirableFlow desirable flow
     * @return cost value
     */
    public abstract double evalDesirableCost(AbstractEdge edge, double desirableFlow);

    /**
     * Converts an String object to a double object
     *
     * @param value String value
     * @return Double value
     */
    protected Double stringToDouble(String value) {
        if (value.equals("")) {
            return 0.0;
        } else {
            return Double.parseDouble(value);
        }
    }
}
