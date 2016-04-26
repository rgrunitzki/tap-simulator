package extensions.att;

import driver.learning.QLStatefull;
import driver.learning.RewardFunction;
import scenario.ImplementedTAP;
import simulation.Params;
import simulation.Simulation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ricardo Grunitzki
 */
public class BruteForceRewardSearch {

    public static void main(String[] args) {
        
        double alpha1 = Double.parseDouble(args[0]);
        double alpha2 = Double.parseDouble(args[0]);
        double alpha3 = Double.parseDouble(args[0]);

        //Parameters Setting
        Params.REWARD_FUNCTION = RewardFunction.STANDARD_REWARD;
        Params.DEFAULT_ALGORITHM = QLStatefull.class;
        Params.PRINT_OD_PAIRS_AVG_COST = false;
        Params.PRINT_FLOWS = false;
        Params.PRINT_ON_TERMINAL = false;
        Params.PRINT_AVERAGE_RESULTS = false;
        Params.MAX_EPISODES = 100;
        Params.EPSILON_DECAY = 0.9f;
        Params.REPETITIONS = 1;
        QLStatefull.ALPHA = 0.5f;
        QLStatefull.GAMMA = 0.99f;
        Params.DEFAULT_TAP = ImplementedTAP.OW;
        Params.MAX_STEPS = 50;
        Params.createTap();

        //Experiment Definition
        Simulation simulation = null;

        System.out.println("a1 a2 a3 a4 tt");
        //destination
//        for (StatefullRewardFunctionATT.alpha[0] = -1.0; StatefullRewardFunctionATT.alpha[0] <= 1.0; StatefullRewardFunctionATT.alpha[0] += 0.5) {
        //uturn?
//            for (StatefullRewardFunctionATT.alpha[1] = -1.0; StatefullRewardFunctionATT.alpha[1] <= 1.0; StatefullRewardFunctionATT.alpha[1] += 0.5) {
        //travelled
        //for (StatefullRewardFunctionATT.alpha[2] = -1.0; StatefullRewardFunctionATT.alpha[2] <= 1.0; StatefullRewardFunctionATT.alpha[2] += 1) {
        //otherwise
//                    for (StatefullRewardFunctionATT.alpha[3] = -1.0; StatefullRewardFunctionATT.alpha[3] <= 1.0; StatefullRewardFunctionATT.alpha[3] += 0.5) {
        
        
        simulation = new Simulation(Params.USED_TAP);
        simulation.execute();
//        System.out.format("%.2f %.2f %.2f %.2f %.4f\n", StatefullRewardFunctionATT.alpha[0], StatefullRewardFunctionATT.alpha[1], StatefullRewardFunctionATT.alpha[2], StatefullRewardFunctionATT.alpha[3], Double.parseDouble(simulation.getSimulationOutputs().split(" ")[1]));
        simulation.reset();
        simulation.end();
    }
}
//            }
//        }

//    }
//}
