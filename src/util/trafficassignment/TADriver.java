/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.trafficassignment;

import driver.Driver;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;
import scenario.network.StandardEdge;

/**
 *
 * @author Ricardo Grunitzki
 */
public class TADriver extends Driver<TADriver, List<StandardEdge>> {

    public TADriver(int id, String origin, String destination, Graph graph) {
        super(id, origin, destination, graph);
    }

    @Override
    public void beforeSimulation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void afterSimulation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void beforeEpisode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void afterEpisode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void beforeStep() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void afterStep() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Pair> getParameters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<StandardEdge> getRoute() {
        return this.route;
    }

    public void setRoute(List<StandardEdge> route) {
        this.route = route;
    }

    @Override
    public double getTravelTime() {
        double cost = 0;
        for (StandardEdge e : this.getRoute()) {
            cost += e.getCost();
        }
        return cost;
    }

}
