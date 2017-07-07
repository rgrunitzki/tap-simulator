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
package driver.learning.mdp;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.jgrapht.GraphPath;

/**
 * MDP of the drivers that use a set of precomputed routes.
 *
 * @author Ricardo Grunitzki
 */
public class StatelessMDP extends AbstractMDP<String, GraphPath, Double> {

    @Override
    public void setValue(GraphPath action, Double value) {
        mdp.get((String) action.getStartVertex()).put(action, value);
    }

    @Override
    public double getValue(GraphPath actionKey) {
        return mdp.get((String) actionKey.getStartVertex()).get(actionKey);
    }

    @Override
    public void createMDP(List<GraphPath> actions) {
        for (GraphPath path : actions) {
            if (mdp.get((String) path.getStartVertex()) == null) {
                mdp.put((String) path.getStartVertex(), new ConcurrentHashMap<>());
            }
            mdp.get((String) path.getStartVertex()).put(path, 0.0);
        }
    }

    @Override
    public void reset() {
        if (!mdp.isEmpty()) {
            mdp.get(mdp.keySet().iterator().next()).entrySet().parallelStream().forEach((entrySet) -> {
                entrySet.setValue(0.0);
            });
        }
    }
}
