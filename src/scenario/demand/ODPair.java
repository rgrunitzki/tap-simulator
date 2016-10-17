/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scenario.demand;

import driver.Driver;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an OD-pair of the TAP. The OD-pair is composed of a collection of
 * drivers with the same origin-destination restrictions.
 *
 * @author Ricardo Grunitzki
 * @param <DriverType> The type of the drivers
 */
public class ODPair<DriverType> {

    private final String name;

    private final List<DriverType> drivers = new ArrayList<>();

    /**
     * Creates an OD pair according to the OD restrictions.
     *
     * @param name The description name of the OD pair.
     */
    public ODPair(String name) {
        this.name = name;
    }

    /**
     * Adds a Driver to the OD-pair.
     *
     * @param driver Driver to be added to the OD-pair
     */
    public void addDriver(DriverType driver) {
        drivers.add(driver);
    }

    /**
     * Returns the average travel cost the drivers of this OD-pair.
     *
     * @return average travel cost
     */
    public double getAverageCost() {
        Double soma = 0.0;
        for (Object driver : drivers) {
            soma += ((Driver) driver).getTravelTime();
        }
        return soma / drivers.size();
    }

    /**
     * Returns the name of the OD-pair.
     *
     * @return OD-pair name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of drivers of this OD-pair.
     *
     * @return list of drivers of this OD-pair
     */
    public List<DriverType> getDrivers() {
        return drivers;
    }

    /**
     * Returns the amount of drivers of this OD-pair
     *
     * @return number of drivers
     */
    public synchronized int demandSize() {
        return drivers.size();
    }

}
