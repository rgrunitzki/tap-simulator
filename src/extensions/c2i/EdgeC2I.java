/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extensions.c2i;

import driver.Driver;
import driver.learning.QLStatefullC2I;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import scenario.AbstractCostFunction;
import scenario.AbstractEdge;

/**
 *
 * @author rgrunitzki
 */
public class EdgeC2I extends AbstractEdge {

    private final Map<AbstractEdge, MessageC2I> knowledgeBase = new ConcurrentHashMap<>();

    public EdgeC2I(AbstractCostFunction costFunction) {
        super(costFunction);
    }

    public MessageC2I getInformation(AbstractEdge key) {
        return knowledgeBase.get(key);
    }

    private void updateInformation(AbstractEdge key, Double value) {

        if (!knowledgeBase.containsKey(key)) {
            knowledgeBase.put(key, new MessageC2I());
        }

        knowledgeBase.get(key).addValue(value);
    }

    @Override
    public synchronized void proccess(Driver driver) {
        //update flow counters
        super.proccess(driver);

        if (QLStatefullC2I.INFORMATION_TYPE != InformationType.None) {

            //Runs through the knowledge base of the agent and updates Edge's knowledgeBase
            QLStatefullC2I d = (QLStatefullC2I) driver;

            //Runs through the states
            for (String state : d.getMdp().getMdp().keySet()) {
                //Runs through the action-state pairs
                for (AbstractEdge action : d.getMdp().getMdp().get(state).keySet()) {
                    //get the estimated reward
                    double value = d.getMdp().getMdp().get(state).get(action).getReward();
                    
                    //update the knownledge base
                    if (value != 0.0) {
                        this.updateInformation(action, value);
                    }
                }
            }
        }

        //
//        String output = this.getName() + " -> ";
//        for (AbstractEdge e : knowledgeBase.keySet()) {
//            output+="("+ e.getName() + "-" + knowledgeBase.get(e).getValue()+")";
//        }
//        System.out.println(output);
    }

}
