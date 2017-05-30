/* 
 * Copyright (C) 2017 Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import experiments.DefaultExperiment;
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
