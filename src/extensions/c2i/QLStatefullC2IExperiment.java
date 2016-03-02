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
        Params.DEFAULT_EDGE = EdgeC2I.class;

        Params.PRINT_ALL_OD_PAIR = false;
        Params.PRINT_FLOWS = false;
        Params.PRINT_ON_TERMINAL = false;
        Params.PRINT_AVERAGE_RESULTS = true;
        Params.PRINT_ON_FILE = true;

        Params.EPISODES = 1000;
        Params.MAX_STEPS = 100;
        Params.E_DECAY_RATE = 0.99f;
        Params.REPETITIONS = 10;
        Params.TAP_NAME = ImplementedTAP.OW;
        QLStatefullC2I.ALPHA = 0.5;
        QLStatefullC2I.GAMMA = 0.99;
        Params.createTap();

        QLStatefullC2I.INFORMATION_TYPE = InformationType.Best;
        DefaultExperiment experiment = new DefaultExperiment();
        experiment.run();
    }
}
