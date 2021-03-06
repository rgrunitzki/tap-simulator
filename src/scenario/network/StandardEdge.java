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
 * The standard structure for Edges in the simulator.
 *
 * @author Ricardo Grunitzki
 */
public class StandardEdge extends AbstractEdge {

    /**
     * Creates an instance of the Standard Edge.
     *
     * @param costFunction
     */
    public StandardEdge(AbstractCostFunction costFunction) {
        super(costFunction);
    }

}
