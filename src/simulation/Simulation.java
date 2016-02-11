/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import driver.Driver;
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
import scenario.Edge;
import scenario.TAP;
import org.jgrapht.Graph;
import scenario.ODPair;

/**
 *
 * @author rgrunitzki
 */
public class Simulation {

    private final List<Driver> drivers;
    private final Graph<String, Edge> graph;
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
        Params.DEMAND_SIZE = drivers.size();
    }

    String fileNameToPrint = "";

    public void execute() {

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

            if (Params.PRINT_ALL_EPISODES) {
//                System.out.println(getSimulationOutputs());
//                System.out.println(Params.EPISODE + Params.SEPARATOR + getTravelTimeAndFlows());
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

    public double simulationTravelTime() {
        double avgcost = 0;
        for (Edge e : graph.edgeSet()) {
            avgcost += e.getTotalFlow() * e.getCost();
        }
        return (avgcost / drivers.size());
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
        System.out.println("link" + Params.SEPARATOR + "flow" + Params.SEPARATOR + "cost");
        double soma = 0;
        for (Edge e : graph.edgeSet()) {
            soma += e.getTotalFlow() * e.getCost();
            System.out.println(e.getName() + Params.SEPARATOR + e.getTotalFlow() + Params.SEPARATOR + e.getCost());
        }
    }

    private String getAverageTravelCosts() {
        String out = String.valueOf(simulationTravelTime()); //Average Cost

        if (Params.PRINT_ALL_OD_PAIR) {

            List<String> keys = new ArrayList<>(odpairs.keySet());
            Collections.sort(keys);

            double totalCost = 0.0;
            int demand = 0;
            for (String key : keys) {
                out += Params.SEPARATOR + odpairs.get(key).getAverageCost();
                totalCost += odpairs.get(key).demandSize() * odpairs.get(key).getAverageCost();
                demand += odpairs.get(key).demandSize();
            }

            System.out.println("avg_tt: " + simulationTravelTime() + "\tavg_tt2:" + totalCost / demand);
            System.out.println("");
        }
        return out;
    }

    //calculates links flows;
    private String getFlows() {
        String out = "";
        List<Edge> keys = new ArrayList<>(graph.edgeSet());
        Collections.sort(keys);
        for (Edge e : keys) {
            out += e.getTotalFlow() + Params.SEPARATOR;
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
        if (Params.EPISODE == 0) {
            output += getExperimentOutputHeader() + "\n";
        }
        output += Params.EPISODE + Params.SEPARATOR + getAverageTravelCosts();
        if (Params.PRINT_FLOWS) {
            output += Params.SEPARATOR + getFlows();
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

        if (Params.EPISODE >= Params.EPISODES - 1) {

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String getExperimentPath() {
        String path = Params.OUTPUTS_DIRECTORY + File.separator + tap.getNetworkName();

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

        output += "\n" + Params.COMMENT + "episode" + Params.SEPARATOR + "overal_tt";

        if (Params.PRINT_ALL_OD_PAIR) {

            List<String> keys = new ArrayList<>(odpairs.keySet());
            Collections.sort(keys);

            for (String key : keys) {
                output += Params.SEPARATOR + odpairs.get(key).getName();
            }
        }

        if (Params.PRINT_FLOWS) {
            List<Edge> keys = new ArrayList<>(graph.edgeSet());
            Collections.sort(keys);
            for (Edge e : keys) {
                output += Params.SEPARATOR + e.getName();
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
