package experiments;

import experiments.Experiment;
import driver.learning.QLStateless;
import org.apache.commons.cli.ParseException;
import scenario.TAP;
import simulation.Params;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rgrunitzki
 */
public class ExperimentRunner {

    public static void main(String[] args) throws ParseException {
        
        Params.parseParams(args);
        
        Experiment experiment = new Experiment();
        experiment.run();
        
    }
}
