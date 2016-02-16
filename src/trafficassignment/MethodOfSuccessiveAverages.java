/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficassignment;

import driver.Driver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
public class MethodOfSuccessiveAverages {

    public static void main(String[] args) {

        TAP tap = TAP.SF(MSADriver.class);
        List<String> odpairs = new ArrayList<>(tap.getOdpairs().keySet());
        Collections.sort(odpairs);
        List<Edge> edges = new ArrayList<>(tap.getGraph().edgeSet());
        Collections.sort(edges);
        Edge.MSA = true;

        String header = "iteration" + Params.SEPARATOR + "average_tt";
        String results = "";
        int iterations = 50;
        double phi;
        FloydWarshallShortestPaths fws;

        for (int iteration = 0; iteration < iterations; iteration++) {
            phi = 1.0 / (iteration + 1);
            //calculate all-shortest-path
            fws = new FloydWarshallShortestPaths(tap.getGraph());
            final FloydWarshallShortestPaths fws2 = fws;

            //Update edges flow
            odpairs.parallelStream().forEach((odPair) -> {
                //get the shortest path of the current OD pair
                GraphPath path = fws2.getShortestPath(odPair.split("-")[0], odPair.split("-")[1]);

                //Generate alterative flow F
                path.getEdgeList().parallelStream().forEach((edge) -> {
                    int flow = (tap.getOdpairs().get(odPair).demandSize());
                    ((Edge) edge).incrementTotalFlow(flow);
                });

                //update drivers' route
                tap.getOdpairs().get(odPair).getDrivers().parallelStream().forEach((driver) -> {
                    ((MSADriver) driver).setRoute(path.getEdgeList());
                });

            });

            final double phi2 = phi;
            edges.parallelStream().forEach((edge) -> {
                double flow = (1 - phi2) * edge.getMsaFlow() + phi2 * edge.getTotalFlow();
                edge.setMsaFlow(flow);
            });

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


            System.out.println(iteration + Params.SEPARATOR + cost / tap.getDrivers().size() + results);
            results = "";
        }
    }
}
