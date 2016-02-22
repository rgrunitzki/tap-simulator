package experiments.wcci;

import driver.learning.QLStatefull;
import driver.learning.RewardFunction;
import experiments.DefaultExperiment;
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
public class QLStatefullAlphaGammaSearch {

    //QLStateless on ortuzar network
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) {

        int alphaIndex = 3;
        int gammaIndex = 5;

        if (args.length > 0) {
            alphaIndex = Integer.parseInt(args[0]);
            gammaIndex = Integer.parseInt(args[1]);
        }

        System.out.println("QLStatefull - Alpha and Gamma search");

        //improve this
        Params.PRINT_FLOWS = true;
        Params.PRINT_ALL_OD_PAIR = true;

        Params.EPISODES = 150;
        Params.E_DECAY_RATE = 0.91f;
        Params.STEPS = 100;
        Params.REWARD_FUNCTION = RewardFunction.STANDARD_REWARD;

        double alphas[] = {0.3, 0.5, 0.7, 0.9};
        double gammas[] = {0.5, 0.6, 0.7, 0.8, 0.9, 0.99};

        QLStatefull.ALPHA = alphas[alphaIndex];
        QLStatefull.GAMMA = gammas[gammaIndex];

        DefaultExperiment experiment = new DefaultExperiment();
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
