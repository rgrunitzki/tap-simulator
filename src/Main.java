
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

        Float[] gammas = {0.99f};
        Float[] alphas = {0.3f, 0.5f, 0.7f, 0.9f};
        String network = "OW";
        String rewards[] = {"STD"};

        for (String reward : rewards) {
            for (int g = 0; g < gammas.length; g++) {
                for (int a = 0; a < alphas.length; a++) {
                    System.out.format("java -XX:+UseParallelGC -Xmx8000m -cp tap-simulator.jar experiments.CommandLineExperiment "
                            + "-n %s "
                            + "-a QLStatefullC2I "
                            + "-info.type Last"
                            + "-alpha %.2f "
                            + "-gamma %.2f "
                            + "-reward %s "
                            + "-E EGreedy "
                            + "-epsilon 0.5 "
                            + "-edecay 0.99 "
                            + "-e 1000 "
                            + "-r 30 "
                            + "-d ~/experimentsATT/results "
                            + "-f "
                            + "-l "
                            + "-o\n", network, alphas[a], gammas[g], reward);
                }
            }
        }

        String[] arg = {"-h"};
        CommandLineExperiment.main(arg);
    }
}
