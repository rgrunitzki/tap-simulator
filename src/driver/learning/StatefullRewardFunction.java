/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import org.jgrapht.Graph;
import scenario.Edge;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class StatefullRewardFunction extends AbstractRewardFunction<QLStatefull> {

    public StatefullRewardFunction(Graph graph) {
        this.graph = graph;
    }
    
    Graph graph;
    

    @Override
    public Double getReward(QLStatefull driver) {
        switch (Params.REWARDFUNCTION) {
            case DifferenceRewards:
                return getDifferenceRewards(driver);
            case StandardReward:
                return getStandardReward(driver);
        }
        return getStandardReward(driver);
    }

    @Override
    public Double getStandardReward(QLStatefull driver) {
        return -driver.getCurrentEdge().getCost();
    }

    @Override
    public Double getRewardShaping(QLStatefull driver) {
        return 0.0;
    }

    @Override
    public Double getDifferenceRewards(QLStatefull driver) {
        double soma_gz = 0;
        double gz_zi = 0;
        for (Object edge : graph.edgeSet()) {
            Edge e = (Edge) edge;

            if (e.getTotalFlow() > 0) {
                soma_gz -= e.getCost() * e.getTotalFlow();
            } else {
                soma_gz -= e.getCost();
            }
            
            if (driver.getRoute().contains(e)) {
                if (e.getTotalFlow() > 0) {
                    gz_zi -= e.getCostFunction().evalDesirableCost(e, e.getTotalFlow() - 1) * (e.getTotalFlow() - 1);
                } else {
                    gz_zi -= e.getCostFunction().evalDesirableCost(e, e.getTotalFlow() - 1);
                }
            } else {
                if (e.getTotalFlow() > 0) {
                    gz_zi -= e.getCost() * e.getTotalFlow();
                } else {
                    gz_zi -= e.getCost();
                }
            }
        }
        return (soma_gz - gz_zi) / Params.DEMAND_SIZE;
//
//        Double gz = driver.getCurrentEdge().getCost();
//        Double gz_zi = (double) driver.getCurrentEdge().getCostFunction().evalDesirableCost(driver.getCurrentEdge(), driver.getCurrentEdge().getTotalFlow() - 1);
//        System.out.println("gz: " + gz +"\tgz_zi: " + gz_zi +"du(gz-gz_zi): " + (gz-gz_zi));
//        return -(gz - gz_zi);
    }
}
