/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficassignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.alg.KShortestPaths;
import scenario.Edge;
import scenario.TAP;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class ImprovedAllOrNothingAssignment {

    public static void main(String[] args) {

        TAP tap = TAP.BRAESS(CustomTADriver.class);
        List<String> odpairs = new ArrayList<>(tap.getOdpairs().keySet());
        Collections.sort(odpairs);
        String header = "average_tt";
        String results = "";

        int numberShortestPath = 5;
        Map<String, List<List<Edge>>> ksps = new HashMap<>();
        

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
            
            if(paths.size()>1){
                System.out.format("%s-%s:\t%s\n", paths.get(0).getStartVertex(), paths.get(0).getEndVertex(), paths.size());
//                System.out.println(paths.toString());
            }
            
            //transforms GraphPath in List<Edge>
            List<List<Edge>> edges = new ArrayList<>();
            for (GraphPath path : paths) {
                List<Edge> eds = new ArrayList<>();
                for (Object e: path.getEdgeList()){
                    eds.add((Edge) tap.getGraph().getEdge(((Edge) e).getSourceVertex(), ((Edge) e).getTargetVertex()));
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
            
            List<List<Edge>> paths = ksps.get(origin+"-"+destination);

            //update edges flow
            for (List<Edge> path : paths) {
                for (Edge e : path) {
                    Edge edge = (Edge) e;
                    edge.incrementTotalFlow(tap.getOdpairs().get(odPair).demandSize() / paths.size());
                }
            }
        }
        
        
        
        

        //evaluate cost per OD pair
        for (String odPair : odpairs) {
            header += Params.SEPARATOR + odPair;

            String origin = odPair.split("-")[0];
            String destination = odPair.split("-")[1];

            double average_tt = 0;
            for (List<Edge> path : ksps.get(origin + "-" + destination)) {
                for (Edge e : path) {
                    average_tt += e.getCost();
                }
            }
            average_tt/=ksps.get(origin+"-"+destination).size();

            results += Params.SEPARATOR + average_tt;

        }

        //print Result
        List<Edge> edges = new ArrayList<>(tap.getGraph().edgeSet());
        Collections.sort(edges);
        double cost = 0.0;

        for (Edge e : edges) {
            header += Params.SEPARATOR + e.getName();
            results += Params.SEPARATOR + e.getTotalFlow();
            cost += (e.getCost() * e.getTotalFlow()) / tap.getDrivers().size();
        }
        System.out.println(header);
        System.out.println(cost + results);

    }

}
