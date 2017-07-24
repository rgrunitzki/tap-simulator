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
package driver.learning.stopping;

import driver.Driver;
import extensions.hierarchical.QLStatefullHierarchical;
import simulation.Params;
import util.math.DynamicList;

/**
 *
 * @author Ricardo Grunitzki
 */
public class DeltaQStopCriterion extends AbstractStopCriterion {

    private final DynamicList dynamicList = new DynamicList(Params.DELTA_INTERVAL);

    @Override
    public boolean stop() {
        boolean value
                = !isConstraint()
                //                && (stoppingValue() <= Params.RELATIVE_DELTA && Params.CURRENT_EPISODE > 0)
                && (dynamicList.check(Params.RELATIVE_DELTA, simulation.averageTravelCost()) && Params.CURRENT_EPISODE > 0)
                || Params.CURRENT_EPISODE >= Params.MAX_EPISODES;
        return value;
    }

    @Override
    public boolean stop(Double relativeDelta) {
        boolean value = (dynamicList.check(relativeDelta, simulation.averageTravelCost()) && Params.CURRENT_EPISODE > 0);
        return value;
    }

    @Override
    public double stoppingValue() {
        double maxDetaQ = simulation.getTap().getDrivers().get(0).getDeltaQ();
        for (Driver d : simulation.getTap().getDrivers()) {
            if (d.getDeltaQ() < maxDetaQ) {
                maxDetaQ = d.getDeltaQ();
            }
        }

        double deltaValue;

        //delta value
//        if (QLStatefullHierarchical.FIRST_LEVEL) {
//            deltaValue = Math.abs(maxDetaQ);
//        } else {
            deltaValue = dynamicList.relativeValue(simulation.averageTravelCost(), Math.abs(maxDetaQ));
//        }
        //deltaValue = dynamicList.relativeValue(simulation.averageTravelCost(), Math.abs(maxDetaQ));
        //update relative delta value
        dynamicList.add(deltaValue);
        return deltaValue;
    }

    @Override
    public void reset() {
        //resets the list of elements
        this.dynamicList.reset();
    }

}
