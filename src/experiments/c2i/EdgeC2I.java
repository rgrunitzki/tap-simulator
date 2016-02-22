/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package experiments.c2i;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import scenario.AbstractCostFunction;
import scenario.AbstractEdge;

/**
 *
 * @author rgrunitzki
 */
public class EdgeC2I extends AbstractEdge {

    private Map<AbstractEdge, AditionalData> knowledgeBase = new ConcurrentHashMap<>();

    public EdgeC2I(AbstractCostFunction costFunction) {
        super(costFunction);
    }

    public AditionalData getInformation(AbstractEdge key) {
        return knowledgeBase.get(key);
    }

    public void updateInformation(AbstractEdge key, AditionalData data) {
        knowledgeBase.put(key, data);
    }

}
