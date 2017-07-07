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
package experiments.eorp;

import driver.learning.QLStatefull;
import driver.learning.QLStateless;
import driver.learning.QLambdaStatefull;
import driver.learning.SARSALambdaStatefull;
import driver.learning.SARSAStatefull;
import driver.learning.exploration.EpsilonDecreasing;
import driver.learning.reward.RewardFunction;
import experiments.DefaultExperiment;
import extensions.c2i.EdgeC2I;
import extensions.c2i.InformationType;
import extensions.c2i.QLStatefullC2I;
import scenario.ImplementedTAP;
import simulation.Params;

/**
 *
 * @author Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
 */
public class EligibilityTracesTests {

    public static void main(String[] args) {

        Params.COLUMN_SEPARATOR = "\t";

        Params.PRINT_OD_PAIRS_AVG_COST = true;
        Params.PRINT_FLOWS = true;
        Params.PRINT_ON_TERMINAL = true;
        Params.PRINT_AVERAGE_RESULTS = false;
        Params.PRINT_ON_FILE = false;

        Params.MAX_EPISODES = 1000;
        Params.MAX_STEPS = 100;
        EpsilonDecreasing.EPSILON_DECAY = 0.99f;
        DefaultExperiment.REPETITIONS = 1;
        Params.DEFAULT_TAP = ImplementedTAP.OW;
        Params.PROPORTION = 1;

        Params.REWARD_FUNCTION = RewardFunction.DIFFERENCE_REWARDS;

        int type = 6;
        switch (type) {
            case 1:
                //"SARSAStatefull"
                EpsilonDecreasing.EPSILON_INITIAL = 1f;
                SARSAStatefull.ALPHA = 0.4f;
                SARSAStatefull.GAMMA = 0.99f;
                Params.DEFAULT_ALGORITHM = SARSAStatefull.class;
                break;
            case 2:
                //"SARSALambdaStatefull"
                EpsilonDecreasing.EPSILON_INITIAL = 1f;
                SARSALambdaStatefull.ALPHA = 0.4f;
                SARSALambdaStatefull.GAMMA = 0.99f;
                SARSALambdaStatefull.LAMBDA = 0.9;
                Params.DEFAULT_ALGORITHM = SARSALambdaStatefull.class;
                break;

            case 3:
                //"QLStatefull"
                QLStatefull.GAMMA = 0.99;
                QLStatefull.ALPHA = 0.9f;
                Params.DEFAULT_ALGORITHM = QLStatefull.class;
                break;
            case 4:
                //"QLambdaStatefull"
                EpsilonDecreasing.EPSILON_INITIAL = 1f;
                QLambdaStatefull.ALPHA = 0.9f;
                QLambdaStatefull.GAMMA = 0.99f;
                QLambdaStatefull.LAMBDA = 0.99;
                Params.DEFAULT_ALGORITHM = QLambdaStatefull.class;
                break;

            case 5:
                //"QLStatefullC2I"
                QLStatefullC2I.ALPHA = 0.9f;
                QLStatefullC2I.GAMMA = 0.99f;
                QLStatefullC2I.COMMUNICATION_RATE = 0.25f;
                EpsilonDecreasing.EPSILON_INITIAL = 0.25f;
                Params.DEFAULT_ALGORITHM = QLStatefullC2I.class;
                Params.DEFAULT_EDGE = EdgeC2I.class;
                QLStatefullC2I.INFORMATION_TYPE = InformationType.Last;

                break;
            case 6:
                //"QLStateless"
                QLStateless.K = 8;
                Params.REWARD_FUNCTION = RewardFunction.STANDARD_REWARD;
                QLStateless.ALPHA = 0.5f;
                Params.DEFAULT_ALGORITHM = QLStateless.class;
        }

        Params.createTap();

//        Simulation simulation = new Simulation(Params.USED_TAP);
//        simulation.execute();
//        simulation.end();
        DefaultExperiment experiment = new DefaultExperiment();
        experiment.run();

        System.out.println("");
    }

}
