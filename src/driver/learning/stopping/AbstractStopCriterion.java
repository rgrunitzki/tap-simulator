/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning.stopping;

import simulation.Simulation;

/**
 *
 * @author Ricardo Grunitzki
 */
public abstract class AbstractStopCriterion {

    protected Simulation simulation;
    private boolean constraint = false;

    public abstract boolean stop();

    public abstract boolean stop(Double relativeDelta);

    public abstract double stoppingValue();

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public boolean isConstraint() {
        return constraint;
    }

    public void setConstraint(boolean constraint) {
        this.constraint = constraint;
    }

    public void reset() {
        this.constraint = false;
    }

}
