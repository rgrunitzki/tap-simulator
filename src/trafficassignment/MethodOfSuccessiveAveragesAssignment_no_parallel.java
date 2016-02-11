/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficassignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import scenario.Edge;
import scenario.TAP;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class MethodOfSuccessiveAveragesAssignment_no_parallel {

    public static void main(String[] args) {

        TAP tap = TAP.OW(MSADriver.class);
        List<String> odpairs = new ArrayList<>(tap.getOdpairs().keySet());
        Collections.sort(odpairs);
        List<Edge> edges = new ArrayList<>(tap.getGraph().edgeSet());
        Collections.sort(edges);
        Edge.MSA = true;

        String header = "iteration" + Params.SEPARATOR + "average_tt";
        String results = "";
        int iterations = 1;
        double phi;
        FloydWarshallShortestPaths fws;

        for (int iteration = 0; iteration < iterations; iteration++) {
            phi = 1.0 / (iteration + 1);
            //calculate all-shortest-path
            fws = new FloydWarshallShortestPaths(tap.getGraph());            

            //Update edges flow
            for (String odPair : odpairs) {

                //get the shortest path of the current OD pair
                GraphPath path = fws.getShortestPath(odPair.split("-")[0], odPair.split("-")[1]);

                //Generate alterative flow F
                for (Object e : path.getEdgeList()) {
                    Edge edge = (Edge) e;
                    int flow = (tap.getOdpairs().get(odPair).demandSize());
                    edge.incrementTotalFlow(flow);
                }

                //update drivers' route
                for (int i = 0; i < tap.getOdpairs().get(odPair).getDrivers().size(); i++) {
                    MSADriver driver = (MSADriver) tap.getOdpairs().get(odPair).getDrivers().get(i);
                    driver.setRoute(path.getEdgeList());
                }
            }

            //Update current flow MSA
            for (Edge e : edges) {
                double flow = (1 - phi) * e.getMsaFlow() + phi * e.getTotalFlow();
                e.setMsaFlow(flow);
            }

            //Print Result per iteration
            //Evaluate cost per OD pair
            for (String odPair : odpairs) {
                header += Params.SEPARATOR + odPair;
                results += Params.SEPARATOR + tap.getOdpairs().get(odPair).getAverageCost();
            }

            //Get links' flow
            double cost = 0.0;

            for (Edge e : edges) {
                header += Params.SEPARATOR + e.getName();
                //Link Flow
                results += Params.SEPARATOR + e.getMsaFlow();
                //Evaluate average travel time
                cost += (e.getCostFunction().evalDesirableCost(e, e.getMsaFlow())) * e.getMsaFlow();

                e.setTotalFlow(0);
            }

//            double cost2 = 0;
//            for (Driver d : tap.getDrivers()) {
//                cost2 += d.getTravelTime();
//            }
//
//            System.out.println((cost / tap.getDrivers().size()) + " " + (cost2 / tap.getDrivers().size()));
//            if (iteration == 0) {
//                System.out.println(header);
//            }
            System.out.println(iteration + Params.SEPARATOR + cost / tap.getDrivers().size() + results);
            results = "";
        }
    }
}
