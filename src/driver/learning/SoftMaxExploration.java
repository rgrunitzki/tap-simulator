/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import java.util.Map;
import java.util.Random;
import simulation.Params;

/**
 *
 * @author Ricardo Grunitzki
 * @param <State>
 * @param <Action>
 * @param <Value>
 */
public class SoftMaxExploration<State, Action, Value extends Comparable> extends ExplorationPolicy<State, Action, Value> {

    public static double TAU = 10;

    @Override
    public Action getAction(Map<Action, Value> mdp) {

        //sum of values \sum_i=1^n exp^{qi/tau}
        String values = "sum: ";
        double sum = 0.0;
        for (Action action : mdp.keySet()) {
            sum += Math.exp((Double) mdp.get(action) / getTau());
        }
        values +=sum;
        //random value representing the choosed action
        double random = new Random().nextDouble();
        values+=";\trandom: " + random;
        //find the action selected through the random value
        double auxCounter = 0;
        for (Action action : mdp.keySet()) {
            auxCounter += Math.exp((Double) mdp.get(action) / getTau()) / sum;
            values +=";\tqi"+auxCounter;
            if (random <= auxCounter) {
                return action;
            }
        }
        System.out.println(values);
        return null;

    }

    @Override
    public void episodeUpdate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private double getTau() {
        double tau = TAU - 0.1*Params.CURRENT_EPISODE;
        if (tau < 1.0) {
            return 1;
        } else {
            return tau;
        }
    }

}
