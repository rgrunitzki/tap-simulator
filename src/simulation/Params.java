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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import scenario.TAP;

/**
 *
 * @author rgrunitzki
 */
public class Params {

    //Simulation
    public static int EPISODES = 150;//
    public static int STEPS = 100;
    public static int CURRENT_EPISODE = 1;
    public static Class ALGORITHM = QLStatefull.class;
    public static TAP USED_TAP = TAP.OW(ALGORITHM);//
    public static int REPETITIONS = 1;//

    public static boolean PRINT_ALL_EPISODES = false;//
    public static boolean AVERAGE_RESULTS = false; //not been used

    public static boolean PRINT_ALL_OD_PAIR = false;//
    public static boolean PRINT_FLOWS = false;//
    public static String OUTPUTS_DIRECTORY = "results";//
    public static boolean PRINT_ON_FILE = false;//
    public static boolean PRINT_ON_TERMINAL = true;//

    //QLStateless
    public static float EPSILON = 1.0f;
    public static float E_DECAY_RATE = 0.99f;//
    public static final long SEED = System.currentTimeMillis();
    public static Random RANDOM = new Random();
    public static RewardFunction REWARD_FUNCTION = RewardFunction.STANDARD_REWARD;

    public static String SEPARATOR = " ";//
    public static String COMMENT = "#";//

    public static void parseParams(String[] args) throws ParseException {

        if (args.length == 0) {
            return;
        }

        Options options = new Options();

        options.addOption("f", "output.on-file", false, "print simulation output on file.");
        options.addOption("t", "output.on-terminal", false, "print simulation output on terminal.");
        options.addOption("o", "output.od-pairs", false, "print travel times by OD pair.");
        options.addOption("l", "output.link-flow", false, "print the flow of the links.");
        options.addOption("c", "output.column-char", false, "character used to separate columns. Default value is < >.");
        options.addOption("C", "output.comment-char", false, "character used to indicate a commented line.");
        options.addOption("d", "output.dir", false, "directory used to print the outputs.");
        options.addOption("a", "algorithm", true, "algorithm used to solve the traffic assignment problem. Accepted values are <QLStatefull>, <QLStateless>, <AoN>, <InA>, <MSA>.");
        options.addOption("n", "tap", true, "algorithm used to solve the traffic assignment problem. Accepted values are <ANA>, <BRAESS>, <BYPASS>, <EMME>, <ND>, <OW>, <SF>.");
        options.addOption("h", "help", false, "shows this message");
        options.addOption("d", "output.dir", true, "directory used to print the files. Default value is </results>");
        options.addOption("r", "runs", true, "number of repetitions of the experiment.");
        options.addOption("e", "ql.episodes", true, "number of episodes of each experiment.");
        options.addOption("epsilon", "ql.epsilon", true, "epsilon decay rate parameter of exploration.");
        options.addOption("reward", "ql.reward", true, "Reward function for QLearning-based methods. Avaiable values are Difference Rewards <DR>, Standard Reward <STD>, Reward Shaping <RS>.");
        options.addOption("alpha", "ql.alpha", true, "Alpha parameter of QLStateless and QLStatefull");
        options.addOption("k", "ql.k", true, "Number of routes used in QLStateless");
        options.addOption("gamma", "ql.gamma", true, "Gamma parameter of QLStatefull");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmdLine = parser.parse(options, args);

        if (cmdLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("tap-simulator", options);
            System.exit(1);
        } else {

            if (cmdLine.hasOption("algorithm")) {
                switch (cmdLine.getOptionValue("algorithm").toUpperCase()) {
                    case "QLSTATEFULL":
                        ALGORITHM = QLStatefull.class;
                        break;

                    case "QLSTATELESS":
                        ALGORITHM = QLStateless.class;
                        break;
                }
            }

            if (cmdLine.hasOption("ql.reward")) {
                switch (cmdLine.getParsedOptionValue("ql.reward").toString().toUpperCase()) {
                    case "STD":
                        REWARD_FUNCTION = RewardFunction.STANDARD_REWARD;
                        break;

                    case "DR":
                        REWARD_FUNCTION = RewardFunction.DIFFERENCE_REWARDS;
                        break;

                    case "RS":
                        REWARD_FUNCTION = RewardFunction.REWARD_SHAPING;
                        break;
                }
            }

            if (cmdLine.hasOption("output.comment-char")) {
                SEPARATOR = cmdLine.getOptionValue("output.comment-char");
            }

            PRINT_ON_FILE = cmdLine.hasOption("output.on-file");

            PRINT_FLOWS = cmdLine.hasOption("output.link-flow");

            if (cmdLine.hasOption("tap")) {

                switch (cmdLine.getOptionValue("tap").toUpperCase()) {

                    case "ANA":
                        USED_TAP = TAP.ANA(ALGORITHM);
                        break;
                    case "BYPASS":
                        USED_TAP = TAP.BYPASS(ALGORITHM);
                        break;
                    case "BRAESS":
                        USED_TAP = TAP.BRAESS(ALGORITHM);
                        break;
                    case "EMME":
                        USED_TAP = TAP.EMME(ALGORITHM);
                        break;
                    case "ND":
                        USED_TAP = TAP.ND(ALGORITHM);
                        break;
                    case "OW":
                        USED_TAP = TAP.OW(ALGORITHM);
                        break;
                    case "SF":
                        USED_TAP = TAP.SF(ALGORITHM);
                        break;
                    default:
                        USED_TAP = TAP.OW(ALGORITHM);
                        break;
                }
            }

            if (cmdLine.hasOption("runs")) {
                REPETITIONS = Integer.parseInt(cmdLine.getParsedOptionValue("runs").toString());
            }

            if (cmdLine.hasOption("ql.episodes")) {
                EPISODES = Integer.parseInt(cmdLine.getParsedOptionValue("ql.episodes").toString());
            }

            if (cmdLine.hasOption("ql.epsilon")) {
                E_DECAY_RATE = Float.parseFloat(cmdLine.getParsedOptionValue("ql.epsilon").toString());
            }

            if (cmdLine.hasOption("ql.alpha")) {
                QLStatefull.ALPHA = Double.parseDouble(cmdLine.getParsedOptionValue("ql.alpha").toString());
                QLStateless.ALPHA = Float.parseFloat(cmdLine.getParsedOptionValue("ql.alpha").toString());
            }

            if (cmdLine.hasOption("ql.gamma")) {
                QLStatefull.GAMMA = Double.parseDouble(cmdLine.getParsedOptionValue("ql.gamma").toString());
            }

            if (cmdLine.hasOption("ql.k")) {
                QLStateless.K = Integer.parseInt(cmdLine.getParsedOptionValue("ql.k").toString());
            }

            PRINT_ALL_OD_PAIR = cmdLine.hasOption("output.od-pairs");

            if (cmdLine.hasOption("output.dir")) {
                OUTPUTS_DIRECTORY = cmdLine.getOptionValue("output.dir");
            }

            if (cmdLine.hasOption("output.column-char")) {
                SEPARATOR = cmdLine.getOptionValue("output.column-char");
            }

            PRINT_ON_TERMINAL = cmdLine.hasOption("output.on-terminal");
        }

    }

}
