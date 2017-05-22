package util.math;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * This class creates a Simple Moving Average Structure.
 *
 * @author Ricardo Grunitzki
 */
public class SimpleMovingAverage {

    private final int size;
    private double total = 0d;
    private int index = 0;
    private final double samples[];

    /**
     * Creates a fixed-sized Simple Moving Average (SMA).
     *
     * @param size of the SMA.
     */
    public SimpleMovingAverage(int size) {
        this.size = size;
        samples = new double[size];
        for (int i = 0; i < size; i++) {
            samples[i] = 0d;
        }
    }

    /**
     * Adds an element to the Simple Moving Average list of elements.
     *
     * @param x element to be added.
     */
    public void add(double x) {
        total -= samples[index];
        samples[index] = x;
        total += x;
        if (++index == size) {
            index = 0; // cheaper than modulus
        }
    }

    /**
     * Get the average of the simple moving average.
     *
     * @return an double identifier
     */
    public double getAverage() {
        return total / size;
    }
}
