/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import driver.Driver;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import scenario.Edge;
import scenario.TAP;
import org.jgrapht.Graph;

/**
 *
 * @author rgrunitzki
 */
public class Simulation {

    private final List<Driver> drivers;
    private final Graph<String, Edge> graph;

    //Multi core objects
    //Factory class to create ExecuterServices instances
    private final ExecutorService eservice = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    //Task executor
    private final CompletionService<Object> cservice = new ExecutorCompletionService<>(eservice);

    public Simulation(TAP tap) {
        this.drivers = tap.getDrivers();
        this.graph = tap.getGraph();
        Params.DEMAND_SIZE = drivers.size();
    }

    public void execute() {

        //System.gc();
        //pre-simulation processing        
        drivers.parallelStream().forEach((driver) -> {
            driver.beforeSimulation();
        });

        for (Params.EPISODE = 0; Params.EPISODE < Params.EPISODES; Params.EPISODE++) {
            
            drivers.parallelStream().forEach((driver) -> {
                    driver.beforeEpisode();
            });
            
            runEpisode();

            drivers.parallelStream().forEach((driver) -> {
                driver.afterEpisode();
            });

            if (Params.PRINT_ALL_EPISODES) {
                System.out.println(Params.EPISODE + " " + getAverageCost());
            }

        }

        //post-simulation processing
        drivers.parallelStream().forEach((driver) -> {
            driver.afterSimulation();
        });
    }

    @SuppressWarnings("empty-statement")
    private void runEpisode() {
        int step = 0;
        resetEdgesForEpisode();
        while ((!runStep()) && (step++ < Params.STEPS));
    }

    private boolean runStep() {
        boolean finished = true;
        List<Driver> driversToProcess = new LinkedList<>();

        for (Driver d : this.drivers) {
            if (!d.hasArrived()) {
                finished = false;
                driversToProcess.add(d);
            }
        }
        if (finished) {
            return true;
        }

        //before step processing
        driversToProcess.parallelStream().forEach((driver) -> {
            this.cservice.submit(driver);
        });

        driversToProcess.parallelStream().forEach((_item) -> {
            try {
                boolean result = this.cservice.take().isDone();
                if (!result) {
                    System.out.println("step A error");
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //intermediate computation
        this.graph.edgeSet().parallelStream().forEach((e) -> {
            e.clearCurrentFlow();
        });

        driversToProcess.parallelStream().filter((d) -> (!d.hasArrived())).forEach((d) -> {
            d.getCurrentEdge().incrementFlow();
        });
        
        //before step processing
        driversToProcess.parallelStream().forEach((driver) -> {
            this.cservice.submit(driver);
        });
        driversToProcess.parallelStream().forEach((_item) -> {
            try {
                boolean result = this.cservice.take().isDone();
                if (!result) {
                    System.out.println("step_b error");
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return false;
    }

    public void printLinksFlow() {
        //print the flow of the used links
        double soma = 0;
        for (Edge e : graph.edgeSet()) {
            soma += e.getTotalFlow() * e.getCost();
            System.out.println(e.getName() + "\tflow: " + e.getTotalFlow() + "\tCost:" + e.getCost());
        }
        System.out.println("final travel time: " + soma / drivers.size());

    }

    public double getAverageCost() {

        double avgCost = 0;
        for (Edge e : graph.edgeSet()) {
            avgCost += e.getTotalFlow() * e.getCost();
        }
        avgCost /= drivers.size();

        return avgCost;
    }

    private void resetEdgesForEpisode() {
        this.graph.edgeSet().parallelStream().forEach((e) -> {
            e.reset();
        });
    }

    private void resetEdgesForSimulation() {
        this.graph.edgeSet().parallelStream().forEach((e) -> {
            e.resetAll();
        });
    }

    private void resetDrivers() {
        drivers.parallelStream().forEach((driver) -> {
            driver.resetAll();
        });
    }

    public void reset() {
        this.resetDrivers();
        this.resetEdgesForSimulation();
    }

    public void end() {
        eservice.shutdown();
    }
}
