/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.math;

import java.util.LinkedList;
import java.util.List;

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
        for (Double element : elements) {
            if (relativeValue(currentTravelTime, element) > delta) {
                return false;
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
     * @param deltaV current delta value
     * @return a positive relative delta value
     */
    public Double relativeValue(Double currentTravelTime, Double deltaV) {
        return 100 * deltaV / currentTravelTime;
    }

}
