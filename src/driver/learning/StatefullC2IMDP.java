/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import extensions.c2i.QValueC2I;
import java.util.List;
import java.util.Map;
import scenario.AbstractEdge;

/**
 *
 * @author Ricardo Grunitzki
 */
public class StatefullC2IMDP extends AbstractMDP<String, AbstractEdge, QValueC2I> {

    public static StatefullC2IMDP staticMdp;

    @Override
    public void setValue(AbstractEdge action, QValueC2I value) {
        this.mdp.get(action.getSourceVertex()).put(action, value);
    }

    @Override
    public double getValue(AbstractEdge actionKey) {
        return this.mdp.get(actionKey.getSourceVertex()).get(actionKey).getValue();
    }

    @Override
    public void createMDP(List<AbstractEdge> actions) {
    }

    @Override
    public AbstractEdge getAction(Map<AbstractEdge, QValueC2I> mdp) {
        return super.getAction(mdp);
    }

    @Override
    public void reset() {
    }
}
