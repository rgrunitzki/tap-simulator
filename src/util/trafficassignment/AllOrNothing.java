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
 *
 * @author Ricardo Grunitzki
 */
public class AllOrNothing {

    public static void main(String[] args) {

        TAP tap = TAP.BRAESS(TADriver.class);
        
        List<String> odpairs = new ArrayList<>(tap.getOdpairs().keySet());
        Collections.sort(odpairs);
        String header = "average_tt";
        String results = "";

        //calculate all-shortest-path
        FloydWarshallShortestPaths fws = new FloydWarshallShortestPaths(tap.getGraph());

        //update the edges cost
        for (String odPair : odpairs) {
            String origin = odPair.split("-")[0];
            String destination = odPair.split("-")[1];

            GraphPath path = fws.getShortestPath(origin, destination);

            for (Object e : path.getEdgeList()) {
                StandardEdge edge = (StandardEdge) e;
                edge.incrementTotalFlow(tap.getOdpairs().get(odPair).demandSize());
            }
        }

        //evaluate cost per OD pair
        for (String odPair : odpairs) {
            header += Params.COLUMN_SEPARATOR + odPair;
            
            String origin = odPair.split("-")[0];
            String destination = odPair.split("-")[1];

            results += Params.COLUMN_SEPARATOR + fws.getShortestPath(origin, destination).getWeight();

        }

        //print Result
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
