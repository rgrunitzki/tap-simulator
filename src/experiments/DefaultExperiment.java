package experiments;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import simulation.Params;
import simulation.Simulation;

/**
 * Object used to represent an experiment structure. It enables to run
 * repetitions of the same simulation.
 *
 * @author Ricardo Grunitzki
 */
public class DefaultExperiment {

    private final int runs;
    private final Simulation simulation;
    private List<Integer> episodes;
    private List<Double> travelCost;
    private List<Double> learningEffort;
    private List<Double> stoppingValue;

    /**
     * Creates a DefaultExperiment object.
     */
    public DefaultExperiment() {
        this.runs = Params.REPETITIONS;
        Params.createTap();
        this.simulation = new Simulation(Params.USED_TAP);
    }

    /**
     * Run the 'n' repetitions of the experiment.
     */
    public void run() {
        //variables initialization
        int run = 1;
        episodes = new ArrayList<>(runs);
        travelCost = new ArrayList(runs);
        learningEffort = new ArrayList(runs);
        stoppingValue = new ArrayList(runs);
        try {
            while (run++ <= runs) {
                //runs simulation
                simulation.execute();
                //get outputs
                episodes.add(Params.CURRENT_EPISODE);
                travelCost.add(simulation.averageTravelCost());
                learningEffort.add(simulation.getLearningEffort());
                stoppingValue.add(simulation.getStopCriterion().stoppingValue(simulation));
                //print results
                if (Params.PRINT_AVERAGE_RESULTS) {
                    System.out.println("#" + (run - 1) + Params.COLUMN_SEPARATOR
                            + episodes.get(run - 2) + Params.COLUMN_SEPARATOR
                            + travelCost.get(run - 2) + Params.COLUMN_SEPARATOR
                            + learningEffort.get(run - 2) + Params.COLUMN_SEPARATOR
                            + stoppingValue.get(run - 2));
                }
                simulation.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            simulation.end();
            if ((runs > 1) && Params.PRINT_AVERAGE_RESULTS) {
                DescriptiveStatistics dsEpisodes = new DescriptiveStatistics();
                DescriptiveStatistics dsTravelCost = new DescriptiveStatistics();
                DescriptiveStatistics dsLearningEffort = new DescriptiveStatistics();
                DescriptiveStatistics dsStoppingValue = new DescriptiveStatistics();
                for (int i = 0; i < travelCost.size(); i++) {
                    dsEpisodes.addValue(episodes.get(i));
                    dsTravelCost.addValue(travelCost.get(i));
                    dsLearningEffort.addValue(learningEffort.get(i));
                    dsStoppingValue.addValue(stoppingValue.get(i));
                }
                System.out.println("avgs." + Params.COLUMN_SEPARATOR
                        + dsEpisodes.getMean() + Params.COLUMN_SEPARATOR
                        + dsTravelCost.getMean() + Params.COLUMN_SEPARATOR
                        + dsLearningEffort.getMean() + Params.COLUMN_SEPARATOR
                        + dsStoppingValue.getMean() + Params.COLUMN_SEPARATOR
                );

                System.out.println("stdevs." + Params.COLUMN_SEPARATOR
                        + dsEpisodes.getStandardDeviation() + Params.COLUMN_SEPARATOR
                        + dsTravelCost.getStandardDeviation() + Params.COLUMN_SEPARATOR
                        + dsLearningEffort.getStandardDeviation() + Params.COLUMN_SEPARATOR
                        + dsStoppingValue.getStandardDeviation() + Params.COLUMN_SEPARATOR
                );

//                for (double d : travelCost) {
//                    dsTravelCost.addValue(d);
//                }
//                System.out.println("mean: " + dsTravelCost.getMean() + Params.RAMDON_SEED + "\tstdev: " + dsTravelCost.getStandardDeviation());
//                System.out.println("mean: " + dsTravelCost.getMean() + Params.RAMDON_SEED + "\tstdev: " + dsTravelCost.getStandardDeviation());
            }
        }
    }

    /**
     * Returns the average travel cost of the simulation
     *
     * @return average travel cost
     */
    public Double averageTravelCost() {
        double sum = 0;
        for (double cost : travelCost) {
            sum += cost;
        }
        return sum / travelCost.size();
    }

}
