package simulation;

import driver.learning.stopping.AbstractStopCriterion;
import driver.learning.stopping.NumberOfEpisodesStopCriterion;
import driver.learning.exploration.EpsilonDecreasing;
import driver.learning.QLStatefull;
import extensions.c2i.QLStatefullC2I;
import driver.learning.QLStateless;
import driver.learning.reward.RewardFunction;
import driver.learning.exploration.SoftMaxExploration;
import extensions.c2i.EdgeC2I;
import extensions.c2i.InformationType;
import java.util.Random;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import scenario.network.StandardEdge;
import scenario.ImplementedTAP;
import scenario.TAP;

/**
 *
 * @author Ricardo Grunitzki
 */
public class Params {

    //Simulation
    public static int MAX_EPISODES = 1000;//
    public static int MAX_STEPS = 100;
    public static int CURRENT_STEP = 0;
    public static int CURRENT_EPISODE = 1;
    public static TAP USED_TAP = null;//
    public static int REPETITIONS = 1;//
    public static final long RAMDON_SEED = System.currentTimeMillis();
    public static Random RANDOM = new Random();
    /**
     * Proportion of learning agents per trip. For instance: - value = 1 means
     * that it will be one learning agent per unit of trip; - value = 100 means
     * that it will be one learning agent for each 100 trips; All trips will use
     * the route the agent is learning.
     */
    public static int PROPORTION = 1;
    public static ImplementedTAP DEFAULT_TAP = ImplementedTAP.OW;
    public static Class DEFAULT_EDGE = StandardEdge.class;
    //Learning Parameters
    public static RewardFunction REWARD_FUNCTION = RewardFunction.STANDARD_REWARD;
    public static Class EXPLORATION_POLICY = EpsilonDecreasing.class;
    public static Class DEFAULT_ALGORITHM = QLStatefull.class;
    public static double RELATIVE_DELTA = 0.01;
    public static int DELTA_INTERVAL = 3;
    public static boolean PRINT_DELTA = false;
    public static boolean PRINT_EFFORT = true;
    public static AbstractStopCriterion DEFAULT_STOP_CRITERION = new NumberOfEpisodesStopCriterion();
    //Outputs Parameters
    public static boolean PRINT_AVERAGE_RESULTS = false; //not been used (problems here)
    public static boolean PRINT_OD_PAIRS_AVG_COST = false;
    public static boolean PRINT_FLOWS = false;
    public static boolean PRINT_ON_FILE = false;
    public static boolean PRINT_ON_TERMINAL = true;
    public static String OUTPUT_DIRECTORY = "results";
    public static String COLUMN_SEPARATOR = " ";
    public static String COMMENT_CHARACTER = "#";

