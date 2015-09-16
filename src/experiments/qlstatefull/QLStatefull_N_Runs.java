package experiments.qlstatefull;

import experiments.Experiment;
import driver.learning.QLStatefull;
import driver.learning.RewardFunction;
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
public class QLStatefull_N_Runs {

    //QLStateless on Nguyen and Dupuis 1984
    public static void main(String[] args) {

        Params.PRINT_ALL_EPISODES = true;
        Params.AVERAGE_RESULTS = false;

        Params.EPISODES = 1000;
        Params.E_DECAY_RATE = 0.99f;
        Params.STEPS = 100;
        Params.REWARDFUNCTION = RewardFunction.DifferenceRewards;

        QLStatefull.ALPHA = 0.99;
        QLStatefull.GAMMA = 0.99;

        TAP tap = TAP.OW(QLStatefull.class);
        int repetitions = 1;
        Experiment experiment = new Experiment(repetitions, tap);
        experiment.run();
    }
}
