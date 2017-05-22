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
 * Hierarchical implementation of an Markov Decision Process (MDP).
 *
 * @author Ricardo Grunitzki
 */
public class HierarchicalMDP extends AbstractMDP<String, AbstractEdge, Double> {

    /**
     * Low level MDPs
     */
    protected Map<String, Map<String, Map<AbstractEdge, Double>>> lowLevelMDPs = new ConcurrentHashMap<>();
    /**
     * High level MDP
     */
    protected Map<String, Map<List<AbstractEdge>, Double>> highLevelMDP = new ConcurrentHashMap<>();

    /**
     * Object used support the fast clone of StatefullMDP objects.
     */
    public static HierarchicalMDP staticMdp;

    @Override
    public void setValue(AbstractEdge action, Double value) {
        this.updateDetalQ(value - this.mdp.get(action.getSourceVertex()).get(action));
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

    /**
     * Sets the current low level MDP.
     *
     * @param lowLevelMDPs
     */
    public void setLowLevelMDPs(Map lowLevelMDPs) {
        this.lowLevelMDPs = lowLevelMDPs;
    }

    /**
     * Sets the high level MDP.
     *
     * @param highLevelMDP
     */
    public void setHighLevelMDP(Map<String, Map<List<AbstractEdge>, Double>> highLevelMDP) {
        this.highLevelMDP = highLevelMDP;
    }

    /**
     * Sets the current MDP.
     *
     * @param mdp
     */
    public void setMDP(Map mdp) {
        this.mdp = mdp;
    }

}
