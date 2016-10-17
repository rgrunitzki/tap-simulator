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
    private List<Double> costs;

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
        int run = 1;
        costs = new ArrayList(runs);
        try {
            while (run++ <= runs) {
                simulation.execute();
                costs.add(simulation.averageTravelCost());
                if (Params.PRINT_AVERAGE_RESULTS) {
                    System.out.println("#" + (run - 1) + Params.COLUMN_SEPARATOR + costs.get(run - 2));
                }
                simulation.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            simulation.end();
            if ((runs > 1) && Params.PRINT_AVERAGE_RESULTS) {
                DescriptiveStatistics st = new DescriptiveStatistics();
                for (double d : costs) {
                    st.addValue(d);
                }
                System.out.println("mean: " + st.getMean() + Params.RAMDON_SEED + "stdev: " + st.getStandardDeviation());
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
        for (double cost : costs) {
            sum += cost;
        }
        return sum / costs.size();
    }
}
