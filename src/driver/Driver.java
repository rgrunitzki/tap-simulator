/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;

import java.util.List;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;
import scenario.network.AbstractEdge;

/**
 * The Driver class represents the basic structure of drivers in transportation
 * systems.
 * <p>
 * All drivers of the system must extend this class. The DriverClass describe
 * the type of the driver, whilst Route describe the route type.
 * </p>
 *
 * <p>
 * This class uses the Callable interface to process a collection of drivers in
 * parallel.
 * </p>
 *
 * @author Ricardo Grunitzki
 * @param <DriverClass> The type of the driver
 * @param <Route> The type of the route
 */
@SuppressWarnings("rawtypes")
public abstract class Driver<DriverClass extends Driver, Route> implements Callable<Driver> {

    /**
     * The identifier of the Driver
     */
    protected final int id;

    /**
     * Road network graph
     */
    protected Graph graph;

    /**
     * The origin of the Driver
     */
    protected final String origin;

    /**
     * The destination of the driver
     */
    protected String destination;

    /**
     * The current edge of the driver
     */
    protected AbstractEdge currentEdge;

    /**
     * The current vertex of the driver
     */
    protected String currentVertex;

    /**
     * The traveled route of the driver
     */
    protected Route route;

    /**
     * Flag that identifies the which action the agent must perform in call
     * method
     */
    protected boolean stepA = true;

    /**
     * The travel time of the driver
     */
    protected double travelTime;

    /**
     * The learning effort along the simulation
     */
    protected double learningEffort;

   
    /**
     * <p>
     * Creates a Driver object according to the descriptions of his OD-pair.
     * </p>
     *
     * @param id the identifier
     * @param origin the origin node
     * @param destination the destination node
     * @param graph the graph representing the road network
     */
    public Driver(int id, String origin, String destination, Graph graph) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.graph = graph;
        this.travelTime = 0;
        this.learningEffort = 0;
    }

    /**
     * <p>
     * This method is used in parallel processing of a collection of drivers.
     * The flag {@link stepA} is used to define in which part of the simulation
     * the agent is. When {@link stepA} is true, the he runs the method
     * beforeStep(); otherwise, he performes afterStep() method.
     * </p>
     *
     * @return Driver object
     * @throws Exception
     */
    @Override
    public Driver call() throws Exception {
        if (stepA) {
//            this.incrementLearningEffort();
            this.beforeStep();
            stepA = false;
        } else {
            this.afterStep();
            stepA = true;
        }
        return this;
    }

    /**
     * This method is executed once before starting the simulation.
     */
    public abstract void beforeSimulation();

    /**
     * This method is executed once before ending the simulation.
     */
    public abstract void afterSimulation();

    /**
     * This method is executed once before each episode of the simulation.
     */
    public abstract void beforeEpisode();

    /**
     * This method is executed once after each episode of the simulation.
     */
    public abstract void afterEpisode();

    /**
     * This method is executed once before each step of one episode.
     */
    public abstract void beforeStep();

    /**
     * This method is executed once after each step of one episode.
     */
    public abstract void afterStep();

    /**
     * Resets all attribute of the driver to the initial definitions. This
     * method is used to redefine the driver for a new simulation. It is faster
     * to reset than create a new object.
     */
    public abstract void resetAll();

    protected void incrementLearningEffort() {
        this.learningEffort++;
    }

    /**
     * Returns a list of parameters of the driver.
     *
     * @return list of parameters
     */
    public abstract List<Pair> getParameters();

    /**
     * Returns true when the driver arrives in his destination.
     *
     * @return boolean flag
     */
    public boolean hasArrived() {
        return this.getCurrentEdge() == null;
    }

    public double getLearningEffort() {
        return learningEffort;
    }
    
    public double getDeltaQ(){
        return 0;
    }

    /**
     * Returns the current edge of the driver.
     *
     * @return current edge
     */
    public AbstractEdge getCurrentEdge() {
        return currentEdge;
    }

    /**
     * Sets the current edge of the driver.
     *
     * @param currentEdge current edge
     */
    public void setCurrentEdge(AbstractEdge currentEdge) {
        this.currentEdge = currentEdge;
    }

    /**
     * Returns the current vertex of the deriver.
     *
     * @return current vertex
     */
    public String getCurrentVertex() {
        return currentVertex;
    }

    /**
     * Sets the current vertex of the driver.
     *
     * @param currentVertex current vertex of the driver
     */
    public void setCurrentVertex(String currentVertex) {
        this.currentVertex = currentVertex;
    }

    /**
     * Returns the current route of the driver.
     *
     * @return current route
     */
    public abstract Route getRoute();

    /**
     * Returns the destination node of the driver
     *
     * @return Destination node
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Returns the travel time of the traveled route.
     *
     * @return current travel time
     */
    public double getTravelTime() {
        return travelTime;
    }

    /**
     * Returns the identifier of the driver.
     *
     * @return identifier
     */
    public int getId() {
        return id;
    }
}
