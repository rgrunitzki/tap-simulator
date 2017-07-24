/* 
 * Copyright (C) 2017 Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

        /*
        * Know bugs for the current version:
        1) Sometimes options are created with loops, causing error.
        2) Sometimes the flow of one OD par stucks in states B7 and B8 (looping)
         */
 /*printing parameters*/
        Params.COLUMN_SEPARATOR = "\t";
        Params.PRINT_OD_PAIRS_AVG_COST = false;
        Params.PRINT_FLOWS = false;
        Params.PRINT_ON_TERMINAL = true;//
        Params.PRINT_AVERAGE_RESULTS = false;
        Params.PRINT_ON_FILE = false;
        Params.PRINT_RELATIVE_DELTA = true;
        /*stopping criterion parameters*/
        Params.RELATIVE_DELTA = 0.05;
        Params.DELTA_INTERVAL = 10;
        Params.MAX_EPISODES = 1000;
//        QLStatefullHierarchical.DELTA_FIRST_LEVEL = 0.1; //THIS WAS REMOVED 
        Params.DEFAULT_STOP_CRITERION = new DeltaQStopCriterion();
//        Params.DEFAULT_STOP_CRITERION = new NumberOfEpisodesStopCriterion();
//        Params.DEFAULT_STOP_CRITERION = new NumberOfEpisodesStopCriterion();
        /*Learning parameters*/
        Params.MAX_STEPS = 100;
        EpsilonDecreasing.EPSILON_DECAY = 0.99f;
        EpsilonDecreasing.EPSILON_INITIAL = 1f;
        DefaultExperiment.REPETITIONS = 1;
        Params.PROPORTION = 1;
        Params.DEFAULT_TAP = ImplementedTAP.TWO_NEIGHBORHOOD_REPLICATED;

        /*Experimentation settings*/
        int experimentType = 0;
        switch (experimentType) {
            case 0:
                QLStatefullHierarchical.ALPHA = 0.5;
                Params.DEFAULT_ALGORITHM = QLStatefullHierarchical.class;
                break;
            case 1:
                Params.DEFAULT_ALGORITHM = QLStatefull.class;
                break;
            case 2:
                QLStateless.K = 4;
                Params.REWARD_FUNCTION = RewardFunction.DIFFERENCE_REWARDS;
                Params.DEFAULT_ALGORITHM = QLStateless.class;
                break;
            default:
                System.err.println("Experiment type not recognized");
                System.exit(0);
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
