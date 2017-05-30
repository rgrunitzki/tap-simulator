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
package extensions.c2i;

import driver.Driver;
import static extensions.c2i.QLStatefullC2I.GAMMA;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import scenario.network.AbstractCostFunction;
import scenario.network.AbstractEdge;

/**
 *
 * @author Ricardo Grunitzki
 */
public class EdgeC2I extends AbstractEdge {

    public static FloydWarshallShortestPaths SHORTEST_PATHS;

    public static boolean C2I_WEIGHT = false;

    public static Map<String, Double> PATHS_WEIGHT;

    private final MessageC2I knowledgeC2I = new MessageC2I();

    public EdgeC2I(AbstractCostFunction costFunction) {
        super(costFunction);
    }

    public synchronized MessageC2I getInformation() {
        return knowledgeC2I;
    }

    private synchronized void updateInformation(int flow) {
        knowledgeC2I.addValue(flow);
    }

    @Override
    protected synchronized double getWeight() {
        if (C2I_WEIGHT) {
            if (this.getInformation() == null) {
                return this.getCostFunction().evalDesirableCost(this, 0);
            } else {
                return this.getCostFunction().evalDesirableCost(this, (this.getInformation().getValue()));
            }
        } else {
            return super.getWeight();
        }
    }

    @Override
    public synchronized void proccess(Driver driver) {
        //update flow counters
        super.proccess(driver);
        this.updateInformation(this.getTotalFlow());
    }

    public static synchronized void afterEpisode(Graph graph) {
        if (SHORTEST_PATHS != null) {
            SHORTEST_PATHS = null;
        }
    }

    public static synchronized void beforeEpisode(Graph graph) {
        if (SHORTEST_PATHS == null) {
            EdgeC2I.C2I_WEIGHT = true;
            SHORTEST_PATHS = new FloydWarshallShortestPaths<>(graph);
            PATHS_WEIGHT = new ConcurrentHashMap<>(SHORTEST_PATHS.getShortestPathsCount());
            for (Iterator it = SHORTEST_PATHS.getShortestPaths().iterator(); it.hasNext();) {
                GraphPath path = (GraphPath) it.next();
                PATHS_WEIGHT.put(path.getStartVertex() + "-" + path.getEndVertex(), bellmanValue(path.getEdgeList()));
            }
//            System.out.println("shortest path: 1-20: " + PATHS_WEIGHT.get("1-20"));
            EdgeC2I.C2I_WEIGHT = false;
        }
    }

    static double bellmanValue(List<AbstractEdge> path) {
        double value = -path.get(0).getCost();
        if (path.size() > 1) {
            for (int i = 1; i < path.size(); i++) {
                value -= path.get(i).getCost() * GAMMA;
            }
        }
        return value;
    }

    public static synchronized double getPath(String origin, String destination, Graph graph) {
        beforeEpisode(graph);
        if (origin.equals(destination)) {
            return 0d;
        } else {
            return PATHS_WEIGHT.get(origin + "-" + destination);
        }
    }
}
