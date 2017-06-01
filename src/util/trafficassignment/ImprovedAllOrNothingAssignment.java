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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import scenario.network.StandardEdge;
import scenario.TAP;
import scenario.network.AbstractEdge;
import simulation.Params;

/**
 * Implementation of the improved version of all-or-nothing algorithm. This
 * method was proposed by Ana L. C. Bazzan.
 *
 * @author Ricardo Grunitzki
 */
public class ImprovedAllOrNothingAssignment {

    public static void main(String[] args) {

        TAP tap = TAP.BRAESS_BAZZAN(TADriver.class);
        List<String> odpairs = new ArrayList<>(tap.getOdpairs().keySet());
        Collections.sort(odpairs);
        String header = "average_tt";
        String results = "";

        int numberShortestPath = 5;
        Map<String, List<List<StandardEdge>>> ksps = new HashMap<>();

        //calculate paths
        for (String odPair : odpairs) {
            String origin = odPair.split("-")[0];
            String destination = odPair.split("-")[1];

            //calculate k shortest paths
            KShortestPaths ksp = new KShortestPaths(tap.getGraph(), origin, numberShortestPath);
            //weight of the shortest path
            double spWeight = ((GraphPath) ksp.getPaths(destination).get(0)).getWeight();
            //k shortest paths
            List<GraphPath> paths = ksp.getPaths(destination);
            //remove the paths of cost greater than spWeight
            paths.removeIf(p -> p.getWeight() != spWeight);

            if (paths.size() > 1) {
                System.out.format("%s-%s:\t%s\n", paths.get(0).getStartVertex(), paths.get(0).getEndVertex(), paths.size());
//                System.out.println(paths.toString());
            }

            //transforms GraphPath in List<Edge>
            List<List<StandardEdge>> edges = new ArrayList<>();
            for (GraphPath path : paths) {
                List<StandardEdge> eds = new ArrayList<>();
                for (Object e : path.getEdgeList()) {
                    eds.add((StandardEdge) tap.getGraph().getEdge(((StandardEdge) e).getSourceVertex(), ((StandardEdge) e).getTargetVertex()));
                }
                edges.add(eds);
            }

            //store the shortest paths
            ksps.put(origin + "-" + destination, edges);
        }

        //allocate flows
        for (String odPair : odpairs) {

            String origin = odPair.split("-")[0];
            String destination = odPair.split("-")[1];

            List<List<StandardEdge>> paths = ksps.get(origin + "-" + destination);

            //update edges flow
            for (List<StandardEdge> path : paths) {
                for (StandardEdge e : path) {
                    StandardEdge edge = (StandardEdge) e;
                    edge.incrementTotalFlow(tap.getOdpairs().get(odPair).demandSize() / paths.size());
                }
            }
        }

        //evaluate cost per OD pair
        for (String odPair : odpairs) {
            header += Params.COLUMN_SEPARATOR + odPair;

            String origin = odPair.split("-")[0];
            String destination = odPair.split("-")[1];

            double average_tt = 0;
            for (List<StandardEdge> path : ksps.get(origin + "-" + destination)) {
                for (StandardEdge e : path) {
                    average_tt += e.getCost();
                }
            }
            average_tt /= ksps.get(origin + "-" + destination).size();

            results += Params.COLUMN_SEPARATOR + average_tt;

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
