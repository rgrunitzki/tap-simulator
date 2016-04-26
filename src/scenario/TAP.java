/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scenario;

import driver.Driver;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgrapht.Graph;
import simulation.Params;

/**
 * This class models the structure of the Traffic Assignment Problem (TAP).
 *
 * @author Ricardo Grunitzki
 */
public class TAP {

    private final File demandFile;
    private final File networkFile;
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
     * @param demand {@link File} object with the demand.od.xml file
     * @param network {@link File} object with the network.net.xml file
     * @param costFunction {@link CostFunction} with the cost function of the
     * problem
     * @param clazz Type of the drivers.
     */
    public TAP(File demand, File network, AbstractCostFunction costFunction, Class clazz) {
        this.demandFile = demand;
        this.networkFile = network;
        this.costFunction = costFunction;
        try {
            this.graph = Loader.loadNetwork(networkFile, Params.DEFAULT_EDGE, costFunction);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(TAP.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.driverClass = clazz;
        try {
            this.drivers = Loader.processODMatrix(graph, demandFile, clazz);
            this.odPairs = Loader.odpairs;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(TAP.class.getName()).log(Level.SEVERE, null, ex);
        }

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
    public Graph getGraph() {
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
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/siouxfalls.net.xml";
        demandFile = "files/siouxfalls.od.xml";
        costFunction = new BPRFunction();
        return new TAP(new File(demandFile), new File(netFile), costFunction, driverClass);
    }

}
