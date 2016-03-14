
import experiments.CommandLineExperiment;
import org.apache.commons.cli.ParseException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rgrunitzki
 */
public class Main {

    public static void main(String[] args) throws ParseException {

        Integer[] routes = {2, 4, 6, 8, 10};
        Float[] alphas = {0.3f, 0.5f, 0.7f, 0.9f};
        String network = "OW";
        String rewards[] = {"STD", "DR"};

        for (String reward : rewards) {
            for (int k = 0; k < routes.length; k++) {
                for (int a = 0; a < alphas.length; a++) {
                    System.out.format("java -XX:+UseParallelGC -Xmx8000m -cp tap-simulator.jar experiments.CommandLineExperiment "
                            + "-n %s "
                            + "-a QLStateless "
                            + "-alpha %.2f "
                            + "-k %d "
                            + "-reward %s "
                            + "-E EGreedy "
                            + "-epsilon 0.99 "
                            + "-e 1000 "
                            + "-r 30 "
                            + "-d ~/experimentsATT/%s "
                            + "-f "
                            + "-l "
                            + "-o\n", network, alphas[a], routes[k], reward, reward);
                }
            }
        }

        String[] arg = {"-h"};
        CommandLineExperiment.main(arg);
    }
}
