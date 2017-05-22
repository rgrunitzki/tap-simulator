package extensions.coadaptation;

import driver.learning.exploration.EpsilonDecreasing;
import driver.learning.*;
import driver.learning.reward.RewardFunction;
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
 * @author Ricardo Grunitzki
 */
public class CoadaptationExperiment {

    public static void main(String[] args) {

        Params.COLUMN_SEPARATOR = "\t";

        Params.PRINT_OD_PAIRS_AVG_COST = false;
        Params.PRINT_FLOWS = true;
        Params.PRINT_ON_TERMINAL = false;
        Params.PRINT_AVERAGE_RESULTS = true;
        Params.PRINT_ON_FILE = false;
        Params.RELATIVE_DELTA = -0.01;
        Params.PRINT_DELTA = false;
        Params.MAX_EPISODES = 1000;
        Params.MAX_STEPS = 100;
        EpsilonDecreasing.EPSILON_DECAY = 0.99f;
        EpsilonDecreasing.EPSILON_INITIAL = 1f;
        Params.REPETITIONS = 10;
        Params.DEFAULT_TAP = ImplementedTAP.OW;
//        Params.DEFAULT_EDGE = LearnerEdge.class;
        Params.PROPORTION = 1;

        int type = 2;
        switch (type) {
            case 1:
                //"QLStatefull"
//                Params.MAX_EPISODES = 150;
                QLStatefull.ALPHA = 0.5f;
                QLStatefull.GAMMA = 0.99f;
//                Params.REWARD_FUNCTION = RewardFunction.DIFFERENCE_REWARDS;
                Params.DEFAULT_ALGORITHM = QLStatefull.class;

                break;
            case 2:
                //"QLStateless"
                QLStateless.K = 10;
                Params.REWARD_FUNCTION = RewardFunction.STANDARD_REWARD;
                QLStateless.ALPHA = 0.1f;
                Params.DEFAULT_ALGORITHM = QLStateless.class;

                break;
            default:

        }

        Params.createTap();

        DefaultExperiment experiment = new DefaultExperiment();
        experiment.run();

//        for (int i = 0; i < 10; i++) {
//            Params.createTap();
//            Simulation simulation = new Simulation(Params.USED_TAP);
//            simulation.execute();
//            System.out.println(i + "\t" + simulation.averageTravelCost() + "\t" + simulation.getLearningEffort());
//            simulation.end();
//        }
    }
}
