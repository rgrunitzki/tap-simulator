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
package scenario.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mariuszgromada.math.mxparser.Expression;

/**
 *
 * @author Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
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
