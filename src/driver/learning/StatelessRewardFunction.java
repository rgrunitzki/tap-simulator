/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import scenario.Edge;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class StatelessRewardFunction extends AbstractRewardFunction<QLStateless> {

    @Override
    public Double getStandardReward(QLStateless driver) {
        Double cost = 0.0;
        for (Object edge : driver.getRoute().getEdgeList()) {
            Edge e = (Edge) edge;
            cost -= e.getCostFunction().evalCost(e);
        }
        return cost;
    }

    @Override
    public Double getRewardShaping(QLStateless driver) {
        Double cost = 0.0;
        for (Object edge : driver.getRoute().getEdgeList()) {
            Edge e = (Edge) edge;
            if (e.getTotalFlow() > 0) {
                cost -= e.getCost() * e.getTotalFlow();
            } else {
                cost -= e.getCost();
            }
        }

        return cost;
    }

    @Override
    public Double getDifferenceRewards(QLStateless driver
    ) {
        double soma_gz = 0;
        double gz_zi = 0;
        for (Object edge : driver.getRoute().getGraph().edgeSet()) {
            Edge e = (Edge) edge;

            if (e.getTotalFlow() > 0) {
                soma_gz -= e.getCost() * e.getTotalFlow();
            } else {
                soma_gz -= e.getCost();
            }

            if (driver.getRoute().getEdgeList().contains(e)) {
                if (e.getTotalFlow() > 0) {
                    gz_zi -= e.getCostFunction().evalDesirableCost(e, e.getTotalFlow() - 1) * (e.getTotalFlow() - 1);
                } else {
                    gz_zi -= e.getCostFunction().evalDesirableCost(e, e.getTotalFlow() - 1);
                }
            } else {
                if (e.getTotalFlow() > 0) {
                    gz_zi -= e.getCost() * e.getTotalFlow();
                } else {
                    gz_zi -= e.getCost();
                }
            }

        }
        return (soma_gz - gz_zi) / Params.USED_TAP.getDrivers().size();
    }
}
