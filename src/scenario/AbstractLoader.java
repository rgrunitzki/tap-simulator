/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author rgrunitzki
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
    protected abstract Pair<Graph, AbstractCostFunction> createSupply(File networkFile ,Class edgeClass, AbstractCostFunction costFunction);

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
