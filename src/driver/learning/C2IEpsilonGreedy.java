/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import extensions.c2i.EdgeC2I;
import extensions.c2i.InformationType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import scenario.AbstractEdge;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 * @param <State>
 * @param <Action>
 * @param <Value>
 */
public class C2IEpsilonGreedy<State, Action, Value extends Comparable> extends ExplorationPolicy<State, Action, Value> {

    private static double SG_INIT = .02;
//    private static double SG_END = 1;

    @Override
    public Action getAction(Map<Action, Value> mdp) {
        float random = Params.RANDOM.nextFloat();
        if (random <= getEpsilon()) {
            if (QLStatefullC2I.INFORMATION_TYPE != InformationType.None && EdgeC2I.SHORTEST_PATHS != null
                    && superExploration() && Params.RANDOM.nextFloat() > 0.6) {
                return null;
            } else {
                List actions = new ArrayList<>(mdp.keySet());
                Collections.shuffle(actions, Params.RANDOM);
                return (Action) actions.get(0);
            }
        } else {
            return Collections.max(mdp.entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue())).getKey();
        }
    }

    @Override
    public void episodeUpdate() {

    }

    @Override
    public void reset() {
    }

    private double getEpsilon() {
        double epsilon = 1 * Math.pow(Params.E_DECAY_RATE, Params.CURRENT_EPISODE);
        return epsilon;
    }

    public static boolean superExploration() {
        return (Params.CURRENT_EPISODE >= Params.EPISODES * SG_INIT);
    }
//        return (Params.CURRENT_EPISODE >= Params.EPISODES * SG_INIT && Params.CURRENT_EPISODE <= Params.EPISODES * SG_END);
//    }

}
