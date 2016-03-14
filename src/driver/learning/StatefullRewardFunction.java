/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import driver.Driver;
import org.jgrapht.Graph;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class StatefullRewardFunction extends AbstractRewardFunction<Driver> {

    public StatefullRewardFunction(Graph graph) {
        this.graph = graph;
    }
    
    Graph graph;
    

    @Override
    public Double getReward(Driver driver) {
        switch (Params.REWARD_FUNCTION) {
            case DIFFERENCE_REWARDS:
                return getDifferenceRewards(driver);
            case STANDARD_REWARD:
                return getStandardReward(driver);
        }
        return getStandardReward(driver);
    }

    @Override
    public Double getStandardReward(Driver driver) {
        return -driver.getCurrentEdge().getCost();
    }

    @Override
    public Double getRewardShaping(Driver driver) {
        return 0.0;
    }

    @Override
    public Double getDifferenceRewards(Driver driver) {
        //It will not work for QLStatefullC2I
//        QLStatefull ql = (QLStatefull) driver;
        
//        double soma_gz = 0;
//        double gz_zi = 0;
//        for (Object edge : graph.edgeSet()) {
//            AbstractEdge e = (AbstractEdge) edge;
//
//            if (e.getTotalFlow() > 0) {
//                soma_gz -= e.getCost() * e.getTotalFlow();
//            } else {
//                soma_gz -= e.getCost();
//            }
//            
//            if (ql.getRoute().contains(e)) {
//                if (e.getTotalFlow() > 0) {
//                    gz_zi -= e.getCostFunction().evalDesirableCost(e, e.getTotalFlow() - 1) * (e.getTotalFlow() - 1);
//                } else {
//                    gz_zi -= e.getCostFunction().evalDesirableCost(e, e.getTotalFlow() - 1);
//                }
//            } else {
//                if (e.getTotalFlow() > 0) {
//                    gz_zi -= e.getCost() * e.getTotalFlow();
//                } else {
//                    gz_zi -= e.getCost();
//                }
//            }
//        }
//        return (soma_gz - gz_zi) / Params.USED_TAP.getDrivers().size();
//
        Double gz = driver.getCurrentEdge().getCost();
        Double gz_zi = (double) driver.getCurrentEdge().getCostFunction().evalDesirableCost(driver.getCurrentEdge(), driver.getCurrentEdge().getTotalFlow() - 1);
//        System.out.println("gz: " + gz +"\tgz_zi: " + gz_zi +"du(gz-gz_zi): " + (gz-gz_zi));
        return -(gz - gz_zi);
    }
}
