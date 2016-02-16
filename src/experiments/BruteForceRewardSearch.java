package experiments;

import experiments.Experiment;
import driver.learning.QLStateless;
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
public class BruteForceRewardSearch {

    //QLStateless on Nguyen and Dupuis 1984
    public static void main(String[] args) {

        Params.PRINT_ALL_EPISODES = true;

        Params.EPISODES = 3000;
        Params.E_DECAY_RATE = 0.995f;
        Params.STEPS = 100;
        Params.REWARD_FUNCTION = RewardFunction.REWARD_SHAPING;

        QLStateless.ALPHA = 0.9f;
        QLStateless.K = 8;                  

//        for (float coeficient = 0.1f; coeficient <= 1; coeficient += 0.1f) {
        Experiment experiment = new Experiment();

//            System.out.println("coeficient: " + coeficient + "\tresult: ");
        experiment.run();

//        }
    }
}
