package scenario;

import driver.Driver;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ricardo Grunitzki
 */
public class Loader {

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

//                AbstractEdge edge = new AbstractEdge(costFunction);
                AbstractEdge edge = null;
                try {
                    edge = (AbstractEdge) edgeClass.getConstructor(edgeClass.getConstructors()[0].getParameterTypes()).newInstance(
                            costFunction);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
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

        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException e) {
            System.err.println("Error on reading XML file!");
        }

        return graph;

    }

    public static Map<String, ODPair> odpairs = new ConcurrentHashMap<>();

    public static <T> List<T> processODMatrix(Graph graph, File demandFile, Class driverClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<T> drivers = new ArrayList<>();
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
                    drivers.add((T) driver);
                    od.addDriver((Driver) driver);
                }
                odpairs.put(od.getName(), od);
            }

        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException e) {
            System.err.println("Error on reading XML file!");
        }
//        printOD(); 
        return drivers;
    }
}
