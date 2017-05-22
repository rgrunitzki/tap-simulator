/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.trafficassignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import scenario.network.AbstractEdge;
import scenario.TAP;
import simulation.Params;

/**
 *
 * @author Ricardo Grunitzki
 */
public class MethodOfSuccessiveAveragesAssignment {

    public static void main(String[] args) {

        Params.DEFAULT_EDGE = EdgeMSA.class;
        TAP tap = TAP.BRAESS(MSADriver.class);
        List<String> odpairs = new ArrayList<>(tap.getOdpairs().keySet());
        Collections.sort(odpairs);
        List<AbstractEdge> edges = new ArrayList<>(tap.getGraph().edgeSet());
        Collections.sort(edges);

        String header = "iteration" + Params.COLUMN_SEPARATOR + "average_tt";
        String results = "";
        int iterations = 1000;
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
                    EdgeMSA edge = (EdgeMSA) e;
                    int flow = (tap.getOdpairs().get(odPair).demandSize());
                    edge.incrementTotalFlow(flow);
                }

//                //update drivers' route
//                for (int i = 0; i < tap.getOdpairs().get(odPair).getDrivers().size(); i++) {
//                    MSADriver driver = (MSADriver) tap.getOdpairs().get(odPair).getDrivers().get(i);
//                    driver.setRoute(path.getEdgeList());
//                }
            }

            //Update current flow MSA
            for (AbstractEdge e : edges) {
                float flow = (float) ((1 - phi) * ((EdgeMSA) e).getMsaFlow() + phi * ((EdgeMSA) e).getTotalFlow());
                ((EdgeMSA) e).setMsaFlow(flow);
            }

            //Print Result per iteration
            //Evaluate cost per OD pair
//            for (String odPair : odpairs) {
//                header += Params.COLUMN_SEPARATOR + odPair;
//                results += Params.COLUMN_SEPARATOR + tap.getOdpairs().get(odPair).getAverageCost();
//            }

            //Get links' flow
            double cost = 0.0;

            for (AbstractEdge e : edges) {
                header += Params.COLUMN_SEPARATOR + e.getName();
                //Link Flow
                results += Params.COLUMN_SEPARATOR + ((EdgeMSA) e).getMsaFlow();
                //Evaluate average travel time
                cost += (e.getCostFunction().evalDesirableCost(e, ((EdgeMSA) e).getMsaFlow())) * ((EdgeMSA) e).getMsaFlow();

                e.setTotalFlow(0);
            }

//            double cost2 = 0;
//            for (Driver d : USED_TAP.getDrivers()) {
//                cost2 += d.getTravelTime();
//            }
//
//            System.out.println((cost / USED_TAP.getDrivers().size()) + " " + (cost2 / USED_TAP.getDrivers().size()));
            if (iteration == 0) {
                System.out.println(header);
            }
            System.out.println(iteration + Params.COLUMN_SEPARATOR + cost / tap.getDrivers().size() + results);
            results = "";
        }
    }
}
