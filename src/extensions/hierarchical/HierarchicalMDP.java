/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions.hierarchical;

import driver.learning.mdp.AbstractMDP;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import scenario.network.AbstractEdge;

/**
 *
 * @author rgrunitzki
 */
public class HierarchicalMDP extends AbstractMDP<String, AbstractEdge, Double> {

    //Low level MDPs
    protected Map<String, Map<String, Map<AbstractEdge, Double>>> lowLevelMDPs = new ConcurrentHashMap<>();
    //High level MDP
    protected Map<String, Map<List<AbstractEdge>, Double>> highLevelMDP = new ConcurrentHashMap<>();

    /**
     * Object used support the fast clone of StatefullMDP objects.
     */
    public static HierarchicalMDP staticMdp;

    @Override
    public void setValue(AbstractEdge action, Double value) {
        this.mdp.get(action.getSourceVertex()).put(action, value);
    }

    @Override
    public double getValue(AbstractEdge key) {
        return this.mdp.get(key.getSourceVertex()).get(key);
    }

    @Override
    public void createMDP(List<AbstractEdge> actions) {
    }

    @Override
    public void reset() {
    }

    public void setLowLevelMDPs(Map lowLevelMDPs) {
        this.lowLevelMDPs = lowLevelMDPs;
    }

    public void setHighLevelMDP(Map<String, Map<List<AbstractEdge>, Double>> highLevelMDP) {
        this.highLevelMDP = highLevelMDP;
    }

    public void setMDP(Map mdp) {
        this.mdp = mdp;
    }

}
