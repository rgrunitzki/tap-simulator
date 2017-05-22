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
