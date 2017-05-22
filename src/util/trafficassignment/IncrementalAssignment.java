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
public class IncrementalAssignment {

    public static void main(String[] args) {

        TAP tap = TAP.BRAESS(TADriver.class);
        List<String> odpairs = new ArrayList<>(tap.getOdpairs().keySet());
        Collections.sort(odpairs);
        String header = "average_tt";
        String results = "";

        double[] pn = {0.4, 0.3, 0.2, 0.1};
        
        FloydWarshallShortestPaths fws = null;
        for (int fraction = 0; fraction < pn.length; fraction++) {
            //calculate all-shortest-path
            fws = new FloydWarshallShortestPaths(tap.getGraph());
            
             for (String odPair : odpairs) {
                 String origin = odPair.split("-")[0];
                String destination = odPair.split("-")[1];

                GraphPath path = fws.getShortestPath(origin, destination);

                System.out.println(path.getEdgeList().toString()  + ": " + path.getWeight());
             }
            

            //update the edges cost
            for (String odPair : odpairs) {
                
                String origin = odPair.split("-")[0];
                String destination = odPair.split("-")[1];

                GraphPath path = fws.getShortestPath(origin, destination);
                
                for (Object e : path.getEdgeList()) {
                    StandardEdge edge = (StandardEdge) e;
                    int flow = (int) (tap.getOdpairs().get(odPair).demandSize() * pn[fraction]);
                    edge.incrementTotalFlow(flow);
                }

                //updateDriversRoute;
                int odSize = tap.getOdpairs().get(odPair).demandSize();
                int init = 0;
                int end = (int) (pn[fraction] * odSize - 1);
                if (fraction != 0) {
                    double reference = demandSize(pn, fraction - 1);
                    init = (int) (reference * odSize);
                    end = (int) (init + pn[fraction] * odSize) - 1;
                }

                for (int i = init; i <= end; i++) {
                    TADriver driver = (TADriver) tap.getOdpairs().get(odPair).getDrivers().get(i);
                    driver.setRoute(path.getEdgeList());
                }
//                System.out.println(init + "\t" + end);
            }

            //evaluate cost per OD pair
            for (String odPair : odpairs) {
                int odSize = tap.getOdpairs().get(odPair).demandSize();
                int init = 0;
                int end = (int) (pn[fraction] * odSize - 1);
                if (fraction != 0) {
                    double reference = demandSize(pn, fraction - 1);
                    init = (int) (reference * odSize);
                    end = (int) (init + pn[fraction] * odSize) - 1;
                }

                double odTravelTime = 0.0;
                double count = 0;
                for (int i = 0; i <= end; i++) {
                    TADriver driver = (TADriver) tap.getOdpairs().get(odPair).getDrivers().get(i);
                    odTravelTime += driver.getTravelTime();
                    count++;
                }
                header += Params.COLUMN_SEPARATOR + odPair;
                results += Params.COLUMN_SEPARATOR + odTravelTime / count;

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
            if (fraction == 0) {
                System.out.println(header);
            }
            System.out.println(cost + results);
            results = "";
        }
    }

    public static double demandSize(double[] demandFraction, int index) {
        if (index == 0) {
            return demandFraction[index];
        } else {
            return demandFraction[index] + demandSize(demandFraction, index - 1);
        }
    }

}
