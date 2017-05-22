package scenario;

import scenario.demand.ODPair;
import scenario.network.AbstractEdge;
import scenario.network.AbstractCostFunction;
import driver.Driver;
import extensions.hierarchical.QLStatefullHierarchical;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import simulation.Params;

/**
 * Creates the traffic assignment problem (TAP) according the specifications in
 * XML files.
 *
 * @author Ricardo Grunitzki
 */
public class XMLLoader {

    /**
     * Collection of OD-pairs of the problem.
     */
    public static Map<String, ODPair> odpairs = new ConcurrentHashMap<>();

    /**
     * Creates the Graph object that represents the road network of the TAP.
     *
     * @param netFile .net.xml file with the definitions of the road network
     * @param edgeClass the type of the edge.
     * @param costFunction the cost function of the edge.
     * @return Graph object
     * @throws NoSuchMethodException
     */
    public static Graph loadNetwork(File netFile, Class edgeClass, AbstractCostFunction costFunction) throws NoSuchMethodException {

        Graph<String, AbstractEdge> graph = new DefaultDirectedWeightedGraph<>(Params.DEFAULT_EDGE);

        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(netFile);

            //Create vertices
            NodeList list = doc.getElementsByTagName("node");

            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);

                graph.addVertex(e.getAttribute("id"));
            }

            //Create edges
            list = doc.getElementsByTagName("edge");

            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);

                AbstractEdge edge = null;
                try {
                    edge = (AbstractEdge) edgeClass.getConstructor(edgeClass.getConstructors()[0].getParameterTypes()).newInstance(
                            costFunction);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(XMLLoader.class.getName()).log(Level.SEVERE, null, ex);
                }

                Map<String, Object> params = new HashMap<>();

                for (int j = 0; j < e.getAttributes().getLength(); j++) {
                    if (!e.getAttributes().item(j).getNodeName().equalsIgnoreCase("from") && !e.getAttributes().item(j).getNodeName().equalsIgnoreCase("to")) {
                        params.put(e.getAttributes().item(j).getNodeName(), e.getAttributes().item(j).getNodeValue());
                    }
                }
                edge.setParams(params);
                graph.addEdge(e.getAttribute("from"), e.getAttribute("to"), edge);
            }

            //create terminal nodes
            list = doc.getElementsByTagName("terminalNodes");

            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                QLStatefullHierarchical.TERMINAL_VERTICES_COPY = Collections.synchronizedSet(new HashSet<String>(Arrays.asList(e.getAttribute("value").split(" "))));
            }

        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException e) {
            System.err.println("Error on reading network XML file!");
        }

        return graph;

    }

    /**
     * Creates the demand of the TAP.
     *
     * @param <DriverType> The type of the drivers
     * @param graph Network graph
     * @param demandFile File containing the demand definitions
     * @param driverClass The class of the drivers.
     * @return
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static <DriverType> List<DriverType> processODMatrix(Graph graph, File demandFile, Class driverClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<DriverType> drivers = new ArrayList<>();
        try {

            //objects to manipulate the XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(demandFile);

            //get the OD pairs
            NodeList list = doc.getElementsByTagName("od");

            //driver's count
            int countD = 0;

            //create drivers for each OD pair
            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                String origin = (e.getAttribute("origin"));
                String destination = (e.getAttribute("destination"));

                //create as many drivers as the number of trips defined
                int size = (int) ((Integer.parseInt(e.getAttribute("trips"))) / Params.PROPORTION);

                ODPair od = new ODPair<>(origin + "-" + destination);

                for (int d = size; d > 0; d--) {

                    Object driver = driverClass.getConstructor(driverClass.getConstructors()[0].getParameterTypes()).newInstance(
                            ++countD, origin, destination, graph);
                    drivers.add((DriverType) driver);
                    od.addDriver((Driver) driver);
                }
                odpairs.put(od.getName(), od);
            }

        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException e) {
            System.err.println("Error on reading demand XML file!");
        }
        return drivers;
    }
}
