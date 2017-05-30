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
