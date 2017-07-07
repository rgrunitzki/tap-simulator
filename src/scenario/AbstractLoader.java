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
package scenario;

import driver.Driver;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;
import scenario.demand.ODPair;
import scenario.network.AbstractCostFunction;

/**
 *
 * @author Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
 */
public abstract class AbstractLoader {

    /**
     * Creates the demand.
     *
     * @param graph
     * @param demandFile
     * @param driverClass
     * @return
     */
    protected abstract Pair<List<Driver>, Map<String, ODPair>> createDemand(File demandFile, Class driverClass, Graph graph);

    /**
     * Creates the supply.
     *
     * @param networkFile
     * @param edgeClass
     * @param costFunction
     * @return
     */
    protected abstract Pair<Graph, AbstractCostFunction> createSupply(File networkFile, Class edgeClass, AbstractCostFunction costFunction);

    /**
     *
     * @param demandFile
     * @param netFile
     * @param driverClass
     * @param edClass
     * @param costFunction
     * @return
     */
    public TAP createTAP(File demandFile, File netFile, Class driverClass, Class edClass, AbstractCostFunction costFunction) {
        //generate network
        Pair<Graph, AbstractCostFunction> network = createSupply(netFile, edClass, costFunction);
        //generate demand
        Pair<List<Driver>, Map<String, ODPair>> demand = createDemand(demandFile, driverClass, network.getLeft());
        //generate the traffic assignment problem
        TAP tap = new TAP(network.getLeft(), network.getRight(), demand.getLeft(), demand.getRight());
        return tap;
    }

}
