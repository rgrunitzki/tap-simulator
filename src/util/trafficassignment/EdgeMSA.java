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
package util.trafficassignment;

import scenario.network.AbstractCostFunction;
import scenario.network.AbstractEdge;

/**
 *
 * @author Ricardo Grunitzki
 */
public class EdgeMSA extends AbstractEdge {

    private double msaFlow;

    public EdgeMSA(AbstractCostFunction costFunction) {
        super(costFunction);
        msaFlow = 0;
    }

    @Override
    protected double getWeight() {
        return getCostFunction().evalDesirableCost(this, msaFlow); //To change body of generated methods, choose Tools | Templates.
    }

    public double getMsaFlow() {
        return msaFlow;
    }

    public synchronized void setMsaFlow(double msaFlow) {
        this.msaFlow = msaFlow;
    }

}
