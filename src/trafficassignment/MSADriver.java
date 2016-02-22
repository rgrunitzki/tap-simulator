/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficassignment;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;

/**
 *
 * @author rgrunitzki
 */
public class MSADriver extends driver.Driver<MSADriver, List<EdgeMSA>>{

    public MSADriver(int id, String origin, String destination, Graph graph) {
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
    public List<EdgeMSA> getRoute() {
        return this.route;
    }

    public synchronized void setRoute(List<EdgeMSA> route) {
        this.route = route;
    }

    @Override
    public double getTravelTime() {
        double cost = 0;
        for (EdgeMSA e : this.getRoute()) {
            cost += e.getCostFunction().evalDesirableCost(e, e.getMsaFlow());
        }
        return cost;
    }
    
}
