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
 *
 * @author rgrunitzki
 */
public class TAP {

    private final File demandFile;
    private final File networkFile;
    private AbstractCostFunction costFunction;
    private List<Driver> drivers;
    private Graph graph;
    private Map<String, ODPair> odpairs = new ConcurrentHashMap<>();
    private Class clazz;

    public TAP(File demand, File network, AbstractCostFunction costFunction, Class clazz) {
        this.demandFile = demand;
        this.networkFile = network;
        this.costFunction = costFunction;
        try {
            this.graph = Loader.loadNetwork(networkFile, Params.DEFAULT_EDGE, costFunction);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(TAP.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.clazz = clazz;
        try {
            this.drivers = Loader.processODMatrix(graph, demandFile, clazz);
            this.odpairs = Loader.odpairs;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(TAP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public AbstractCostFunction getCostFunction() {
        return costFunction;
    }

    public void setCostFunction(AbstractCostFunction costFunction) {
        this.costFunction = costFunction;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public Map<String, ODPair> getOdpairs() {
        return odpairs;
    }

    public void setOdpairs(Map<String, ODPair> odpairs) {
        this.odpairs = odpairs;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public static TAP OW(Class clazz) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/ow.net.xml";
        demandFile = "files/ow.od.xml";
        costFunction = new LinearCostFunction();

        return new TAP(new File(demandFile), new File(netFile), costFunction, clazz);
    }

    public static TAP SF(Class clazz) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;

        netFile = "files/siouxfalls.net.xml";
        demandFile = "files/siouxfalls.od.xml";
        costFunction = new BPRFunction();

        return new TAP(new File(demandFile), new File(netFile), costFunction, clazz);
    }

    public static TAP EMME(Class clazz) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;

        netFile = "files/emme.net.xml";
        demandFile = "files/emme.od.xml";
        costFunction = new BPRFunction();

        return new TAP(new File(demandFile), new File(netFile), costFunction, clazz);
    }

    public static TAP ND(Class clazz) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;

        netFile = "files/nd.net.xml";
        demandFile = "files/nd.od.xml";
        costFunction = new LinearCostFunction();

        return new TAP(new File(demandFile), new File(netFile), costFunction, clazz);
    }

    public static TAP BRAESS(Class clazz) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;

        netFile = "files/braess.net.xml";
        demandFile = "files/braess.od.xml";
        costFunction = new BraessParadoxCostFunction();

        return new TAP(new File(demandFile), new File(netFile), costFunction, clazz);
    }

    public String getNetworkName() {
        return networkFile.getName().split(".net")[0];
//        return name.split(".net")[0];
    }

    public static TAP ANA(Class clazz) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/ana.net.xml";
        demandFile = "files/ana.od.xml";
        costFunction = new LinearCostFunction();

        return new TAP(new File(demandFile), new File(netFile), costFunction, clazz);
    }

    public static TAP BYPASS(Class clazz) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;
        netFile = "files/bypass.net.xml";
        demandFile = "files/bypass.od.xml";
        costFunction = new LinearCostFunction();

        return new TAP(new File(demandFile), new File(netFile), costFunction, clazz);
    }

}
