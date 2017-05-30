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
