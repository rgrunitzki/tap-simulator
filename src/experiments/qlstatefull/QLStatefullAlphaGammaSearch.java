package experiments.qlstatefull;

import driver.learning.QLStatefull;
import experiments.Experiment;
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
public class QLStatefullAlphaGammaSearch {

    public static TAP tap = TAP.SF(QLStatefull.class);

    //QLStateless on ortuzar network
    public static void main(String[] args) {
        System.out.println("QLStatefull - Alpha and Gamma search");

        Params.PRINT_ALL_EPISODES = false;
        Params.AVERAGE_RESULTS = true;
        Params.PRINT_PARAMS = false;

        Params.EPISODES = 1000;
        Params.E_DECAY_RATE = 0.99f;
        Params.STEPS = 100;
        Params.REWARDFUNCTION = RewardFunction.DifferenceRewards;

        QLStatefull.ALPHA = 0.9;
        QLStatefull.GAMMA = 0.99;

        int repetitions = 1;
//        Experiment experiment = new Experiment(repetitions, tap);
//        experiment.run();

        tap = TAP.OW(QLStatefull.class);

        System.out.println(Params.getParameterValues());
//        for (QLStatefull.GAMMA = 0.1; QLStatefull.GAMMA <= 1; QLStatefull.GAMMA += 0.1) {

//            tap = TAP.BP(QLStatefull.class);
            for (QLStatefull.ALPHA = 0.1f; QLStatefull.ALPHA <= 1.0f; QLStatefull.ALPHA += 0.1) {
                System.out.println("Gamma=" + QLStatefull.GAMMA + "\tAlpha=" + QLStatefull.ALPHA);
                Experiment experiment = new Experiment(repetitions, tap);
                experiment.run();
            }
//        }
    }

}