    public static void parseParams(String[] args) throws ParseException {

        if (args.length == 0) {
            System.exit(1);
        }

        Options options = new Options();

        options.addOption("f", "output.on-file", false, "print simulation output on file.");
        options.addOption("t", "output.on-terminal", false, "print simulation output on terminal.");
        options.addOption("o", "output.od-pairs", false, "print travel times by OD pair.");
        options.addOption("l", "output.link-flow", false, "print the flow of the links.");
        options.addOption("c", "output.column-char", false, "character used to separate columns. Default value is < >.");
        options.addOption("C", "output.comment-char", false, "character used to indicate a commented line.");
        options.addOption("d", "output.dir", false, "directory used to print the outputs.");
        options.addOption("a", "algorithm", true, "algorithm used to solve the traffic assignment problem. Accepted values are <QLStatefull>, <QLStatefullC2I>, <QLStateless>, <AoN>, <InA>, <MSA>.");
//        options.addOption("i", "info.type", true, "Information type used by QLStatefullC2I. Accepted values are <None>, <Average>, <Best>, <Last>.");
        options.addOption("n", "tap", true, "algorithm used to solve the traffic assignment problem. Accepted values are <ANA>, <BRAESS>, <BYPASS>, <EMME>, <ND>, <OW>, <SF>.");
        options.addOption("h", "help", false, "shows this message");
        options.addOption("d", "output.dir", true, "directory used to print the files. Default value is </results>");
        options.addOption("p", "proportion", true, "proportion of learning agents per trip of od pair. default value is <1> (one trip for each learning agent). Example of accepted valeus are <100> (100 trips per learning agents), <10>, <1000>.");
        options.addOption("r", "runs", true, "number of repetitions of the experiment.");
        options.addOption("e", "ql.episodes", true, "number of episodes of each experiment.");
        options.addOption("E", "exploration.strategy", true, "exploration strategy. Accepted values are Epsilon-Decreasing <EGreedy> (Default), Softmax <Softmax>");
        options.addOption("epsilon", "ql.epsilon", true, "initial epsilon parameter of exploration. Default value is <1.0> (corresponds to 100% of initial exploration)");
        options.addOption("edecay", "ql.epsilon-decay", true, "epsilon decay rate parameter of exploration.");
        options.addOption("reward", "ql.reward", true, "Reward function for QLearning-based methods. Avaiable values are Difference Rewards <DR>, Standard Reward <STD>, Reward Shaping <RS>.");
        options.addOption("alpha", "ql.alpha", true, "Alpha parameter of QLStateless and QLStatefull");
//        options.addOption("c2irate", "qlc2i.communication-rate", true, "Probability to get information from infrastructure in QLC2Infrastructure.");
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
                        DEFAULT_ALGORITHM = QLStatefull.class;
                        break;

                    case "QLSTATELESS":
                        DEFAULT_ALGORITHM = QLStateless.class;
                        break;
                    case "QLSTATEFULLC2I":
                        DEFAULT_ALGORITHM = QLStatefullC2I.class;
                        DEFAULT_EDGE = EdgeC2I.class;
                        break;
                }
            }

            if (cmdLine.hasOption("info.type")) {
                switch (cmdLine.getOptionValue("info.type").toUpperCase()) {
                    case "BEST":
                        QLStatefullC2I.INFORMATION_TYPE = InformationType.Best;
                        break;
                    case "LAST":
                        QLStatefullC2I.INFORMATION_TYPE = InformationType.Last;
                        break;
                    case "NONE":
                        QLStatefullC2I.INFORMATION_TYPE = InformationType.None;
                        break;
                    case "AVERAGE":
                        QLStatefullC2I.INFORMATION_TYPE = InformationType.Average;
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
            if (cmdLine.hasOption("exploration.strategy")) {
                switch (cmdLine.getParsedOptionValue("exploration.strategy").toString().toUpperCase()) {
                    case "EDECREASING":
                        Params.EXPLORATION_POLICY = EpsilonDecreasing.class;
                        break;
                    case "SOFTMAX":
                        Params.EXPLORATION_POLICY = SoftMaxExploration.class;
                        break;
                }
            }

            if (cmdLine.hasOption("output.comment-char")) {
                COLUMN_SEPARATOR = cmdLine.getOptionValue("output.comment-char");
            }

            PRINT_ON_FILE = cmdLine.hasOption("output.on-file");

            PRINT_FLOWS = cmdLine.hasOption("output.link-flow");

            if (cmdLine.hasOption("tap")) {

                DEFAULT_TAP = ImplementedTAP.valueOf(cmdLine.getOptionValue("tap"));

                createTap();
            }

            if (cmdLine.hasOption("runs")) {
                REPETITIONS = Integer.parseInt(cmdLine.getParsedOptionValue("runs").toString());
            }

            if (cmdLine.hasOption("ql.episodes")) {
                MAX_EPISODES = Integer.parseInt(cmdLine.getParsedOptionValue("ql.episodes").toString());
            }

            if (cmdLine.hasOption("ql.epsilon-decay")) {
                EpsilonDecreasing.EPSILON_DECAY = Float.parseFloat(cmdLine.getParsedOptionValue("ql.epsilon-decay").toString());
            }

            if (cmdLine.hasOption("ql.epsilon")) {
                EpsilonDecreasing.EPSILON_INITIAL = Float.parseFloat(cmdLine.getParsedOptionValue("ql.epsilon").toString());
            }

            if (cmdLine.hasOption("ql.alpha")) {
                QLStatefull.ALPHA = Double.parseDouble(cmdLine.getParsedOptionValue("ql.alpha").toString());
                QLStatefullC2I.ALPHA = Double.parseDouble(cmdLine.getParsedOptionValue("ql.alpha").toString());
                QLStateless.ALPHA = Float.parseFloat(cmdLine.getParsedOptionValue("ql.alpha").toString());
            }

            if (cmdLine.hasOption("ql.gamma")) {
                QLStatefull.GAMMA = Double.parseDouble(cmdLine.getParsedOptionValue("ql.gamma").toString());
                QLStatefullC2I.GAMMA = Double.parseDouble(cmdLine.getParsedOptionValue("ql.gamma").toString());
            }

            if (cmdLine.hasOption("ql.k")) {
                QLStateless.K = Integer.parseInt(cmdLine.getParsedOptionValue("ql.k").toString());
            }

            if (cmdLine.hasOption("proportion")) {
                PROPORTION = Integer.parseInt(cmdLine.getParsedOptionValue("proportion").toString());
            }

            if (cmdLine.hasOption("qlc2i.communication-rate")) {
                QLStatefullC2I.COMMUNICATION_RATE = Double.parseDouble(cmdLine.getParsedOptionValue("qlc2i.communication-rate").toString());
            }

            PRINT_OD_PAIRS_AVG_COST = cmdLine.hasOption("output.od-pairs");

            if (cmdLine.hasOption("output.dir")) {
                OUTPUT_DIRECTORY = cmdLine.getOptionValue("output.dir");
            }

            if (cmdLine.hasOption("output.column-char")) {
                COLUMN_SEPARATOR = cmdLine.getOptionValue("output.column-char");
            }

            PRINT_ON_TERMINAL = cmdLine.hasOption("output.on-terminal");
        }

    }

    public static void createTap() {

        switch (DEFAULT_TAP) {
            case BYPASS:
                USED_TAP = TAP.BYPASS(DEFAULT_ALGORITHM);
                break;
            case BRAESS:
                USED_TAP = TAP.BRAESS(DEFAULT_ALGORITHM);
                break;
            case BRAESS6:
                USED_TAP = TAP.BRAESS_6(DEFAULT_ALGORITHM);
                break;
            case BRAESSBAZZAN:
                USED_TAP = TAP.BRAESS_BAZZAN(DEFAULT_ALGORITHM);
                break;
            case EMME:
                USED_TAP = TAP.EMME(DEFAULT_ALGORITHM);
                break;
            case ND:
                USED_TAP = TAP.ND(DEFAULT_ALGORITHM);
                break;
            case OW_MULTIOBJECTIVE:
                USED_TAP = TAP.OW_MULTIOBJECTIVE(DEFAULT_ALGORITHM);
                break;
            case OW:
                USED_TAP = TAP.OW(DEFAULT_ALGORITHM);
                break;
            case SF:
                USED_TAP = TAP.SF(DEFAULT_ALGORITHM);
                break;
            case TWO_NEIGHBORHOOD:
                USED_TAP = TAP.TWO_NEIGHBORHOOD(DEFAULT_ALGORITHM);
                break;
            case TWO_NEIGHBORHOOD_MIRRORED:
                USED_TAP = TAP.TWO_NEIGHBORHOOD_MIRRORED(DEFAULT_ALGORITHM);
                break;
            case TWO_NEIGHBORHOOD_REPLICATED:
                USED_TAP = TAP.TWO_NEIGHBORHOOD_REPLICATED(DEFAULT_ALGORITHM);
                break;
            default:
                throw new AssertionError(DEFAULT_TAP.name() + " not found.");
        }
    }

}
