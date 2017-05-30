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

        Params.COLUMN_SEPARATOR = "\t";

        Params.PRINT_OD_PAIRS_AVG_COST = false;
        Params.PRINT_FLOWS = false;
        Params.PRINT_ON_TERMINAL = true;//
        Params.PRINT_AVERAGE_RESULTS = false;
        Params.PRINT_ON_FILE = false;
        Params.RELATIVE_DELTA = 0.0001;
        QLStatefullHierarchical.DELTA_FIRST_LEVEL = 0.8;
        Params.DELTA_INTERVAL = 3;
        Params.PRINT_DELTA = true;
        Params.MAX_EPISODES = 1000;
        Params.MAX_STEPS = 100;
        EpsilonDecreasing.EPSILON_DECAY = 0.99f;
        EpsilonDecreasing.EPSILON_INITIAL = 1f;
        Params.DEFAULT_STOP_CRITERION = new DeltaQStopCriterion();
//        Params.DEFAULT_STOP_CRITERION = new DeltaVStopCriterion();
//        Params.DEFAULT_STOP_CRITERION = new NumberOfEpisodesStopCriterion();
        Params.REPETITIONS = 1;
        Params.DEFAULT_TAP = ImplementedTAP.TWO_NEIGHBORHOOD_REPLICATED;
//        Params.DEFAULT_TAP = ImplementedTAP.OW;
//        Params.PROPORTION = 1;
//        QLStatefullHierarchical.FIRST_LEVEL = false;
        int experimentType = 1;
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
