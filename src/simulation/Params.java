/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import driver.learning.QLStatefull;
import driver.learning.QLStateless;
import driver.learning.RewardFunction;
import java.util.Random;

/**
 *
 * @author rgrunitzki
 */
public class Params {

    //Simulation
    public static int EPISODES = 150;
    public static int STEPS = 100;
    public static int EPISODE = 1;
    public static int DEMAND_SIZE = 0;

    public static boolean PRINT_ALL_EPISODES = true;
    public static boolean PRINT_PARAMS = true;
    public static boolean AVERAGE_RESULTS = false;

    //QLStateless
    public static float EPSILON = 1.0f;
    public static float E_DECAY_RATE = 0.99f;
    public static final long SEED = System.currentTimeMillis();
    public static Random RANDOM = new Random();
    public static RewardFunction REWARDFUNCTION = RewardFunction.StandardReward;

    public static String getParameterValues() {

        String params = "";
        params += "E_DECAY_RATE\t\t" + E_DECAY_RATE + "\n";
        params += "REWARDFUNCTION:\t\t" + REWARDFUNCTION.toString() + "\n";
        params += "QLStateless.ALPHA:\t\t" + QLStateless.ALPHA + "\n";
        params += "QLStateless.K:\t\t" + QLStateless.K + "\n";
        params += "QLStatefull.ALPHA:\t\t" + QLStatefull.ALPHA + "\n";
        params += "QLStatefull.GAMMA.K:\t\t" + QLStatefull.GAMMA + "\n";
        params += "SEED:\t\t" + SEED;
        return params;
    }

}
