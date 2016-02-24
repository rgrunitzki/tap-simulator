/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import extensions.c2i.QValueC2I;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import scenario.AbstractEdge;
import simulation.Params;

/**
 *
 * @author rgrunitzki
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
    public AbstractEdge getAction(String key) {
        Map<AbstractEdge, QValueC2I> mdp2 = mdp.get(key);
        float random = Params.RANDOM.nextFloat();
        if (random <= getEpsilon()) {
            List l = new ArrayList<>(mdp2.keySet());
            Collections.shuffle(l, Params.RANDOM);
            return (AbstractEdge) l.get(0);
        } else {
            return Collections.max(mdp2.entrySet(), (entry1, entry2) -> entry1.getValue().getValue() > entry2.getValue().getValue() ? 1 : -1).getKey();
        }
    }

    @Override
    public void reset() {
    }
    
    private void valueIterator(){
//        codeHere();
    }

}
