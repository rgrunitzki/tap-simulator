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
