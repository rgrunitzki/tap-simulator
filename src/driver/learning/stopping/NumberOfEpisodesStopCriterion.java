/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning.stopping;

import simulation.Params;

/**
 * Ends the simulation process
 *
 * @author rgrunitzki
 */
public class NumberOfEpisodesStopCriterion extends AbstractStopCriterion {

    @Override
    public boolean stop() {
        return !isConstraint() && Params.CURRENT_EPISODE >= Params.MAX_EPISODES;
    }

    @Override
    public boolean stop(Double criteria) {
        return this.stop();
    }

    @Override
    public double stoppingValue() {
        return Params.CURRENT_EPISODE;
    }

}
