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
package scenario.network;

import driver.Driver;
import java.util.HashMap;
import java.util.Map;
import org.jgrapht.graph.DefaultWeightedEdge;
import simulation.Params;

/**
 * Defines the Edge structure used in the simulator.
 *
 * @author Ricardo Grunitzki
 */
public abstract class AbstractEdge extends DefaultWeightedEdge implements Comparable<AbstractEdge> {

    protected Map<String, Object> params;

    protected int currentFlow;

    protected int totalFlow;

    /**
     * The cost function of the edge.
     */
    protected AbstractCostFunction costFunction;

    /**
     * Creates an edge with an specific cost function
     *
     * @param costFunction cost function used in the edge
     */
    public AbstractEdge(AbstractCostFunction costFunction) {
        this.params = new HashMap<>();
        this.currentFlow = 0;
        this.totalFlow = 0;
        this.costFunction = costFunction;
    }

    /**
     * This method is performed before the simulation starts. The
     * {@link AbstractEdge} provide no behavior. However, it is important for
     * the implementation learning behavior on extensions.
     */
    public void beforeSimulation() {

    }

    /**
     * This method is performed after the simulation starts. The
     * {@link AbstractEdge} provide no behavior. However, it is important for
     * the implementation learning behavior on extensions.
     */
    public void afterSimulation() {

    }

    /**
     * This method is performed after each episode. The {@link AbstractEdge}
     * provide no behavior. However, it is important for the implementation
     * learning behavior on extensions.
     */
    public void afterEpisode() {

    }

    /**
     * This method is performed before each episode. The {@link AbstractEdge}
     * provide no behavior. However, it is important for the implementation
     * learning behavior on extensions.
     */
    public void beforeEpisode() {

    }

    /**
     * Sets the parameters of the edge.
     *
     * @param params Map with the parameters.
     */
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    /**
     * Returns the collection of parameters of the edge.
     *
     * @return Map of parameters
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * Returns the current flow of the edge.
     *
     * @return flow in unit of drivers
     */
    public int getCurrentFlow() {
        return currentFlow;
    }

    /**
     * Sets the total flow of drivers using the edge.
     *
     * @param totalFlow unit of flow
     */
    public void setTotalFlow(int totalFlow) {
        this.totalFlow = totalFlow;
    }

    /**
     * Returns the total flow of drivers using the edge in the current time
     * step.
     *
     * @return flow in unit of drivers
     */
    public int getTotalFlow() {
        return totalFlow;
    }

    /**
     * Value used to compute the shortest paths. It is represented by the travel
     * cost according to the total flow.
     *
     * @return edge weight
     */
    @Override
    protected double getWeight() {
        return costFunction.evalCost(this);
    }

    /**
     * Clears the current flow.
     */
    public synchronized void clearCurrentFlow() {
        this.currentFlow = 0;
    }

    /**
     * Resets the edge cleaning its current and total flow.
     */
    public synchronized void reset() {
        this.currentFlow = 0;
        this.totalFlow = 0;
    }

    /**
     * Resets all attributes of the edge.
     */
    public synchronized void resetAll() {
        this.reset();
        totalFlow = 0;
    }

    /**
     * Perform the processing of the edge when a driver uses it.
     *
     * @param driver Driver that is using the edge at the current moment
     */
    public synchronized void proccess(Driver driver) {
        //increment the flow of drivers on edge
        this.currentFlow += Params.PROPORTION;
        this.totalFlow += Params.PROPORTION;
    }

    /**
     * Returns the name of the edge
     *
     * @return identifier of the edge.
     */
    public String getName() {
        return this.getSource().toString() + "|" + this.getTarget().toString();
    }

    /**
     * Returns the source vertex of the edge.
     *
     * @return source vertex
     */
    public String getSourceVertex() {
        return this.getSource().toString();
    }

    /**
     * Returns the target vertex of the edge.
     *
     * @return target vertex
     */
    public String getTargetVertex() {
        return this.getTarget().toString();
    }

    /**
     * Returns the cost of the edge.
     *
     * @return travel cost of the edge
     */
    public synchronized double getCost() {
        return this.getWeight();
    }

    /**
     * Returns the cost function used in the edge.
     *
     * @return cost function of the edge
     */
    public AbstractCostFunction getCostFunction() {
        return costFunction;
    }

    @Override
    public int compareTo(AbstractEdge o) {
        return this.getName().compareTo(o.getName());
    }

    /**
     * Increments the total flow by an specific unit of vehicles.
     *
     * @param flow flow of drivers on link.
     */
    public synchronized void incrementTotalFlow(int flow) {
        this.totalFlow += flow;
    }

}
