package experiments.wcci;

import driver.learning.QLStateless;
import driver.learning.RewardFunction;
import experiments.DefaultExperiment;
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
public class QLStatelessAlphaGammaSearch {


    //QLStateless on ortuzar network
    public static void main(String[] args) {
        System.out.println("QLStateless on Sioux Falls Network");

        Params.EPISODES = 1000;
        Params.E_DECAY_RATE = 0.99f;
        Params.STEPS = 100;
        
        Params.REWARD_FUNCTION = RewardFunction.DIFFERENCE_REWARDS;
        QLStateless.K = 8;
        QLStateless.ALPHA = 0.9f;

        for (QLStateless.K = 2; QLStateless.K <= 12; QLStateless.K++) {

            QLStateless.ksps = new ConcurrentHashMap<>();
            QLStateless.mdpPerOD = new ConcurrentHashMap<>();
            Params.USED_TAP = TAP.SF(Params.ALGORITHM);

            for (QLStateless.ALPHA = 0.1f; QLStateless.ALPHA <= 1.0f; QLStateless.ALPHA += 0.1) {
                System.out.println("K=" + QLStateless.K + "\tAlpha=" + QLStateless.ALPHA);
                DefaultExperiment experiment = new DefaultExperiment();
                experiment.run();
            }
        }
    }
}
