package scenario;

/**
 * The standard structure for Edges in the simulator.
 *
 * @author Ricardo Grunitzki
 */
public class StandardEdge extends AbstractEdge {

    /**
     * Creates an instance of the Standard Edge.
     *
     * @param costFunction
     */
    public StandardEdge(AbstractCostFunction costFunction) {
        super(costFunction);
    }

}
