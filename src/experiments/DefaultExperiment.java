/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package experiments;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import simulation.Params;
import simulation.Simulation;

/**
 *
 * @author rgrunitzki
 */
public class DefaultExperiment {

    private final int runs;
    private final Simulation simulation;

    public DefaultExperiment() {
        this.runs = Params.REPETITIONS;
        Params.createTap();
        this.simulation = new Simulation(Params.USED_TAP);
    }

    public void run() {
        int run = 1;
        List<Double> costs = new ArrayList(runs);
        try {
            while (run++ <= runs) {
                simulation.execute();
                costs.add(simulation.simulationTravelTime());
                if (Params.PRINT_AVERAGE_RESULTS) {
                    System.out.println("#" + (run - 1) + Params.SEPARATOR + costs.get(run - 2));
                }
                simulation.reset();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            simulation.end();
            if ((runs > 1) && Params.PRINT_AVERAGE_RESULTS) {
                DescriptiveStatistics st = new DescriptiveStatistics();
                for (double d : costs) {
                    st.addValue(d);
                }
                System.out.println("mean: " + st.getMean() + Params.SEED + "stdev: " + st.getStandardDeviation());
            }
        }
    }
    
    public String getTravelTime(){
        return ""+simulation.simulationTravelTime();
    }
}
