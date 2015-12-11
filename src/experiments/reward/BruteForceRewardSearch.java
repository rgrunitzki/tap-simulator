package experiments.reward;

import experiments.Experiment;
import driver.learning.QLStatefull;
import driver.learning.QLStateless;
import driver.learning.RewardFunction;
import oracle.jrockit.jfr.tools.ConCatRepository;
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
        Params.AVERAGE_RESULTS = false;
        Params.PRINT_PARAMS = false;

        Params.EPISODES = 3000;
        Params.E_DECAY_RATE = 0.995f;
        Params.STEPS = 100;
        Params.REWARDFUNCTION = RewardFunction.RewardShaping;

        QLStateless.ALPHA = 0.9f;
        QLStateless.K = 8;                  

        int repetitions = 1;
        TAP tap = TAP.OW(QLStateless.class);

//        for (float coeficient = 0.1f; coeficient <= 1; coeficient += 0.1f) {
        Experiment experiment = new Experiment(repetitions, tap);

//            System.out.println("coeficient: " + coeficient + "\tresult: ");
        experiment.run();

//        }
    }
}
