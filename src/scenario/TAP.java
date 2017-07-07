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

import scenario.demand.ODPair;
import scenario.network.BPRFunction;
import scenario.network.BraessParadoxCostFunction;
import scenario.network.AbstractCostFunction;
import scenario.network.LinearCostFunction;
import driver.Driver;
import extensions.coadaptation.MultiObjectiveLinearCostFunction;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgrapht.Graph;
import scenario.network.AbstractEdge;
import scenario.network.BraessBazzanCostFunction;
import simulation.Params;

/**
 * This class models the structure of the Traffic Assignment Problem (TAP).
 *
 * @author Ricardo Grunitzki
 */
public class TAP {

    private File demandFile;
    private File networkFile;
    private AbstractCostFunction costFunction;
    private List<Driver> drivers;
    private Graph graph;
    private Map<String, ODPair> odPairs = new ConcurrentHashMap<>();
    private Class driverClass;

    /**
     * <p>
     * Creates and returns the TAP object.
     *
     * This method receives:
     * <ul>
     * <li> The demand definitions;
     * <li> The network definitions;
     * <li> the cost function definitions;
     * <li> the Class of the drivers.
     * </ul>
     * </p>
     *
     * @param demand       {@link File} object with the demand.od.xml file
     * @param network      {@link File} object with the network.net.xml file
     * @param costFunction {@link CostFunction} with the cost function of the
     *                     problem
     * @param clazz        Type of the drivers.
     */
    public TAP(File demand, File network, AbstractCostFunction costFunction, Class clazz) {
        this.demandFile = demand;
        this.networkFile = network;
        this.costFunction = costFunction;
        XMLLoader loader = new XMLLoader();
        try {
            this.graph = loader.loadNetwork(networkFile, Params.DEFAULT_EDGE, costFunction);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(TAP.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.driverClass = clazz;
        try {
            this.drivers = loader.processODMatrix(graph, demandFile, clazz);
            this.odPairs = loader.odpairs;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(TAP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public TAP(Graph graph, AbstractCostFunction costFunction, List<Driver> drivers, Map<String, ODPair> odPairs) {
        this.graph = graph;
        this.costFunction = costFunction;
        this.drivers = drivers;
        this.odPairs = odPairs;
    }

    /**
     * Returns an integer representing the demand size.
     *
     * @return demand size
     */
    public int demandSize() {
        return drivers.size() * Params.PROPORTION;
    }

    /**
     * Returns the cost function of the TAP.
     *
     * @return cost function object
     */
    public AbstractCostFunction getCostFunction() {
        return costFunction;
    }

    /**
     * Returns the list of drivers in the TAP.
     *
     * @return List of drivers
     */
    public List<Driver> getDrivers() {
        return drivers;
    }

    /**
     * Returns the graph representing the road network of the TAP.
     *
     * @return Graph object
     */
    public Graph<String, AbstractEdge> getGraph() {
        return graph;
    }

    /**
     * Returns the Map of OD-pairs objects of the TAP.
     *
     * @return Map of OD-pairs
     */
    public Map<String, ODPair> getOdpairs() {
        return odPairs;
    }

    /**
     * Returns the Class type of the TAP drivers.
     *
     * @return Class type of the driver.
     */
    public Class getClazz() {
        return driverClass;
    }

    /**
     * Returns the TAP's network name.
     *
     * @return network name
     */
    public String getNetworkName() {
        return networkFile.getName().split(".net")[0];
    }

    /**
     * Returns the BRAESS Paradox TAP. This problem is defined in:
     * <ul>
     * <li>link: https://pt.wikipedia.org/wiki/Paradoxo_de_Braess;
     * <li>accessed in: 25/04/2016.
     * </ul>
     *
     * @param driverClass class of the drivers
     * @return the BRAESS TAP
     */
    public static TAP BRAESS(Class driverClass) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/braess.net.xml";
        demandFile = "files/braess.od.xml";
        costFunction = new LinearCostFunction();

        return new TAP(new File(demandFile), new File(netFile), costFunction, driverClass);
    }

    /**
     * Returns the Braess Paradox with 6 trips TAP. This problem is defined in:
     * <ul>
     * <li>K. Tumer and D. Wolpert, “Collective intelligence and Braess’
     * paradox,” in Proceedings of the National Conference on Artificial
     * Intelligence, 2000, pp. 104–109.
     * </ul>
     *
     * @param driverClass class of the drivers
     * @return the BRAESS_6 TAP
     */
    public static TAP BRAESS_6(Class driverClass) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/braess6.net.xml";
        demandFile = "files/braess6.od.xml";
        costFunction = new BraessParadoxCostFunction();

        return new TAP(new File(demandFile), new File(netFile), costFunction, driverClass);
    }

    public static TAP BRAESS_BAZZAN(Class driverClass) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/tests/braessBazzan.net.xml";
        demandFile = "files/tests/braessBazzan.od.xml";
        costFunction = new BraessBazzanCostFunction();

        return new TAP(new File(demandFile), new File(netFile), costFunction, driverClass);
    }

    /**
     * Returns the Bypass TAP. This problem is defined in:
     * <ul>
     * <li>J. Ortúzar and L. G. Willumsen. Modelling Transport. John Wiley &
     * Sons, 3rd edition, 2001.
     * <li>Example 10.4
     * <li>accessed in: 02/10/2016
     * </ul>
     *
     * @param driverClass class of the drivers
     * @return the BYPASS TAP
     */
    public static TAP BYPASS(Class driverClass) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/bypass.net.xml";
        demandFile = "files/bypass.od.xml";
        costFunction = new LinearCostFunction();
        return new TAP(new File(demandFile), new File(netFile), costFunction, driverClass);
    }

    /**
     * Returns the EMME TAP. This problem is defined in:
     * <ul>
     * <li>Heinz Spiess, EMME/2 Support Center, 1996;
     * <li>link: http://emme2.spiess.ch/e2news/news02/node3.html;
     * <li>accessed in: 02/10/2016.
     * </ul>
     *
     * @param driverClass class of the drivers
     * @return the EMME TAP
     */
    public static TAP EMME(Class driverClass) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/emme.net.xml";
        demandFile = "files/emme.od.xml";
        costFunction = new BPRFunction();
        return new TAP(new File(demandFile), new File(netFile), costFunction, driverClass);
    }

