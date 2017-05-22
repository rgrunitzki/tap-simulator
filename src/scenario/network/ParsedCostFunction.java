/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scenario.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mariuszgromada.math.mxparser.Expression;

/**
 *
 * @author rgrunitzki
 */
public class ParsedCostFunction extends AbstractCostFunction {

    /**
     *
     */
    private Map<String, Expression> expressions = new ConcurrentHashMap<>();

    public ParsedCostFunction(Map<String, Expression> functionsDefinition) {
        this.expressions = functionsDefinition;
    }

    public ParsedCostFunction() {

    }

    @Override
    public double evalCost(AbstractEdge edge) {
        String costFunction = (String) edge.getParams().get("function");
        for (int i = 0; i < this.expressions.get(costFunction).getArgumentsNumber(); i++) {
            String argumentName = this.expressions.get(costFunction).getArgument(i).getArgumentName();
            Double argumentValue;
            if (argumentName.equalsIgnoreCase("f")) {
                argumentValue = (double) edge.getTotalFlow();
            } else {
                argumentValue = Double.valueOf((String) edge.getParams().get(argumentName));
            }
            this.expressions.get(costFunction).setArgumentValue(argumentName, argumentValue);
        }
//        if (edge.getParams().containsKey("f")) {
//            this.expressions.get(costFunction).setArgumentValue("f", edge.getTotalFlow());
//        }
        return this.expressions.get(costFunction).calculate();
    }

    @Override
    public double evalDesirableCost(AbstractEdge edge, double desirableFlow) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addExpression(String key, Expression expression) {
        this.expressions.put(key, expression);
    }

}
