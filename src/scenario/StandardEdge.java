package scenario;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rgrunitzki
 */
public class StandardEdge extends AbstractEdge{

    public StandardEdge(AbstractCostFunction costFunction) {
        super(costFunction);
    }

//    private Map<String, Object> params;
//
//    private int currentFlow;
//
//    private int totalFlow;
//
////    private double msaFlow;
//
//    public static boolean MSA = false;
//
//    private final AbstractCostFunction costFunction;
//
//    public DefaultEdge(AbstractCostFunction costFunction) {
//        this.params = new HashMap<>();
//        this.currentFlow = 0;
//        this.totalFlow = 0;
//        this.costFunction = costFunction;
////        this.msaFlow = 0.0;
//    }
//
//    public void setParams(Map<String, Object> params) {
//        this.params = params;
//    }
//
//    public Map<String, Object> getParams() {
//        return params;
//    }
//
//    public int getCurrentFlow() {
//        return currentFlow;
//    }
//
//    public void setCurrentFlow(int currentFlow) {
//        this.currentFlow = currentFlow;
//    }
//
//    public int getTotalFlow() {
//        return totalFlow;
//    }
//
//    public void setTotalFlow(int totalFlow) {
//        this.totalFlow = totalFlow;
//    }
//
//    @Override
//    protected double getWeight() {
////        if (Edge.MSA) {
////            return this.costFunction.evalDesirableCost(this, msaFlow);
////        } else {
//            return this.costFunction.evalCost(this);
////        }
//    }
//
//    public synchronized void clearCurrentFlow() {
//        this.currentFlow = 0;
//    }
//
//    public synchronized void reset() {
//        this.currentFlow = 0;
//        this.totalFlow = 0;
//    }
//
//    public synchronized void resetAll() {
//        this.reset();
//        totalFlow = 0;
//    }
//
//    public synchronized void incrementFlow() {
//        this.currentFlow++;
//        this.totalFlow++;
//    }
//
//    public String getName() {
//        return this.getSource().toString() + "|" + this.getTarget().toString();
//    }
//
//    public String getSourceVertex() {
//        return this.getSource().toString();
//    }
//
//    public String getTargetVertex() {
//        return this.getTarget().toString();
//    }
//
//    public synchronized double getCost() {
//        return this.getWeight();
//    }
//
//    public AbstractCostFunction getCostFunction() {
//        return costFunction;
//    }
//
////    public double getMsaFlow() {
////        return msaFlow;
////    }
////
////    public synchronized void setMsaFlow(double msaFlow) {
////        this.msaFlow = msaFlow;
////    }
//
//    @Override
//    public int compareTo(DefaultEdge o) {
//        return this.getName().compareTo(o.getName());
//    }
//
//    public synchronized void  incrementTotalFlow(int flow) {
//        this.totalFlow += flow;
//    }

}
