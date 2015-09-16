/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import scenario.Edge;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class StatefullMDP extends AbstractMDP<String, Edge, Double> {

    @Override
    public void setValue(Edge action, Double value) {
        this.mdp.get(action.getSourceVertex()).put(action, value);
    }

    @Override
    public double getValue(Edge actionKey) {
        return this.mdp.get(actionKey.getSourceVertex()).get(actionKey);
    }

    @Override
    public void createMDP(List<Edge> actions) {
    }

    @Override
    public Edge getAction(String key) {
        Map<Edge, Double> mdp2 = mdp.get(key);
        float random = Params.RANDOM.nextFloat();
        if (random <= getEpsilon()) {
            List l = new ArrayList<>(mdp2.keySet());
            Collections.shuffle(l, Params.RANDOM);
            return (Edge) l.get(0);
        } else {
            return Collections.max(mdp2.entrySet(), (entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).getKey();
        }

    }

    @Override
    public void reset() {
    }

}
