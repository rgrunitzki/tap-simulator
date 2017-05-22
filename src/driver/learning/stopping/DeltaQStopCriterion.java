/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning.stopping;

import driver.Driver;
import extensions.hierarchical.QLStatefullHierarchical;
import simulation.Params;
import simulation.Simulation;

/**
 *
 * @author rgrunitzki
 */
public class DeltaQStopCriterion extends AbstractStopCriterion {

    @Override
    public boolean stop(Simulation simulation) {
        if (simulation.getTap().getDrivers().get(0) instanceof QLStatefullHierarchical
                && QLStatefullHierarchical.FIRST_LEVEL) {
            return Params.CURRENT_EPISODE >= Params.MAX_EPISODES;
        }

        return (stoppingValue(simulation) <= Params.RELATIVE_DELTA && Params.CURRENT_EPISODE > 0)
                || Params.CURRENT_EPISODE >= Params.MAX_EPISODES;
    }

    @Override
    public double stoppingValue(Simulation simulation) {
        double maxDetaQ = simulation.getTap().getDrivers().get(0).getDeltaQ();
        for (Driver d : simulation.getTap().getDrivers()) {
            if (d.getDeltaQ() < maxDetaQ) {
                maxDetaQ = d.getDeltaQ();
            }
        }
        return relativeValue(Math.abs(maxDetaQ), simulation.averageTravelCost());
//        return Math.abs(maxDetaQ);
    }

    private double relativeValue(double delta, double travelTime) {
        return (delta * 100) / travelTime;
    }

}
