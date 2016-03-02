/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions.c2i;

import driver.learning.QLStatefullC2I;
import static extensions.c2i.InformationType.Average;
import static extensions.c2i.InformationType.Best;
import static extensions.c2i.InformationType.Last;
import static extensions.c2i.InformationType.None;
import java.util.Random;

/**
 *
 * @author rgrunitzki
 */
public class MessageC2I {

    private Double bestInformation = Double.MAX_VALUE;

    private Double lastInformation = 0.0;

    private double sumInformation = 0;

    private int counter = 0;

    public void addValue(Double value) {
        sumInformation += value;
        counter++;
        lastInformation = value;
        if (value < bestInformation) {
            bestInformation = value;
        }
    }

    public Double getBestInformation() {
        return bestInformation;
    }

    public Double getLastInformation() {
        return lastInformation;
    }

    public Double getAverageInformation() {
        return sumInformation / counter;
    }

    public Double getValue() {
        switch (QLStatefullC2I.INFORMATION_TYPE) {
            case Average:
                return getAverageInformation();
            case Best:
                return getBestInformation();
            case Last:
                return getLastInformation();
            case None:
                return 0.0;
            default:
                throw new AssertionError(QLStatefullC2I.INFORMATION_TYPE.name());

        }
    }
}
