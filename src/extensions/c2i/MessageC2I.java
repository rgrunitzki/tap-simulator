/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions.c2i;

import static extensions.c2i.InformationType.Average;
import static extensions.c2i.InformationType.Best;
import static extensions.c2i.InformationType.Last;
import static extensions.c2i.InformationType.None;

/**
 *
 * @author Ricardo Grunitzki
 */
public class MessageC2I {

    private int bestInformation = Integer.MAX_VALUE;

    private int lastInformation = 0;

    private int sumInformation = 0;

    private int counter = 0;

    public void addValue(int value) {
        sumInformation += value;
        counter++;
        lastInformation = value;
        if (value < bestInformation) {
            bestInformation = value;
        }
    }

    public int getBestInformation() {
        if (bestInformation == Integer.MAX_VALUE) {
            return 0;
        } else {
            return bestInformation;
        }
    }

    public int getLastInformation() {
        return lastInformation;
    }

    public int getAverageInformation() {
        if (counter == 0) {
            return counter;
        } else {
            return (int) sumInformation / counter;
        }
    }

    public int getValue() {
        switch (QLStatefullC2I.INFORMATION_TYPE) {
            case Average:
                return getAverageInformation();
            case Best:
                return getBestInformation();
            case Last:
                return getLastInformation();
            case None:
                return 0;
            default:
                throw new AssertionError(QLStatefullC2I.INFORMATION_TYPE.name());

        }
    }
}
