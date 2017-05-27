/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning.stopping;

import driver.Driver;
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
        //delta value
        double relativeDeltaValue = dynamicList.relativeValue(simulation.averageTravelCost(), Math.abs(maxDetaQ));
        //update relative delta value
        dynamicList.add(relativeDeltaValue);
        return relativeDeltaValue;
    }

    @Override
    public void reset() {
        //resets the list of elements
        this.dynamicList.reset();
    }

}
