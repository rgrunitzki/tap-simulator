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

import simulation.Params;
import util.math.DynamicList;

/**
 *
 * @author Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
 */
public class DeltaVStopCriterion extends AbstractStopCriterion {

    private double lastTravelCost = 10.0d;
    private int currentEpisode = -1;
    private double absolutDeltaValue = 10.0;
    private double relativeDeltaValue = 0;
    private final DynamicList dynamicList = new DynamicList(Params.DELTA_INTERVAL);

    @Override
    public boolean stop() {
        return (dynamicList.check(Params.RELATIVE_DELTA, simulation.averageTravelCost())
                && Params.CURRENT_EPISODE > 1)
                || Params.CURRENT_EPISODE >= Params.MAX_EPISODES;
    }

    @Override
    public boolean stop(Double relativeDelta) {
        return (dynamicList.check(relativeDelta, simulation.averageTravelCost())
                && Params.CURRENT_EPISODE > 1)
                || Params.CURRENT_EPISODE >= Params.MAX_EPISODES;
    }

    @Override
    public double stoppingValue() {
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
