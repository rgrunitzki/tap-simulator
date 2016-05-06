package experiments;

import org.apache.commons.cli.ParseException;
import simulation.Params;

/**
 * This class enables the simulator be executed from command line.
 *
 * @author Ricardo Grunitzki
 */
public class CommandLineExperiment {

    /**
     * This method executes the simulator according to the specifications sent
     * via parameter.
     *
     * @param args list of parameters that describes the simulation
     * @throws ParseException
     */
    public static void main(String[] args) throws ParseException {

        Params.parseParams(args);

        DefaultExperiment experiment = new DefaultExperiment();
        experiment.run();

    }
}
