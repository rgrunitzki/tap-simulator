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
import scenario.network.AbstractEdge;

/**
 * MDP of the drivers that use en-route mechanism.
 *
 * @author Ricardo Grunitzki
 */
public class StatefullMDP extends AbstractMDP<String, AbstractEdge, Double> {

    /**
     * Object used support the fast clone of StatefullMDP objects.
     */
    public static StatefullMDP staticMdp;

    @Override
    public void setValue(AbstractEdge action, Double value) {
        this.updateDetalQ(value - this.mdp.get(action.getSourceVertex()).get(action));

        this.mdp.get(action.getSourceVertex()).put(action, value);
    }

    @Override
    public double getValue(AbstractEdge key) {
        return this.mdp.get(key.getSourceVertex()).get(key);
    }

    @Override
    public void createMDP(List<AbstractEdge> actions) {
    }

    @Override
    public void reset() {
    }

}
