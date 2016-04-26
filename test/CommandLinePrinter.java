
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ricardo Grunitzki
 */
public class CommandLinePrinter {
    
    public static void main(String[] args) {
        float alphas[]={0.3f, 0.5f, 0.7f, 0.9f};
        float gammas[]={0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 0.99f};
        int cont = 1;
        for(float alpha: alphas){
            for (float gamma:gammas){
                System.out.format("java -Xms5000m -XX:+UseParallelGC -cp tap-simulator.jar experiments.CommandLineExperiment "
                        + "-a QLStatefull "
                        + "-reward DR "
                        + "-e 1000 "
                        + "-epsilon 0.99 "
                        + "-alpha %.2f "
                        + "-gamma %.2f "
                        + "-n OW "
                        + "-r 30 "
                        + "-d ~/experiments_wcci_ext "
                        + "-l "
                        + "-o "
                        + "-f\n", alpha, gamma);
            }
        }
    }
    
}
