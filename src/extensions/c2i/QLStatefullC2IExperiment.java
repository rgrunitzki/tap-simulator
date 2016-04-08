package extensions.c2i;

import driver.learning.*;
import driver.learning.RewardFunction;
import experiments.DefaultExperiment;
import scenario.ImplementedTAP;
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
public class QLStatefullC2IExperiment {

    //QLStateless on Nguyen and Dupuis 1984
    public static void main(String[] args) {

        //Parameters Setting
        Params.REWARD_FUNCTION = RewardFunction.STANDARD_REWARD;
        Params.ALGORITHM = QLStatefullC2I.class;
//        Params.ALGORITHM = QLStatefull.class;
//        Params.ALGORITHM = QLStateless.class;
        Params.DEFAULT_EDGE = EdgeC2I.class;
//        Params.EXPLORATION_POLICY = C2IEpsilonGreedy.class;

        Params.PRINT_ALL_OD_PAIR = false;
        Params.PRINT_FLOWS = false;
        Params.PRINT_ON_TERMINAL = true;
        Params.PRINT_AVERAGE_RESULTS = false;
        Params.PRINT_ON_FILE = false;

        Params.EPISODES = 1000;
        Params.MAX_STEPS = 50;
        Params.E_DECAY_RATE = 0.99f;
        Params.REPETITIONS = 1;
        Params.TAP_NAME = ImplementedTAP.OW;

        SARSAStatefull.ALPHA = 0.5f;
        SARSAStatefull.GAMMA = 0.99f;

        QLStatefull.ALPHA = 0.2f;
        QLStatefull.GAMMA = 0.99f;

        QLStatefullC2I.ALPHA = 0.5f;
        QLStatefullC2I.GAMMA = 0.99f;
        Params.EPSILON = 0.5f;

        QLStateless.K = 8;
        QLStateless.ALPHA = 0.1f;

        Params.createTap();

        QLStatefullC2I.INFORMATION_TYPE = InformationType.Average;
        DefaultExperiment experiment = new DefaultExperiment();
        experiment.run();

//        Simulation simulation = null;
//        double alphas[] = {0.3, 0.5, 0.7, 0.9};
//        double gammas[] = {0.8, 0.9, 0.99};
//        for (int alpha = 0; alpha < alphas.length; alpha++) {
//            for (int gamma = 0; gamma < gammas.length; gamma++) {
//                QLStatefullC2I.ALPHA = alphas[alpha];
//                QLStatefullC2I.GAMMA = gammas[gamma];
//                simulation = new Simulation(Params.USED_TAP);
//                simulation.execute();
//                System.out.format("%s %s %.2f\n", alphas[alpha], gammas[gamma],
//                        Double.parseDouble(simulation.getSimulationOutputs().split(" ")[1]));
//                simulation.reset();
//                simulation.end();
//            }
//        }
    }
}