    /**
     * Returns the Nguyen and Dupuis (ND) TAP. This problem is defined in:
     * <ul>
     * <li>Nguyen, S., Dupuis, C., 1984. An efficient method for computing
     * traffic equilibria in networks with asymmetric transportation costs.
     * Transportation Science 18, 185–202. Oppenheim
     * </ul>
     *
     * @param driverClass class of the drivers
     * @return the ND TAP
     */
    public static TAP ND(Class driverClass) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/nd.net.xml";
        demandFile = "files/nd.od.xml";
        costFunction = new LinearCostFunction();
        return new TAP(new File(demandFile), new File(netFile), costFunction, driverClass);
    }

    /**
     * Returns the multi-objective version of Ortuzar and Willumsen (OW) TAP.
     * This problem is defined in:
     * <ul>
     * <li>J. Ortúzar and L. G. Willumsen. Modelling Transport. John Wiley &
     * Sons, 3rd edition, 2001.
     * <li>Example 10.1
     * <li>accessed in: 02/10/2016
     * </ul>
     *
     * @param driverClass class of the drivers
     * @return the OW TAP
     */
    public static TAP OW_MULTIOBJECTIVE(Class driverClass) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/ow.net.xml";
        demandFile = "files/ow.od.xml";
        costFunction = new MultiObjectiveLinearCostFunction();
        return new TAP(new File(demandFile), new File(netFile), costFunction, driverClass);
    }

    /**
     * Returns the Ortuzar and Willumsen (OW) TAP. This problem is defined in:
     * <ul>
     * <li>J. Ortúzar and L. G. Willumsen. Modelling Transport. John Wiley &
     * Sons, 3rd edition, 2001.
     * <li>Example 10.1
     * <li>accessed in: 02/10/2016
     * </ul>
     *
     * @param driverClass class of the drivers
     * @return the OW TAP
     */
    public static TAP OW(Class driverClass) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/ow.net.xml";
        demandFile = "files/ow.od.xml";
        costFunction = new LinearCostFunction();
        return new TAP(new File(demandFile), new File(netFile), costFunction, driverClass);
    }

    /**
     * Returns the Sioux Falls (SF) TAP. This problem is defined in:
     * <ul>
     * <li>Hillel Bargera Transportation Problems Repository;
     * <li>link: http://www.bgu.ac.il/~bargera/tntp/;
     * <li>accessed in: 02/10/2016.
     * </ul>
     *
     * @param driverClass class of the drivers
     * @return the SF TAP
     */
    public static TAP SF(Class driverClass) {
//        String netFile;
//        String demandFile;
//        AbstractCostFunction costFunction = null;
//        netFile = "files/SF.net";
//        demandFile = "files/SF.net";
//
//        NewCSVLoader loader = new NewCSVLoader();
//        return loader.createTAP(new File(demandFile), new File(netFile), null, null, costFunction);

//xml way
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/siouxfalls.net.xml";
        demandFile = "files/siouxfalls.od.xml";
        costFunction = new BPRFunction();
        return new TAP(new File(demandFile), new File(netFile), costFunction, driverClass);
    }

    /**
     * Returns the Two Neighborhood TAP. This problem is defined by Ana Bazzan
     * and Bruno Silva for the work with spectral clustering.
     *
     * @param driverClass class of the drivers
     * @return the Neighborhood TAP
     */
    public static TAP TWO_NEIGHBORHOOD(Class driverClass) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/two_neighborhood.net.xml";
        demandFile = "files/two_neighborhood.od.xml";
        costFunction = new LinearCostFunction();
        return new TAP(new File(demandFile), new File(netFile), costFunction, driverClass);
    }

    public static TAP TWO_NEIGHBORHOOD_MIRRORED(Class driverClass) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/two_neighborhood_mirrored.net.xml";
        demandFile = "files/two_neighborhood_mirrored.od.xml";
        costFunction = new LinearCostFunction();
        return new TAP(new File(demandFile), new File(netFile), costFunction, driverClass);
    }

    public static TAP TWO_NEIGHBORHOOD_REPLICATED(Class driverClass) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/two_neighborhood_replicated.net.xml";
        demandFile = "files/two_neighborhood_replicated.od.xml";
        costFunction = new LinearCostFunction();
        return new TAP(new File(demandFile), new File(netFile), costFunction, driverClass);
    }

}
