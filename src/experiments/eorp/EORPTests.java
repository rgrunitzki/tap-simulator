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

        Params.PRINT_OD_PAIRS_AVG_COST = true;
        Params.PRINT_FLOWS = true;
        Params.PRINT_ON_TERMINAL = false;
        Params.PRINT_AVERAGE_RESULTS = true;
        Params.PRINT_ON_FILE = true;
        Params.PRINT_RELATIVE_DELTA = false;

        Params.MAX_EPISODES = 1000;
        Params.MAX_STEPS = 100;
        EpsilonDecreasing.EPSILON_DECAY = 0.99f;
        DefaultExperiment.REPETITIONS = 30;
        Params.DEFAULT_TAP = ImplementedTAP.ND;
        Params.PROPORTION = 1;
        Params.COLUMN_SEPARATOR = ";";

        Params.REWARD_FUNCTION = RewardFunction.DIFFERENCE_REWARDS;

        int type = 0;
        switch (type) {
            case 0:
                //"QLStatefull"
                QLStatefull.GAMMA = 0.99;
                QLStatefull.ALPHA = 0.5f;
                Params.DEFAULT_ALGORITHM = QLStatefull.class;
                break;
            case 1:
                //"QLStateless"
                QLStateless.K = 7;
                QLStateless.ALPHA = 0.5f;
                Params.DEFAULT_ALGORITHM = QLStateless.class;
                break;
            default:
                System.err.println("Algorithm not recognized.");
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
