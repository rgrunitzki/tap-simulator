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
package scenario;

import driver.Driver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import scenario.demand.ODPair;
import scenario.network.AbstractCostFunction;
import scenario.network.AbstractEdge;
import scenario.network.ParsedCostFunction;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class NewCSVLoader extends AbstractLoader {

    @Override
    protected Pair<Graph, AbstractCostFunction> createSupply(File netFile, Class edgeClass, AbstractCostFunction costFunction) {
        //Output object
        Pair<Graph, AbstractCostFunction> supply;

        Map<String, String> constantsPerFunction = new HashMap<>();

        //Uses the default_edge case edgeClass is null
        if (edgeClass == null) {
            edgeClass = Params.DEFAULT_EDGE;
        }

        //Initialize the cost function case it is not
        if (costFunction == null) {
            costFunction = new ParsedCostFunction();
        }
        //Graph Object
        Graph<String, AbstractEdge> graph = new DefaultDirectedWeightedGraph<>(edgeClass);
        try {
            //Reading objects
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(netFile));
            StringBuilder sb = new StringBuilder();
            //current line
            String line;// = br.readLine();
            //last header
            String[] columns;

            while ((line = br.readLine()) != null) {
//                sb.append(line);
//                sb.append(System.lineSeparator());
//                line = br.readLine();
                columns = line.split(" ");
                //verify if its a header
                if (line.contains("#")) {
                } else {
                    switch (columns[0]) {
                        //creates funciton
                        case "function":

                            String constants;
                            if (!constantsPerFunction.containsKey(columns[1])) {
                                //Gets the arguments of the reward function
                                constants = columns[3];
                                constants = constants.replaceAll("[\\(\\)\\+\\-\\*\\/\\^0-9]", "");
                                constantsPerFunction.put(columns[1], constants);

                            } else {
                                constants = constantsPerFunction.get(columns[1]);
                            }
                            //Array of arguments
                            Argument[] arguments = new Argument[constants.length()];
                            //creates the array of argumets of the cost function
                            for (int i = 0; i < constants.length(); i++) {
                                arguments[i] = new Argument(constants.charAt(i) + "");
                            }
                            //craetes the expression string
                            String expressionString = line.substring(line.indexOf(")") + 2);
                            //craetes the expression object
                            Expression expression = new Expression(expressionString, arguments);
                            //name of the reward function
                            String key = columns[1];
                            //add the expression to the cost function
                            ((ParsedCostFunction) costFunction).addExpression(key, expression);
                            break;
                        //creates node
                        case "node":
                            //creates a node according to its name
                            graph.addVertex(columns[1]);
                            break;
                        //creates an edge
                        case "edge":
                            //Edge object
                            AbstractEdge edge = null;
                            //instantiate an outgoing edge
                            edge = (AbstractEdge) edgeClass.getConstructor(edgeClass.getConstructors()[0].getParameterTypes()).newInstance(
                                    costFunction);
                            //creates its set of parameters
                            Map<String, Object> params = new HashMap<>();
                            //paramter 'function' which indicates the reward function used
                            params.put("function", columns[4]);
                            String args = constantsPerFunction.get(columns[4]).replaceAll("[f]", "");
                            for (int j = 5; j < columns.length; j++) {
                                //parameters that composes the reward function
                                params.put("" + args.charAt(j - 5), columns[j]);
                            }
                            //update the edge's set of parameters
                            edge.setParams(params);
                            //insert the edge on graph
                            graph.addEdge(columns[2], columns[3], edge);

                            //instantiate an outgoing edge
                            AbstractEdge edge2 = (AbstractEdge) edgeClass.getConstructor(edgeClass.getConstructors()[0].getParameterTypes()).newInstance(
                                    costFunction);
                            edge2.setParams(params);
                            graph.addEdge(columns[3], columns[2], edge2);
                            break;

                        case "dedge":
                            //Edge object
                            AbstractEdge edg = null;
                            //instantiate an outgoing edge
                            edg = (AbstractEdge) edgeClass.getConstructor(edgeClass.getConstructors()[0].getParameterTypes()).newInstance(
                                    costFunction);
                            //creates its set of parameters
                            Map<String, Object> par = new HashMap<>();
                            //paramter 'function' which indicates the reward function used
                            par.put("function", columns[4]);
                            String arg = constantsPerFunction.get(columns[4]).replaceAll("[f]", "");
                            for (int j = 5; j < columns.length; j++) {
                                //parameters that composes the reward function
                                par.put("" + arg.charAt(j - 5), columns[j]);
                            }
                            //update the edge's set of parameters
                            edg.setParams(par);
                            //insert the edge on graph
                            graph.addEdge(columns[2], columns[3], edg);
                    }
                }
            }
            br.close();
        } catch (IOException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(NewCSVLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        supply = new MutablePair<>(graph, costFunction);
        return supply;
    }

    @Override
    protected Pair<List<Driver>, Map<String, ODPair>> createDemand(File demandFile, Class driverClass, Graph graph) {
        Pair<List<Driver>, Map<String, ODPair>> demand;
        List<Driver> drivers = new ArrayList<>();
        Map<String, ODPair> odpairs = new ConcurrentHashMap<>();

        if (driverClass == null) {
            driverClass = Params.DEFAULT_ALGORITHM;
        }

        int countD = 0;

        try {//reading variables
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(demandFile));
            String line;
            String[] columns;

            while ((line = br.readLine()) != null) {
                columns = line.split(" ");
                //verify if its a header
                if (columns[0].equalsIgnoreCase("od")) {
                    //create drivers for each OD pair
                    String origin = columns[2];
                    String destination = columns[3];

                    //create as many drivers as the number of trips defined
                    int size = (int) ((Integer.parseInt(columns[4])) / Params.PROPORTION);

                    ODPair od = new ODPair<>(origin + "-" + destination);

                    for (int d = size; d > 0; d--) {

                        Object driver = driverClass.getConstructor(driverClass.getConstructors()[0].getParameterTypes()).newInstance(
                                ++countD, origin, destination, graph);
                        drivers.add((Driver) driver);
                        od.addDriver((Driver) driver);
                    }
                    odpairs.put(od.getName(), od);
                }
            }
            br.close();

        } catch (IOException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(NewCSVLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        demand = new MutablePair<>(drivers, odpairs);
        return demand;

    }

}
