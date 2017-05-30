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

        //other examples of TAP
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
