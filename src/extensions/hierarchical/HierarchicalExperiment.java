package extensions.hierarchical;

import driver.learning.exploration.EpsilonDecreasing;
import driver.learning.*;
import driver.learning.reward.RewardFunction;
import driver.learning.stopping.*;
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
        Params.PRINT_FLOWS = true;
        Params.PRINT_ON_TERMINAL = true;//
        Params.PRINT_AVERAGE_RESULTS = true;
        Params.PRINT_ON_FILE = false;
        Params.RELATIVE_DELTA = 0.001;
        QLStatefullHierarchical.DELTA_SECOND_LEVEL = 0.000005;
        Params.DELTA_INTERVAL = 5;
        Params.PRINT_DELTA = true;
        Params.MAX_EPISODES = 150;
        Params.MAX_STEPS = 100;
        EpsilonDecreasing.EPSILON_DECAY = 0.91f;
        EpsilonDecreasing.EPSILON_INITIAL = 1f;
        Params.DEFAULT_STOP_CRITERION = new DeltaQStopCriterion();
//        Params.DEFAULT_STOP_CRITERION = new DeltaVStopCriterion();
//        Params.DEFAULT_STOP_CRITERION = new NumberOfEpisodesStopCriterion();
        Params.REPETITIONS = 1;
        Params.DEFAULT_TAP = ImplementedTAP.TWO_NEIGHBORHOOD_REPLICATED;
//        Params.DEFAULT_TAP = ImplementedTAP.OW;
//        Params.PROPORTION = 1;
        int experimentType = 0;
        switch (experimentType) {
            case 0:
                QLStatefullHierarchical.ALPHA = 0.5f;
                QLStatefullHierarchical.GAMMA = 0.99f;
                Params.DEFAULT_ALGORITHM = QLStatefullHierarchical.class;
                break;
            case 1:
                //"QLStatefull"
//                Params.MAX_EPISODES = 150;
                QLStatefull.ALPHA = 0.5f;
                QLStatefull.GAMMA = 0.99f;
                Params.DEFAULT_ALGORITHM = QLStatefull.class;

                break;
            case 2:
                //"QLStateless"
                QLStateless.K = 3;
                Params.REWARD_FUNCTION = RewardFunction.DIFFERENCE_REWARDS;
                QLStateless.ALPHA = 0.9f;
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
