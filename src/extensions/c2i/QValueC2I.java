/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions.c2i;

import simulation.Params;

/**
 *
 * @author Ricardo Grunitzki
 */
public class QValueC2I implements Comparable<QValueC2I>{

    private double value;
    private double reward;
    private int episode;
    private boolean experienced;

    public QValueC2I() {
        this.value = 0.0;
        this.reward = 0.0;
        this.episode = 0;
        this.experienced = false;
    }

    public QValueC2I(double value, double reward, int episode, boolean experienced) {
        this.value = value;
        this.reward = reward;
        this.episode = episode;
        this.experienced = experienced;
    }

    public void updateByMessage(MessageC2I message) {
        this.reward = message.getValue();
        if (value == 0.0) {
            this.value = reward;
        }
        this.experienced = false;
        this.episode = Params.CURRENT_EPISODE;
    }

    public double getValue() {
        return value;
    }

    public double getReward() {
        return reward;
    }

    @Override
    public int compareTo(QValueC2I object) {
        return Double.compare(value, object.value);
    }
    
    

}
