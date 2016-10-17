/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions.hierarchical;

import driver.Driver;
import driver.learning.reward.AbstractRewardFunction;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.event.GraphEdgeChangeEvent;
import scenario.network.AbstractEdge;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class HierarchicalStetefullRewardFunction extends AbstractRewardFunction<Driver> {

    /**
     * Creates the reward function object
     *
     * @param graph
     */
    public HierarchicalStetefullRewardFunction(Graph graph) {
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
        boolean check = false;
        
        if(driver.getCurrentEdge().getTargetVertex().equalsIgnoreCase(driver.getDestination())){
            check = true;
        }else{
        Set<AbstractEdge> edges = graph.edgesOf(driver.getCurrentVertex());
        for (AbstractEdge edge : edges) {
            if (edge.getSourceVertex().equalsIgnoreCase(driver.getCurrentEdge().getTargetVertex())) {
                check = true;
            }
        }}
        
        if (check) {
            return -driver.getCurrentEdge().getCost();
        } else {
            return -100.; //penality for going to a situation in which there is no end
        }
    }

    @Override
    public Double getRewardShaping(Driver driver) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Double getDifferenceRewards(Driver driver) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
