/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package experiments;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import scenario.TAP;
import simulation.Params;
import simulation.Simulation;

/**
 *
 * @author rgrunitzki
 */
public class Experiment {

    private final int runs;
    private final Simulation simulation;

    public Experiment(int runs, TAP tap) {
        this.runs = runs;
        this.simulation = new Simulation(tap);
    }

    public void run() {

        if (Params.PRINT_PARAMS) {
            System.out.println(Params.getParameterValues() + "\n" + "------");
        }
        int run = 1;
        List<Double> costs = new ArrayList(runs);
        try {
            while (run++ <= runs) {
                simulation.execute();
                costs.add(simulation.getAverageCost());
                simulation.reset();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            simulation.end();
            DescriptiveStatistics st = new DescriptiveStatistics();
            if (Params.AVERAGE_RESULTS) {
                for (double d : costs) {
                    st.addValue(d);
                }
                System.out.println("mean: " + st.getMean() + "\tvariance: " + st.getVariance() + "\tstdev: " + st.getStandardDeviation());
            } else {
                for (int i = 0; i < costs.size(); i++) {

                    System.out.println((i + 1) + "\t" + costs.get(i));
                }
            }
        }
    }
}
