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
package driver.learning.exploration;

import java.util.Map;
import java.util.Random;
import simulation.Params;

/**
 * Implementation of Soft-Max exploration.
 *
 * @author Ricardo Grunitzki
 * @param <State>
 * @param <Action>
 * @param <Value>
 */
public class SoftMaxExploration<State, Action, Value extends Comparable> extends ExplorationStrategy<State, Action, Value> {

    /**
     * Tau parameter of softmax exploration.
     */
    public static double TAU = 10;

    @Override
    public Action getAction(Map<Action, Value> mdp) {

        //sum of values \sum_i=1^n exp^{qi/tau}
        String values = "sum: ";
        double sum = 0.0;
        for (Action action : mdp.keySet()) {
            sum += Math.exp((Double) mdp.get(action) / getTau());
        }
        values += sum;
        //random value representing the choosed action
        double random = new Random().nextDouble();
        values += ";\trandom: " + random;
        //find the action selected through the random value
        double auxCounter = 0;
        for (Action action : mdp.keySet()) {
            auxCounter += Math.exp((Double) mdp.get(action) / getTau()) / sum;
            values += ";\tqi" + auxCounter;
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
        double tau = TAU - 0.1 * Params.CURRENT_EPISODE;
        if (tau < 1.0) {
            return 1;
        } else {
            return tau;
        }
    }

}
