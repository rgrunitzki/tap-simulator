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
package experiments.tests;

import driver.learning.QLStatefull;
import driver.learning.QLStateless;
import driver.learning.QLambdaStatefull;
import driver.learning.exploration.EpsilonDecreasing;
import driver.learning.reward.RewardFunction;
import experiments.DefaultExperiment;
import scenario.ImplementedTAP;
import simulation.Params;
import simulation.Simulation;

/**
 *
 * @author Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
 */
public class EORPTests {

    public static void main(String[] args) {

        Params.COLUMN_SEPARATOR = "\t";

        Params.PRINT_OD_PAIRS_AVG_COST = false;
        Params.PRINT_FLOWS = false;
        Params.PRINT_ON_TERMINAL = true;
        Params.PRINT_AVERAGE_RESULTS = true;
        Params.PRINT_ON_FILE = false;

        Params.MAX_EPISODES = 1000;
        Params.MAX_STEPS = 100;
        EpsilonDecreasing.EPSILON_DECAY = 0.99f;
        DefaultExperiment.REPETITIONS = 5;
        Params.DEFAULT_TAP = ImplementedTAP.OW;
        Params.PROPORTION = 1;

        Params.REWARD_FUNCTION = RewardFunction.DIFFERENCE_REWARDS;

        int type = 0;
        switch (type) {
            case 0:
                //"QLStatefull"
                QLStatefull.GAMMA = 0.99;
                QLStatefull.ALPHA = 0.5f;//optimal value for difference rewards is 0.07
                //0.02 = 70.21
                //0.2 = 68.74                
                //0.5 = 68.62
                //0.9 = 70.03

                Params.DEFAULT_ALGORITHM = QLStatefull.class;
                break;
            case 1:
                //"QLambdaStatefull"
                EpsilonDecreasing.EPSILON_INITIAL = 1f;
                QLambdaStatefull.ALPHA = 0.9f;
                QLambdaStatefull.GAMMA = 0.99f;
                QLambdaStatefull.LAMBDA = 0.99;
                Params.DEFAULT_ALGORITHM = QLambdaStatefull.class;
                break;
            case 2:
                //"QLStateless"
                QLStateless.K = 8;
//                Params.REWARD_FUNCTION = RewardFunction.DIFFERENCE_REWARDS;
//                QLStateless.ALPHA = 0.5f;
                Params.DEFAULT_ALGORITHM = QLStateless.class;
                break;
            default:
                System.err.println("Algoritmo não identificado.");
        }

        Params.createTap();

        Simulation simulation = new Simulation(Params.USED_TAP);
        simulation.execute();
        simulation.end();
//        DefaultExperiment experiment = new DefaultExperiment();
//        experiment.run();

        System.out.println("");
    }

}
