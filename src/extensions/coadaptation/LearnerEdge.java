/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions.coadaptation;

import driver.learning.QLStatefull;
import driver.learning.exploration.EpsilonDecreasing;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import scenario.network.AbstractCostFunction;
import scenario.network.AbstractEdge;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class LearnerEdge extends AbstractEdge {

    static double INTERVAL = 0.1d;
    private final double pMax = 10.0;

    private final Map<Double, Double> mdp;

    private double currentAction = 1.;

    static int test = 0;

    public LearnerEdge(AbstractCostFunction costFunction) {
        super(costFunction);
        this.mdp = new ConcurrentHashMap<>();
    }

    @Override
    public void beforeSimulation() {
        //cretes the mdp;
        double action = INTERVAL;
        while (action <= 1.0d) {
            this.mdp.put(action, 0.0);
            action += INTERVAL;
        }

    }

    @Override
    public void afterSimulation() {
//        System.out.println(mdp.entrySet());
    }

    @Override
    public void beforeEpisode() {
        //random number
        float random = Params.RANDOM.nextFloat();
        //current epsilon based on epsilon-decreasing strategy
        double epsilon = EpsilonDecreasing.EPSILON_INITIAL * Math.pow(EpsilonDecreasing.EPSILON_DECAY, Params.CURRENT_EPISODE);
        if (random <= epsilon) {
            //exploration phase
            //list of actions
            List actions = new ArrayList<>(mdp.keySet());
            //shuffle the list
            Collections.shuffle(actions, Params.RANDOM);
            //take the first action from the list
            currentAction = (double) actions.get(0);
        } else {
            //exploitation phase
            //take the action with the highest q-value
            currentAction = Collections.max(mdp.entrySet(), (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue())).getKey();
        }

        if (this.getName().equalsIgnoreCase("H|K!")) {
            System.out.println("");
            System.out.println(currentAction);
//            System.out.println(mdp.entrySet());
            System.out.println("");
        }
    }

    @Override
    public void afterEpisode() {
        double qa = mdp.get(currentAction);
        double reward = getReward();
        qa = (1 - QLStatefull.ALPHA) * qa + (QLStatefull.ALPHA * reward);
        mdp.put(currentAction, qa);
    }

    /**
     * Returns the reward signal. 
     * Reward is positive because the goal is maximize the flow.
     * @return double value representing the reward.
     */
    private double getReward() {
        return getTotalFlow();
    }

    @Override
    public synchronized void resetAll() {
        super.resetAll();
        for (Double action : mdp.keySet()) {
            this.mdp.put(action, 0.0);
        }
    }

    @Override
    public synchronized void reset() {
        super.reset(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @return
     */
    public double getPriceProportion() {
        return currentAction;
    }

    /**
     *
     * @return
     */
    public double getpMax() {
        return pMax;
    }

}