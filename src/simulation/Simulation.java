/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import driver.Driver;
import driver.learning.QLStateless;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;
import scenario.TAP;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import scenario.AbstractEdge;
import scenario.ODPair;

/**
 *
 * @author rgrunitzki
 */
public class Simulation {

    private final List<Driver> drivers;
    private final Graph<String, AbstractEdge> graph;
    private final Map<String, ODPair> odpairs;
    private final TAP tap;

    //Multi core objects
    //Factory class to create ExecuterServices instances
    private final ExecutorService eservice = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    //Task executor
    private final CompletionService<Object> cservice = new ExecutorCompletionService<>(eservice);

    public Simulation(TAP tap) {
        this.drivers = tap.getDrivers();
        this.graph = tap.getGraph();
        this.odpairs = tap.getOdpairs();
        this.tap = tap;
    }

    String fileNameToPrint = "";

    public void execute() {

        drivers.parallelStream().forEach((driver) -> {
            driver.beforeSimulation();
        });

        for (Params.CURRENT_EPISODE = 0; Params.CURRENT_EPISODE < Params.MAX_EPISODES; Params.CURRENT_EPISODE++) {

            drivers.parallelStream().forEach((driver) -> {
                driver.beforeEpisode();
            });

            runEpisode();

            drivers.parallelStream().forEach((driver) -> {
                driver.afterEpisode();
            });

            String results = getSimulationOutputs();
            if (Params.PRINT_ON_TERMINAL) {
                System.out.print(results);
            }

            if (Params.PRINT_ON_FILE) {
                if (fileNameToPrint.equals("")) {
                    fileNameToPrint = getExperimentFileName();
                }
                this.printExperimentResultsOnFile(getExperimentPath(), fileNameToPrint, results);
            }

        }
        
        System.out.println("Quantidade de veículos: " + drivers.size());

        //post-simulation processing
        drivers.parallelStream().forEach((driver) -> {
            driver.afterSimulation();
        });
        
    }

    public static int step = 0;

    @SuppressWarnings("empty-statement")
    private void runEpisode() {
        step = 0;
//        int step = 0;
        resetEdgesForEpisode();
        while ((!runStep()) && (step++ < Params.MAX_STEPS));
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

        driversToProcess.stream().forEach((driver) -> {
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
        this.graph.edgeSet().parallelStream().forEach((edge) -> {
            edge.clearCurrentFlow();
        });
        
        driversToProcess.parallelStream().filter((driver) -> (!driver.hasArrived())).forEach((driver) -> {
            if(driver.getCurrentEdge()==null){
                System.out.println("deu pau");
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
                    System.out.println("step_b error");
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return false;
    }

    public double simulationTravelTime() {
        double avgcost = 0;
        for (AbstractEdge e : graph.edgeSet()) {
            avgcost += e.getTotalFlow() * e.getCost();
        }
        return (avgcost / (drivers.size()*Params.PROPORTION));
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
        this.fileNameToPrint = "";
        this.fileWriter = null;
    }

    public void end() {
        eservice.shutdown();
    }

    //print flows in a formated manner
    private void printFlowsOnTerminal() {
        //print the flow of the used links
        System.out.println("link" + Params.COLUMN_SEPARATOR + "flow" + Params.COLUMN_SEPARATOR + "cost");
        double soma = 0;
        for (AbstractEdge e : graph.edgeSet()) {
            soma += e.getTotalFlow() * e.getCost();
            System.out.println(e.getName() + Params.COLUMN_SEPARATOR + e.getTotalFlow() + Params.COLUMN_SEPARATOR + e.getCost());
        }
    }

    private String getAverageTravelCosts() {
        String out = String.valueOf(simulationTravelTime()); //Average Cost

        if (Params.PRINT_OD_PAIRS_AVG_COST) {

            List<String> keys = new ArrayList<>(odpairs.keySet());
            Collections.sort(keys);

            for (String key : keys) {
                out += Params.COLUMN_SEPARATOR + odpairs.get(key).getAverageCost();
            }
        }
        return out;
    }

    //calculates links flows;
    private String getFlows() {
        String out = "";
        List<AbstractEdge> keys = new ArrayList<>(graph.edgeSet());
        Collections.sort(keys);
        for (AbstractEdge e : keys) {
            out += e.getTotalFlow() + Params.COLUMN_SEPARATOR;
        }
        return out;
    }

//    private String getTravelTimeAndFlows() {
//        String cost = getAverageTravelCosts();
//        if (Params.PRINT_FLOWS) {
//            cost += Params.SEPARATOR + getFlows();
//        }
//        return cost;
//    }
    public String getSimulationOutputs() {
        String output = "";
        if (Params.CURRENT_EPISODE == 0) {
            output += getExperimentOutputHeader() + "\n";
        }
        output += Params.CURRENT_EPISODE + Params.COLUMN_SEPARATOR + getAverageTravelCosts();
        if (Params.PRINT_FLOWS) {
            output += Params.COLUMN_SEPARATOR + getFlows();
        }

        return output + "\n";
    }

    private FileWriter fileWriter = null;

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

        for (Object pair : drivers.get(0).getParameters()) {
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
        for (Object pair : drivers.get(0).getParameters()) {
            Pair p = (Pair) pair;
            output += " " + p.getLeft() + ": " + p.getRight();
        }

        output += "\n" + Params.COMMENT_CHARACTER + "episode" + Params.COLUMN_SEPARATOR + "overal_tt";

        if (Params.PRINT_OD_PAIRS_AVG_COST) {

            List<String> keys = new ArrayList<>(odpairs.keySet());
            Collections.sort(keys);

            for (String key : keys) {
                output += Params.COLUMN_SEPARATOR + odpairs.get(key).getName();
            }
        }

        if (Params.PRINT_FLOWS) {
            List<AbstractEdge> keys = new ArrayList<>(graph.edgeSet());
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

        for (Object pair : drivers.get(0).getParameters()) {
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
        //class+_ow_e0.91_a099_g0.8.txt
        return name;
    }

}
