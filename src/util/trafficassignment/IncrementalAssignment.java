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
import scenario.network.StandardEdge;
import scenario.TAP;
import scenario.network.AbstractEdge;
import simulation.Params;

/**
 * Implements the Incremental Assignment method described in Chapter 10.5.3 of
 * Ortúzar and Willumsen (2011).
 *
 * Ortúzar, J. D. D., & Willumsen, L. G. (2011). Modelling Transport. Modelling
 * Transport. Chichester, UK: John Wiley & Sons, Ltd.
 * https://doi.org/10.1002/9781119993308
 *
 * @author Ricardo Grunitzki
 */
public class IncrementalAssignment {

    public static void main(String[] args) {

        //define a traffic assignment problem
        TAP tap = TAP.BRAESS(TADriver.class);//Braess paradox

        //other examples of TAP
        //TAP tap = TAP.OW(TADriver.class); //scenario presented in Exercise 10.1 of Ortúzar and Willumsen (2011)
        //TAP tap = TAP.SF(TADriver.class); //Sioux Falls scenario
        //List of origin-destination (OD) pairs
        List<String> odpairs = new ArrayList<>(tap.getOdpairs().keySet());
        //sort the list of OD-pairs
        Collections.sort(odpairs);
        String header = "average_tt";
        String results = "";

        //parameter of incremental assignment algorithm
        double[] pn = {0.4, 0.3, 0.2, 0.1};

        FloydWarshallShortestPaths fws = null;
        for (int fraction = 0; fraction < pn.length; fraction++) {
            //calculate all-shortest-path
            fws = new FloydWarshallShortestPaths(tap.getGraph());

//            for (String odPair : odpairs) {
//                String origin = odPair.split("-")[0];
//                String destination = odPair.split("-")[1];
//
//                GraphPath path = fws.getShortestPath(origin, destination);
//
//                System.out.println(path.getEdgeList().toString() + ": " + path.getWeight());
//            }
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

            //evaluate cost per OD-pair
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

            //print the obtained results
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

    private static double demandSize(double[] demandFraction, int index) {
        if (index == 0) {
            return demandFraction[index];
        } else {
            return demandFraction[index] + demandSize(demandFraction, index - 1);
        }
    }

}
