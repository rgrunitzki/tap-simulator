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
package util.math;

import extensions.hierarchical.QLStatefullHierarchical;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import simulation.Params;

/**
 * Creates an structure that keeps the last n elements of an list
 *
 * @author Ricardo Grunitzki
 */
public class DynamicList {

    /**
     * List of elements
     */
    private LinkedList<Double> elements;

    /**
     * size of the list of elements
     */
    private int size = 0;

    /**
     * Creates an list of elements with dimensions according to {@code size}.
     *
     * @param size
     */
    public DynamicList(int size) {
        this.size = size;
        this.elements = new LinkedList<>();
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
    }

    /**
     * Add an element to the list
     *
     * @param element
     */
    public void add(Double element) {
        //adds an element to the end of the list
        elements.addLast(element);
        //verifies if an item should be removed
        if (elements.size() > size) {
            //removes the first element of an list
            elements.removeFirst();
        }
    }

    /**
     * Returns the list with the stored elements
     *
     * @return a list of elements
     */
    public List getElements() {
        return elements;
    }

    /**
     * Verifies if the stored elements values are less than {@code delta}. If at
     * least one element is greater than {@code delta}, the method returns
     * {@code false}.
     *
     * @param delta
     * @param currentTravelTime
     * @return
     */
    public boolean check(Double delta, Double currentTravelTime) {
        if (elements.size() < size) {
            return false;
        }

        for (Double element : elements) {
//            System.err.println(df.format(relativeValue(currentTravelTime, element)) + "\t" + df.format(delta));
            if (QLStatefullHierarchical.FIRST_LEVEL) {
                if (element > delta) {
                    return false;
                }
            } else {
                if (relativeValue(currentTravelTime, element) > delta) {
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * Returns the average of the last elements
     *
     * @return
     */
    public Double getAverage() {
        Double sum = .0;
        for (Double element : elements) {
            sum += element;
        }
        return sum / elements.size();
    }

    /**
     * Computes the relative delta value.
     *
     * @param currentTravelTime current travel time of the simulation
     * @param deltaValue current delta value
     * @return a positive relative delta value
     */
    public Double relativeValue(Double currentTravelTime, Double deltaValue) {
        return (deltaValue) / currentTravelTime;
    }

    /**
     * Removes all stored elements in the dynamic list.
     */
    public void reset() {
        this.elements.clear();
    }

    /**
     * used for tests. must be removed
     */
    private DecimalFormat df = new DecimalFormat("#0.0000");

}
