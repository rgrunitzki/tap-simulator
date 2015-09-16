/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver;

import java.util.concurrent.Callable;
import scenario.Edge;
import org.jgrapht.Graph;

/**
 *
 * @author rgrunitzki
 * @param <T>
 * @param <Route>
 */
@SuppressWarnings("rawtypes")
public abstract class Driver<T extends Driver, T2> implements Callable<Driver> {

    protected final int id;

    protected Graph graph;

    protected final String origin;

    protected final String destination;

    protected Edge currentEdge;

    protected String currentVertex;

    protected T2 route;

    protected boolean stepA = true;

    protected double travelTime;

    public Driver(int id, String origin, String destination, Graph graph) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.graph = graph;
        this.travelTime = 0;
    }

    @Override
    public Driver call() throws Exception {
        if (stepA) {
            this.beforeStep();
            stepA = false;
        } else {
            this.afterStep();
            stepA = true;
        }
        return this;
    }

    public abstract void beforeSimulation();

    public abstract void afterSimulation();

    public abstract void beforeEpisode();

    public abstract void afterEpisode();

    public abstract void beforeStep();

    public abstract void afterStep();

    public abstract void resetAll();

    public boolean hasArrived() {
        return this.getCurrentEdge() == null;
    }

    public Edge getCurrentEdge() {
        return currentEdge;
    }

    /**
     *
     * @param currentEdge
     */
    public void setCurrentEdge(Edge currentEdge) {
        this.currentEdge = currentEdge;
    }

    /**
     *
     * @return
     */
    public String getCurrentVertex() {
        return currentVertex;
    }

    /**
     *
     * @param currentVertex
     */
    public void setCurrentVertex(String currentVertex) {
        this.currentVertex = currentVertex;
    }

    /**
     *
     * @return
     */
    public abstract T2 getRoute();

    
    public String getDestination() {
        return destination;
    }
}
