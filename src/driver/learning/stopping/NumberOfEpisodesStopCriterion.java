/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning.stopping;

import simulation.Params;
import simulation.Simulation;

/**
 * Ends the simulation process always the nubmer of
 *
 * @author rgrunitzki
 */
public class NumberOfEpisodesStopCriterion extends AbstractStopCriterion {

    @Override
    public boolean stop(Simulation simulation) {
        return Params.CURRENT_EPISODE >= Params.MAX_EPISODES;
    }

    @Override
    public double stoppingValue(Simulation simulation) {
        return Params.CURRENT_EPISODE;
    }

}
