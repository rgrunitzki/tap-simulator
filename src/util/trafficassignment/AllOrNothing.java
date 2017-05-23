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
import scenario.network.StandardEdge;
import scenario.TAP;
import scenario.network.AbstractEdge;
import simulation.Params;

/**
 * Implements the All-or-Nothing algorithm described in Chapter 10.3 of Ortúzar
 * and Willumsen (2011).
 *
 * Ortúzar, J. D. D., & Willumsen, L. G. (2011). Modelling Transport. Modelling
 * Transport. Chichester, UK: John Wiley & Sons, Ltd.
 * https://doi.org/10.1002/9781119993308
 *
 * @author Ricardo Grunitzki
 */
public class AllOrNothing {

    public static void main(String[] args) {

        //define a traffic assignment problem
        TAP tap = TAP.BRAESS(TADriver.class);//Braess paradox

        //another examples of TAP
        //TAP tap = TAP.OW(TADriver.class); //scenario presented in Exercise 10.1 of Ortúzar and Willumsen (2011)
        //TAP tap = TAP.SF(TADriver.class); //Sioux Falls scenario
        //List of origin-destination (OD) pairs
        List<String> odpairs = new ArrayList<>(tap.getOdpairs().keySet());
        //sort the list of OD-pairs
        Collections.sort(odpairs);

        String header = "average_tt";
        String results = "";

        //calculate all-shortest-paths
        FloydWarshallShortestPaths fws = new FloydWarshallShortestPaths(tap.getGraph());

        //update edges cost
        for (String odPair : odpairs) {
            //origin node
            String origin = odPair.split("-")[0];
            //destination node
            String destination = odPair.split("-")[1];
            //shortest path for the OD-pair
            GraphPath path = fws.getShortestPath(origin, destination);

            //increment the flow of the edges of the shortest path
            for (Object e : path.getEdgeList()) {
                StandardEdge edge = (StandardEdge) e;
                edge.incrementTotalFlow(tap.getOdpairs().get(odPair).demandSize());
            }
        }

        //evaluate cost per OD-pair
        for (String odPair : odpairs) {
            header += Params.COLUMN_SEPARATOR + odPair;
            String origin = odPair.split("-")[0];
            String destination = odPair.split("-")[1];
            results += Params.COLUMN_SEPARATOR + fws.getShortestPath(origin, destination).getWeight();

        }

        //print obtained results
        List<AbstractEdge> edges = new ArrayList<>(tap.getGraph().edgeSet());
        Collections.sort(edges);
        double cost = 0.0;

        for (AbstractEdge e : edges) {
            header += Params.COLUMN_SEPARATOR + e.getName();
            results += Params.COLUMN_SEPARATOR + e.getTotalFlow();
            cost += (e.getCost() * e.getTotalFlow()) / tap.getDrivers().size();
        }
        System.out.println(header);
        System.out.println(cost + results);

    }

}
