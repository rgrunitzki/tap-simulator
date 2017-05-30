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
package extensions.c2i;

import driver.learning.exploration.EpsilonDecreasing;
import driver.Driver;
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
public class QLStatefullC2IExperiment {

    //QLStateless on Nguyen and Dupuis 1984
    public static void main(String[] args) {
        
        Params.COLUMN_SEPARATOR="\t";

        Params.PRINT_OD_PAIRS_AVG_COST = false;
        Params.PRINT_FLOWS = false;
        Params.PRINT_ON_TERMINAL = true;
        Params.PRINT_AVERAGE_RESULTS = false;
        Params.PRINT_ON_FILE = false;

        Params.MAX_EPISODES = 1000;
        Params.MAX_STEPS = 100;
        EpsilonDecreasing.EPSILON_DECAY = 0.99f;
        Params.REPETITIONS = 1;
        Params.DEFAULT_TAP = ImplementedTAP.OW;
        Params.PROPORTION = 1;

        int type = 3;
        switch (type) {
            case 1:
                //"QLStatefull"
                EpsilonDecreasing.EPSILON_INITIAL = 1f;
                QLStatefull.ALPHA = 0.9f;
                QLStatefull.GAMMA = 0.99f;
//                Params.REWARD_FUNCTION = RewardFunction.DIFFERENCE_REWARDS;
                Params.DEFAULT_ALGORITHM = QLStatefull.class;

                break;
            case 2:
                //"QLStatefullC2I"
                QLStatefullC2I.ALPHA = 0.9f;
                QLStatefullC2I.GAMMA = 0.99f;
                QLStatefullC2I.COMMUNICATION_RATE = 0.25f;
                EpsilonDecreasing.EPSILON_INITIAL = 0.25f;
                Params.DEFAULT_ALGORITHM = QLStatefullC2I.class;
                Params.DEFAULT_EDGE = EdgeC2I.class;
                QLStatefullC2I.INFORMATION_TYPE = InformationType.Last;

                break;
            case 3:
                //"QLStateless"
                QLStateless.K = 9;
                Params.REWARD_FUNCTION = RewardFunction.DIFFERENCE_REWARDS;
                QLStateless.ALPHA = 0.9f;
                Params.DEFAULT_ALGORITHM = QLStateless.class;

                break;
            case 4:
                //"SARSAStatefull":
                SARSAStatefull.ALPHA = 0.5f;
                SARSAStatefull.GAMMA = 0.99f;
                Params.DEFAULT_ALGORITHM = SARSAStatefull.class;
                break;
            default:

        }

        Params.createTap();

        DefaultExperiment experiment = new DefaultExperiment();
        experiment.run();

//        Simulation simulation = null;
//        double alphas[] = {0.3, 0.5, 0.7, 0.9};
//        double gammas[] = {0.8, 0.9, 0.99};
//        for (int alpha = 0; alpha < alphas.length; alpha++) {
//            for (int gamma = 0; gamma < gammas.length; gamma++) {
//                QLStatefullC2I.ALPHA = alphas[alpha];
//                QLStatefullC2I.GAMMA = gammas[gamma];
//                simulation = new Simulation(Params.USED_TAP);
//                simulation.execute();
//                System.out.format("%s %s %.2f\n", alphas[alpha], gammas[gamma],
//                        Double.parseDouble(simulation.getSimulationOutputs().split(" ")[1]));
//                simulation.reset();
//                simulation.end();
//            }
//        }
    }
}
