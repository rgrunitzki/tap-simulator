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
 * Implements the Method of successive averages described in Chapter 10.5.4 of
 * Ortúzar and Willumsen (2011).
 *
 * Ortúzar, J. D. D., & Willumsen, L. G. (2011). Modelling Transport. Modelling
 * Transport. Chichester, UK: John Wiley & Sons, Ltd.
 * https://doi.org/10.1002/9781119993308
 *
 * @author Ricardo Grunitzki
 */
public class MethodOfSuccessiveAveragesAssignment {

    public static void main(String[] args) {

        Params.DEFAULT_EDGE = EdgeMSA.class;

        //define a traffic assignment problem
        TAP tap = TAP.TWO_NEIGHBORHOOD_REPLICATED(TADriver.class);//Braess paradox

        //other examples of TAP
        //TAP tap = TAP.OW(TADriver.class); //scenario presented in Exercise 10.1 of Ortúzar and Willumsen (2011)
        //TAP tap = TAP.SF(TADriver.class); //Sioux Falls scenario
        //List of origin-destination (OD) pairs
        List<String> odpairs = new ArrayList<>(tap.getOdpairs().keySet());
        //sort the list of OD-pairs
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
