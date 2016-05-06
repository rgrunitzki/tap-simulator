package driver.learning;

import java.util.List;
import scenario.AbstractEdge;

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
