package extensions.hierarchical;

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
public class HierarchicalExperiment {

    public static void main(String[] args) {

        Params.COLUMN_SEPARATOR = "\t";

        Params.PRINT_OD_PAIRS_AVG_COST = false;
        Params.PRINT_FLOWS = false;
        Params.PRINT_ON_TERMINAL = false;
        Params.PRINT_AVERAGE_RESULTS = true;
        Params.PRINT_ON_FILE = false;

        Params.MAX_EPISODES = 150;
        Params.MAX_STEPS = 100;
        EpsilonDecreasing.EPSILON_DECAY = 0.91f;
        EpsilonDecreasing.EPSILON_INITIAL = 0.1f;
        Params.REPETITIONS = 1;
        Params.DEFAULT_TAP = ImplementedTAP.TWO_NEIGHBORHOOD;
        Params.PROPORTION = 1;

        int type = 0;
        switch (type) {
            case 0:
                QLStatefullHierarchical.ALPHA = 0.9f;
                QLStatefullHierarchical.GAMMA = 0.99f;
                Params.DEFAULT_ALGORITHM = QLStatefullHierarchical.class;
                break;
            case 1:
                //"QLStatefull"
//                Params.MAX_EPISODES = 150;
                QLStatefull.ALPHA = 0.9f;
                QLStatefull.GAMMA = 0.99f;
//                Params.REWARD_FUNCTION = RewardFunction.DIFFERENCE_REWARDS;
                Params.DEFAULT_ALGORITHM = QLStatefull.class;

                break;
            case 2:
                //"QLStateless"
                QLStateless.K = 9;
                Params.REWARD_FUNCTION = RewardFunction.DIFFERENCE_REWARDS;
                QLStateless.ALPHA = 0.9f;
                Params.DEFAULT_ALGORITHM = QLStateless.class;

                break;
            default:

        }

        Params.createTap();

        DefaultExperiment experiment = new DefaultExperiment();
        experiment.run();

    }
}
