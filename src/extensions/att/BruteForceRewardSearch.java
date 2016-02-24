package extensions.att;

import driver.learning.QLStateless;
import driver.learning.RewardFunction;
import scenario.ImplementedTAP;
import simulation.Params;
import simulation.Simulation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rgrunitzki
 */
public class BruteForceRewardSearch {

    public static void main(String[] args) {

        //Parameters Setting
        Params.REWARD_FUNCTION = RewardFunction.STANDARD_REWARD;
        Params.ALGORITHM = QLStateless.class;
        Params.PRINT_ALL_OD_PAIR = false;
        Params.PRINT_FLOWS = false;
        Params.PRINT_ON_TERMINAL = false;
        Params.PRINT_AVERAGE_RESULTS = false;
        Params.EPISODES = 150;
        Params.EPSILON = 0.91f;
        Params.REPETITIONS = 1;
        QLStateless.ALPHA = 0.5f;
        QLStateless.K = 8;
        Params.TAP_NAME = ImplementedTAP.OW;
        Params.createTap();
        
        //Experiment Definition

        Simulation simulation = null;
        for (float coeficient = 0.1f; coeficient <= 0.9f; coeficient += 0.1f) {
            simulation = new Simulation(Params.USED_TAP);
            simulation.execute();
            System.out.print("coeficient: " + coeficient + "\tresult: " + simulation.getSimulationOutputs());
            simulation.reset();
            simulation.end();
        }
    }
}
