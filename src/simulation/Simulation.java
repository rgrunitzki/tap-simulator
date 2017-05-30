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
package simulation;

import driver.Driver;
import driver.learning.stopping.AbstractStopCriterion;
import extensions.coadaptation.LearnerEdge;
import extensions.coadaptation.MultiObjectiveLinearCostFunction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;
import scenario.TAP;
import scenario.network.AbstractEdge;

/**
 * This class represents the core of the simulation. It is responsible for
 * controlling the whole simulation.
 *
 * @author Ricardo Grunitzki
 */
public class Simulation {

    /**
     * Traffic assignment problem.
     */
    private final TAP tap;

    /**
     * Object used for printing outputs in text files.
     */
    private FileWriter fileWriter = null;

    /**
     * Factory class to create ExecuterServices instances
     */
    private final ExecutorService eservice = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    /**
     * Task executor
     */
    private final CompletionService<Object> cservice = new ExecutorCompletionService<>(eservice);
    
    private String fileNameToPrint = "";
    
    private DecimalFormat df = new DecimalFormat("#0.0000");

    /**
     * Stopping criteria for the Reinforcement Learning algorithm.
     */
    public static final AbstractStopCriterion stopCriterion = Params.DEFAULT_STOP_CRITERION;

    /**
     * Creates and simulation object according to the TAP specifications.
     *
     * @param tap traffic assignment problem
     */
    public Simulation(TAP tap) {
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        Simulation.stopCriterion.setSimulation(this);
        this.tap = tap;
    }

    /**
     * Executes the traffic simulation.
     */
    public void execute() {

        //process drivers before simulation starting
        tap.getDrivers().parallelStream().forEach((driver) -> {
            try {
                driver.beforeSimulation();
            } catch (Exception e) {
                System.err.println("Error on  driver.beforeSimulation()");
                System.exit(1);
            }
        });

        //process links before simulation starting
        tap.getGraph().edgeSet().parallelStream().forEach((edge) -> {
            try {
                edge.beforeSimulation();
            } catch (Exception e) {
                System.err.println("Error on edge.beforeSimulation()");
                System.exit(1);
            }
        });
        
        Params.CURRENT_EPISODE = 0;
        
        while (!stopCriterion.stop()) {

            //episode's looping
            //for (Params.CURRENT_EPISODE = 0; Params.CURRENT_EPISODE < Params.MAX_EPISODES; Params.CURRENT_EPISODE++) {
            //process drivers at the begining of the current episode
            tap.getDrivers().parallelStream().forEach((driver) -> {
                try {
                    driver.beforeEpisode();
                } catch (Exception e) {
                    System.err.println("Error on driver.beforeEpisode()");
                    driver.beforeEpisode();
                    System.exit(1);
                }
                
            });

            //process edges at the begining of the current episode
            tap.getGraph().edgeSet().parallelStream().forEach((edge) -> {
                try {
                    edge.beforeEpisode();
                } catch (Exception e) {
                    System.err.println("Error on edge.beforeEpisode()");
                    System.exit(1);
                }
            });

            //runs the current episode
            runEpisode();

            //process drivers at the end of the current episode
            tap.getDrivers().parallelStream().forEach((driver) -> {
                try {
                    driver.afterEpisode();
                } catch (Exception e) {
                    System.err.println("Error on driver.afterEpisode()");
                    System.exit(1);
                }
            });

            //process drivers at the end of the current episode
            tap.getGraph().edgeSet().parallelStream().forEach((edge) -> {
                try {
                    edge.afterEpisode();
                } catch (Exception e) {
                    System.err.println("Error on edge.afterEpisode();");
                    System.exit(1);
                }
                
            });

            //mannage outputs
            String results = getSimulationOutputs();
            if (Params.PRINT_ON_TERMINAL) {
                System.out.print(results);
            }
            //prints outputs on file
            if (Params.PRINT_ON_FILE) {
                if (fileNameToPrint.equals("")) {
                    fileNameToPrint = getExperimentFileName();
                }
                this.printExperimentResultsOnFile(getExperimentPath(), fileNameToPrint, results);
            }
            
            Params.CURRENT_EPISODE++;
        }

        //proccess drivers at the end of the simulation
        tap.getDrivers().parallelStream().forEach((driver) -> {
            try {
                driver.afterSimulation();
            } catch (Exception e) {
                System.err.println("Error on driver.afterSimulation()");
                return;
            }
        });

        //process edges at the end of the simulation
        tap.getGraph().edgeSet().parallelStream().forEach((edge) -> {
            try {
                edge.afterSimulation();
            } catch (Exception e) {
                System.err.println("Error on edge.afterSimulation()");
                System.exit(1);
            }
        });
        
    }

