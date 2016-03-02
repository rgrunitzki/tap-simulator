/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import driver.Driver;
import org.jgrapht.Graph;
import scenario.AbstractEdge;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class StatefullRewardFunctionATT extends AbstractRewardFunction<Driver> {

    public static Double[] alpha = {-1.0, -1.0, -1.0, -1.0};

    public StatefullRewardFunctionATT(Graph graph) {
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
        AbstractEdge edge = driver.getCurrentEdge();
        QLStatefull d = (QLStatefull) driver;
        
        //destination?
        if (d.getDestination().equals(edge.getTargetVertex())) {
//            System.out.println(edge.toString() + " destination");
            return alpha[0] * driver.getCurrentEdge().getCost();
            //u turn?
        } else if (d.getRoute().size()>1 && d.getCurrentEdge().getTargetVertex().equals(d.getRoute().get(d.getRoute().size()-2).getSourceVertex())) {
//            System.out.println(edge.toString() + " uturn");
            return alpha[1] * driver.getCurrentEdge().getCost();
            //travelled
       // } else if (d.getRoute().size()>1 && d.getRoute().subList(0, d.getRoute().size()-2).contains(edge)) {
//            System.out.println(edge.toString() + " travelled");
        //    return alpha[2] * driver.getCurrentEdge().getCost();
            //otherwise
        } else {
//            System.out.println(edge.toString() + " otherwise");
            return alpha[3] * driver.getCurrentEdge().getCost();
        }
    }

    @Override
    public Double getRewardShaping(Driver driver) {
        return 0.0;
    }

    @Override
    public Double getDifferenceRewards(Driver driver) {
        return 0.0;
    }
}
