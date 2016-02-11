package experiments.qlstatefull;

import driver.learning.QLStatefull;
import experiments.core.Experiment;
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
public class QLStatefullAlphaGammaSearchSF1 {

    public static TAP tap = TAP.SF(QLStatefull.class);

    //QLStateless on ortuzar network
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) {

        int alphaIndex = Integer.parseInt(args[0]);
        int gammaIndex = Integer.parseInt(args[1]);

        System.out.println("QLStatefull - Alpha and Gamma search");

        //improve this
        Params.PRINT_ALL_EPISODES = true;
        Params.AVERAGE_RESULTS = true;

        Params.PRINT_FLOWS = true;
        Params.PRINT_ALL_OD_PAIR = true;

        Params.EPISODES = 1000;
        Params.E_DECAY_RATE = 0.99f;
        Params.STEPS = 100;
        Params.REWARDFUNCTION = RewardFunction.StandardReward;
        
        double alphas[] = {0.3, 0.5, 0.7, 0.9};
        double gammas[] = {0.5, 0.6, 0.7, 0.8, 0.9, 0.99};

        QLStatefull.ALPHA = alphas[alphaIndex];
        QLStatefull.GAMMA = gammas[gammaIndex];
        int repetitions = 10;
        
        tap = TAP.SF(QLStatefull.class);

        Experiment experiment = new Experiment(repetitions, tap);
        experiment.run();

    }

    public static void printExperiment() {
        double alphas[] = {0.3, 0.5, 0.7, 0.9};
        double gammas[] = {0.5, 0.6, 0.7, 0.8, 0.9, 0.99};
        int cont = 1;
        for (int alpha = 0; alpha < alphas.length; alpha++) {
            for (int gamma = 0; gamma < gammas.length; gamma++) {
                System.out.println(cont + ") sudo nohup java -Xms5000m -XX:+UseParallelGC -cp tap-simulator.jar experiments.qlstatefull.QLStatefullAlphaGammaSearchSF1 " + alpha + " " + gamma + " > " + "results/#" + cont++ + "-" + alphas[alpha] + "-" + gammas[gamma] + ".txt&\n;;");
            }
        }
    }

}
