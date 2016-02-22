package experiments.c2i;

import driver.learning.QLStatefull;
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
        Params.DEFAULT_EDGE = EdgeC2I.class;
        Params.ALGORITHM = QLStatefull.class;
        
        Params.PRINT_ALL_OD_PAIR = false;
        Params.PRINT_FLOWS = false;
        Params.PRINT_ON_TERMINAL = true;
        Params.PRINT_AVERAGE_RESULTS = false;
        
        Params.EPISODES = 150;
        Params.EPSILON = 0.91f;
        Params.REPETITIONS = 1;
        Params.EPISODES = 100;
        Params.TAP_NAME = ImplementedTAP.BRAESS;
        Params.createTap();

//        QLStatefullC2I.INFORMATION_TYPE = InformationType.None;
//        QLStatefullC2I.ALPHA = 0.5;
//        QLStatefullC2I.GAMMA = 0.99;

        DefaultExperiment experiment = new DefaultExperiment();
        experiment.run();
    }
}
