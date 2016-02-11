/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package experiments.bazzan;

import driver.learning.QLStatefull;

/**
 *
 * @author rgrunitzki
 */
public class AditionalData {

    private Double bestValue = Double.MAX_VALUE;

    private Double lastValue = 0.0;

    private double values = 0;

    private int cont = 0;

    public void addValue(Double value) {
        values += value;
        cont++;
        lastValue = value;
        if (value < bestValue) {
            bestValue = value;
        }
    }

    public void reset() {
        cont = 0;
        values = 0.0;
        lastValue = 0.0;
        bestValue = 0.0;
    }

    public Double getBestValue() {
        return bestValue;
    }

    public Double getLastValue() {
        return lastValue;
    }

    public Double getAverageValue() {
        return values / cont;
    }

    public Double getValue() {
        switch (QLStatefull.INFORMATION_TYPE){
            case Average:
                return getAverageValue();
            case Best:
                return getBestValue();
            case Last:
                return getLastValue();
            case None:
                return 0.0;
            default:
                throw new AssertionError(QLStatefull.INFORMATION_TYPE.name());
            
        }
    }
}
