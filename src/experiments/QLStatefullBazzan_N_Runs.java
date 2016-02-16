package experiments;

import driver.learning.QLStatefull;
import driver.learning.RewardFunction;
import experiments.Experiment;
import simulation.Params;
import scenario.TAP;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rgrunitzki
 */
public class QLStatefullBazzan_N_Runs {

    //QLStateless on Nguyen and Dupuis 1984
    public static void main(String[] args) {

        Params.PRINT_ALL_EPISODES = true;

        Params.EPISODES = 1000;
        Params.E_DECAY_RATE = 0.99f;
        Params.STEPS = 100;
        Params.REWARD_FUNCTION = RewardFunction.STANDARD_REWARD;

        QLStatefull.ALPHA = 0.3;
        QLStatefull.GAMMA = 0.99;
        
        QLStatefull.INFORMATION_TYPE = InformationType.Average;

        Experiment experiment = new Experiment();
        experiment.run();
    }
}
