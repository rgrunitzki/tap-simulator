/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scenario;

import driver.Driver;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rgrunitzki
 */
public class ODPair<T> {

    private String name;

    private List<T> drivers = new ArrayList<>();

    public ODPair(String name) {
        this.name = name;
    }

    public void addDriver(T driver) {
        drivers.add(driver);
    }

    public double getAverageCost() {
        Double soma = 0.0;
        for (Object driver : drivers) {
            soma += ((Driver) driver).getTravelTime();
        }
        return soma / drivers.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<T> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<T> drivers) {
        this.drivers = drivers;
    }
    
    public synchronized int demandSize(){
        return drivers.size();
    }

}
