package experiments;

import org.apache.commons.cli.ParseException;
import simulation.Params;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ricardo Grunitzki
 */
public class CommandLineExperiment {

    public static void main(String[] args) throws ParseException {
        
        Params.parseParams(args);
        
        DefaultExperiment experiment = new DefaultExperiment();
        experiment.run();
        
    }
}
