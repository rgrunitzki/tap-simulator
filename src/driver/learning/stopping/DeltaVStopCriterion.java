/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning.stopping;

import extensions.hierarchical.QLStatefullHierarchical;
import simulation.Params;
import simulation.Simulation;
import util.math.DynamicList;

/**
 *
 * @author rgrunitzki
 */
public class DeltaVStopCriterion extends AbstractStopCriterion {

    private double lastTravelCost = 10.0d;
    private int currentEpisode = -1;
    private double absolutDeltaValue = 10.0;
    private double relativeDeltaValue = 0;
    private final DynamicList dynamicList = new DynamicList(Params.DELTA_INTERVAL);

    @Override
    public boolean stop(Simulation simulation) {
        if (simulation.getTap().getDrivers().get(0) instanceof QLStatefullHierarchical
                && QLStatefullHierarchical.FIRST_LEVEL) {
            return Params.CURRENT_EPISODE >= Params.MAX_EPISODES;
        }

        return (dynamicList.check(Params.RELATIVE_DELTA, simulation.averageTravelCost())
                //        return (stoppingValue(simulation) <= Params.DELTA 
                && Params.CURRENT_EPISODE > 1)
                || Params.CURRENT_EPISODE >= Params.MAX_EPISODES;
    }

    @Override
    public double stoppingValue(Simulation simulation) {
        if (this.currentEpisode != Params.CURRENT_EPISODE) {
            //gets current travel time
            double currentTravelCost = simulation.averageTravelCost();
            //current travel time minus last travel time
            this.absolutDeltaValue = Math.abs(currentTravelCost - lastTravelCost);

            this.dynamicList.add(absolutDeltaValue);
            //update last travel cost
            this.lastTravelCost = currentTravelCost;
            //updates current episode
            this.currentEpisode = Params.CURRENT_EPISODE;
            //update relative delta value
            this.relativeDeltaValue = dynamicList.relativeValue(currentTravelCost, absolutDeltaValue);
        }
        return relativeDeltaValue;
    }

}
