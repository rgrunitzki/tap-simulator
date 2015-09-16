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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgrapht.Graph;

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

    public TAP(File demand, File network, AbstractCostFunction costFunction, Class clazz) {
        this.demandFile = demand;
        this.networkFile = network;
        this.costFunction = costFunction;

        this.graph = Loader.loadNetwork(networkFile, costFunction);
        try {
            this.drivers = Loader.processODMatrix(graph, demandFile, clazz);
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

    public static TAP OW(Class clazz) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;

        netFile = "files/ortuzar.net.xml";
        demandFile = "files/ortuzar.od.xml";
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

    public static TAP ND(Class clazz) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;

        netFile = "files/ND.net.xml";
        demandFile = "files/ND.od.xml";
        costFunction = new LinearCostFunction();

        return new TAP(new File(demandFile), new File(netFile), costFunction, clazz);
    }

    public static TAP BP(Class clazz) {
        String netFile;
        String demandFile;
        AbstractCostFunction costFunction;

        netFile = "files/braess.net.xml";
        demandFile = "files/braess.od.xml";
        costFunction = new BraessParadoxCostFunction();

        return new TAP(new File(demandFile), new File(netFile), costFunction, clazz);
    }

}