    /**
     * Executes an specific episode.
     */
    @SuppressWarnings("empty-statement")
    private void runEpisode() {
        Params.CURRENT_STEP = 0;
        resetEdgesForEpisode();
        while ((!runStep()) && (Params.CURRENT_STEP++ < Params.MAX_STEPS));
    }
    
    private boolean runStep() {
        boolean finished = true;
        List<Driver> driversToProcess = new LinkedList<>();
        
        for (Driver d : this.tap.getDrivers()) {
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
        
        driversToProcess.stream().forEach((driver) -> {
            try {
                boolean result = this.cservice.take().isDone();
                if (!result) {
                    System.err.println("step A error");
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        //intermediate computation
        this.tap.getGraph().edgeSet().parallelStream().forEach((edge) -> {
            edge.clearCurrentFlow();
        });
        
        driversToProcess.stream().filter((driver) -> (!driver.hasArrived())).forEach((Driver driver) -> {
            if (driver.getCurrentEdge() == null) {
                System.err.println("Error on Edge's processing ");
            }
            driver.getCurrentEdge().proccess(driver);
        });

        //before step processing
        driversToProcess.parallelStream().forEach((driver) -> {
            this.cservice.submit(driver);
        });
        driversToProcess.parallelStream().forEach((driver) -> {
            try {
                boolean result = this.cservice.take().isDone();
                if (!result) {
                    System.err.println("step_b error");
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return false;
    }

    /**
     * Returns the average simulation travel cost.
     *
     * @return average cost of all drivers
     */
    public double averageTravelCost() {
        double avgcost = 0;
        for (AbstractEdge e : tap.getGraph().edgeSet()) {
            avgcost += e.getTotalFlow() * e.getCost();
        }
        return (avgcost / (tap.getDrivers().size() * Params.PROPORTION));
    }

    /**
     * Returns the average learning effort.
     *
     * @return average learning effort of all drivers
     */
    public double getLearningEffort() {
        double learningEffort = 0;
        for (Driver d : tap.getDrivers()) {
            learningEffort += d.getLearningEffort();
        }
        return learningEffort / tap.getDrivers().size();
    }
    
    private void resetEdgesForEpisode() {
        this.tap.getGraph().edgeSet().parallelStream().forEach((e) -> {
            e.reset();
        });
    }
    
    private void resetEdgesForSimulation() {
        this.tap.getGraph().edgeSet().parallelStream().forEach((e) -> {
            e.resetAll();
        });
    }
    
    private void resetDrivers() {
        tap.getDrivers().parallelStream().forEach((driver) -> {
            driver.resetAll();
        });
    }

    /**
     * Resets the simulation
     */
    public void reset() {
        this.resetDrivers();
        this.resetEdgesForSimulation();
        this.fileNameToPrint = "";
        this.fileWriter = null;
    }

    /**
     * Finishes the executor service.
     */
    public void end() {
        eservice.shutdown();
    }
    
    private String getAverageTravelCosts() {
        String out = df.format(averageTravelCost()); //Average Cost

        if (Params.PRINT_OD_PAIRS_AVG_COST) {
            
            List<String> keys = new ArrayList<>(tap.getOdpairs().keySet());
            Collections.sort(keys);
            
            for (String key : keys) {
                out += Params.COLUMN_SEPARATOR + tap.getOdpairs().get(key).getAverageCost();
            }
        }
        return out;
    }

    /**
     * calculates links flows.
     *
     * @return
     */
    private String getFlows() {
        String out = "";
        List<AbstractEdge> keys = new ArrayList<>(tap.getGraph().edgeSet());
        Collections.sort(keys);
        for (AbstractEdge e : keys) {
            out += e.getTotalFlow() + Params.COLUMN_SEPARATOR;
        }
        return out;
    }

    /**
     * Returns the outputs of the simulation
     *
     * @return a String with outputs
     */
    public String getSimulationOutputs() {
        String output = "";
        if (Params.CURRENT_EPISODE == 0) {
            output += getExperimentOutputHeader() + "\n";
        }
        //episode
        output += Params.CURRENT_EPISODE;
        //overal travel time
        output += Params.COLUMN_SEPARATOR + getAverageTravelCosts();
        if (Params.DEFAULT_EDGE == LearnerEdge.class) {
            output += Params.COLUMN_SEPARATOR + this.getOnlyTravelCost();
            output += Params.COLUMN_SEPARATOR + this.getOnlyMonetaryCost();
        }
        if (Params.PRINT_EFFORT) {
            output += Params.COLUMN_SEPARATOR + df.format(getLearningEffort());
        }
        if (Params.PRINT_DELTA) {
            output += Params.COLUMN_SEPARATOR + df.format(Simulation.stopCriterion.stoppingValue());
        }
        if (Params.PRINT_FLOWS) {
            output += Params.COLUMN_SEPARATOR + getFlows();
        }
        
        return output + "\n";
    }
    
    private void printExperimentResultsOnFile(String directory, String fileName, String content) {

        //verifica e cria o diretório
        File file = new File(directory);
        if (!file.exists()) {
            file.mkdirs();
        }

        //initialize the writer
        if (fileWriter == null) {
            try {
                
                fileWriter = new FileWriter(file.getPath() + File.separator + fileName);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try {
            //print the outputs
            fileWriter.write(content);
        } catch (IOException ex) {
            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (Params.CURRENT_EPISODE >= Params.MAX_EPISODES - 1) {
            
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private String getExperimentPath() {
        String path = Params.OUTPUT_DIRECTORY + File.separator + tap.getNetworkName();
        
        for (Object pair : tap.getDrivers().get(0).getParameters()) {
            Pair p = (Pair) pair;
            if (p.getRight().equals("")) {
                path += File.separator + p.getLeft();
            } else {
                path += File.separator + p.getLeft() + "_" + p.getRight();
            }
        }
        return path;
    }
    
    private String getExperimentOutputHeader() {
        String output = "";
        
        output += "#parameters";
        for (Object pair : tap.getDrivers().get(0).getParameters()) {
            Pair p = (Pair) pair;
            output += " " + p.getLeft() + ": " + p.getRight();
        }

        //episode label
        output += "\n" + Params.COMMENT_CHARACTER + "episode";
        //overal travel time label
        output += Params.COLUMN_SEPARATOR + "overal-tt";

        //@TODO: this is temporary
        if (Params.DEFAULT_EDGE == LearnerEdge.class) {
            output += Params.COLUMN_SEPARATOR + "f1";
            output += Params.COLUMN_SEPARATOR + "f2";
        }
        
        if (Params.PRINT_EFFORT) {
            output += Params.COLUMN_SEPARATOR + "effort";
        }
        if (Params.PRINT_DELTA) {
            output += Params.COLUMN_SEPARATOR + "stopping-value";
        }
        
        if (Params.PRINT_OD_PAIRS_AVG_COST) {
            
            List<String> keys = new ArrayList<>(tap.getOdpairs().keySet());
            Collections.sort(keys);
            
            for (String key : keys) {
                output += Params.COLUMN_SEPARATOR + tap.getOdpairs().get(key).getName();
            }
        }
        
        if (Params.PRINT_FLOWS) {
            List<AbstractEdge> keys = new ArrayList<>(tap.getGraph().edgeSet());
            Collections.sort(keys);
            for (AbstractEdge e : keys) {
                output += Params.COLUMN_SEPARATOR + e.getName();
            }
        }
        return output;
    }
    
    private String getExperimentFileName() {
        String name = tap.getClazz().getSimpleName().toLowerCase()
                + "_" + tap.getNetworkName();
        
        for (Object pair : tap.getDrivers().get(0).getParameters()) {
            Pair p = (Pair) pair;
            if (!p.getRight().equals("")) {
                name += "_" + p.getLeft().toString().charAt(0) + p.getRight();
            }
        }
        
        if (new File(getExperimentPath()).exists()) {
            name += "_" + (new File(getExperimentPath()).listFiles().length + 1);
        } else {
            name += "_" + 1;
        }
        name += ".txt";
        return name;
    }

    //@TODO: devo excluir no futuro
    private double getOnlyTravelCost() {
        double cost = 0.;
        for (AbstractEdge edge : this.tap.getGraph().edgeSet()) {
            LearnerEdge le = ((LearnerEdge) edge);
            MultiObjectiveLinearCostFunction cf = (MultiObjectiveLinearCostFunction) ((LearnerEdge) edge).getCostFunction();
            cost += cf.getTravelCost(edge, le.getTotalFlow()) * le.getTotalFlow();
        }
        return cost / tap.getDrivers().size();
    }

    //@TODO: devo excluir no futuro
    private double getOnlyMonetaryCost() {
        double cost = 0.;
        for (AbstractEdge edge : this.tap.getGraph().edgeSet()) {
            LearnerEdge le = ((LearnerEdge) edge);
            MultiObjectiveLinearCostFunction cf = (MultiObjectiveLinearCostFunction) ((LearnerEdge) edge).getCostFunction();
            cost += cf.getMonetaryCost(edge, le.getTotalFlow()) * le.getTotalFlow();
        }
        return cost / tap.getDrivers().size();
    }
    
    public TAP getTap() {
        return tap;
    }
    
    public AbstractStopCriterion getStopCriterion() {
        return stopCriterion;
    }
    
}
