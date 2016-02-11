package experiments.qlstatefull;

import driver.learning.QLStatefull;
import driver.learning.RewardFunction;
import experiments.core.Experiment;
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
public class QLStatefullAlphaGammaSearchOW {

    public static TAP tap = TAP.OW(QLStatefull.class);

    //QLStateless on ortuzar network
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) {
        System.out.println("QLStatefull - Alpha and Gamma search");

        //improve this
        Params.PRINT_ALL_EPISODES = true;
        Params.AVERAGE_RESULTS = true;

        Params.PRINT_FLOWS = true;
        Params.PRINT_ALL_OD_PAIR = true;
        Params.PRINT_ON_FILE = false;

        Params.EPISODES = 1000;
        Params.E_DECAY_RATE = 0.99f;
        Params.STEPS = 100;
        Params.REWARDFUNCTION = RewardFunction.StandardReward;

        double alphas[] = {0.9};
        double gammas[] = {0.99};
//        double alphas[] = {0.3, 0.5, 0.7};
//        double gammas[] = {0.5, 0.6, 0.7, 0.8, 0.9, 0.99};

        int repetitions = 1;
        tap = TAP.OW(QLStatefull.class);

        for (double alpha : alphas) {
            QLStatefull.ALPHA = alpha;
            for (double gamma : gammas) {
                QLStatefull.GAMMA = gamma;
                Experiment experiment = new Experiment(repetitions, tap);
                
                experiment.run();
            }
        }

    }

}
