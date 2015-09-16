package experiments.qlstateless;

import experiments.Experiment;
import driver.learning.QLStateless;
import driver.learning.RewardFunction;
import java.util.concurrent.ConcurrentHashMap;
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
public class QLStatelessSFStandard {

    public static TAP tap = TAP.SF(QLStateless.class);

    //QLStateless on ortuzar network
    public static void main(String[] args) {
        System.out.println("QLStateless on Sioux Falls Network");

        Params.PRINT_ALL_EPISODES = true;
        Params.AVERAGE_RESULTS = false;

        Params.EPISODES = 1000;
        Params.E_DECAY_RATE = 0.99f;
        Params.STEPS = 100;
        
        Params.REWARDFUNCTION = RewardFunction.DifferenceRewards;
        QLStateless.K = 8;
        QLStateless.ALPHA = 0.9f;

        int repetitions = 1;
//        Experiment experiment = new Experiment(repetitions, tap);
//        experiment.run();
        
        System.out.println(Params.getParameterValues());
        for (QLStateless.K = 2; QLStateless.K <= 12; QLStateless.K++) {

            QLStateless.ksps = new ConcurrentHashMap<>();
            QLStateless.mdpPerOD = new ConcurrentHashMap<>();
            tap = TAP.SF(QLStateless.class);

            for (QLStateless.ALPHA = 0.1f; QLStateless.ALPHA <= 1.0f; QLStateless.ALPHA += 0.1) {
                System.out.println("K=" + QLStateless.K + "\tAlpha=" + QLStateless.ALPHA);
                Experiment experiment = new Experiment(repetitions, tap);
                experiment.run();
            }
        }
    }

}
