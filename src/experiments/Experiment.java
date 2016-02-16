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

    public Experiment() {
        this.runs = Params.REPETITIONS;
        this.simulation = new Simulation(Params.USED_TAP);
    }

    public void run() {
        int run = 1;
        List<Double> costs = new ArrayList(runs);
        try {
            while (run++ <= runs) {
                simulation.execute();
                costs.add(simulation.simulationTravelTime());
                System.out.println("#" + (run - 1) + "\t" + costs.get(run - 2));
                simulation.reset();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            simulation.end();
            if ((runs > 1) && Params.AVERAGE_RESULTS) {
                DescriptiveStatistics st = new DescriptiveStatistics();
                if (Params.AVERAGE_RESULTS) {
                    for (double d : costs) {
                        st.addValue(d);
                    }
                    System.out.println("mean\t" + st.getMean() + "\t" + st.getStandardDeviation());
                }
            }
        }
    }
}
