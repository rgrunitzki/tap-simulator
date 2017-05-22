/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning.stopping;

import simulation.Simulation;

/**
 *
 * @author rgrunitzki
 */
public abstract class AbstractStopCriterion {

    public abstract boolean stop(Simulation simulation);
    
    public abstract double stoppingValue(Simulation simulation);
}
