/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import java.util.List;
import scenario.AbstractEdge;

/**
 *
 * @author rgrunitzki
 */
public class StatefullMDP extends AbstractMDP<String, AbstractEdge, Double> {

    public static StatefullMDP staticMdp;

    @Override
    public void setValue(AbstractEdge action, Double value) {
        this.mdp.get(action.getSourceVertex()).put(action, value);
    }

    @Override
    public double getValue(AbstractEdge actionKey) {
        return this.mdp.get(actionKey.getSourceVertex()).get(actionKey);
    }

    @Override
    public void createMDP(List<AbstractEdge> actions) {
    }

    @Override
    public void reset() {
    }

}
