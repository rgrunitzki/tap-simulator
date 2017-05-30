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
import org.apache.commons.cli.ParseException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ricardo Grunitzki
 */
public class Main {

    public static void main(String[] args) throws ParseException {

//        Float[] epsilonInitial = {0.25f, 0.5f, 0.75f, 1f};
//        Float[] communicationRate = {0.0f, 0.25f, 0.5f, 0.75f, 1f};
//        String network = "OW";
//        String rewards[] = {"STD"};
//
//            for (int ep = 0; ep < epsilonInitial.length; ep++) {
//                for (int cr = 0; cr < communicationRate.length; cr++) {
//                    System.out.format("java -XX:+UseParallelGC -Xmx8000m -cp tap-simulator.jar experiments.CommandLineExperiment "
//                            + "-n SF "
//                            + "-a QLStatefullC2I "
//                            + "-info.type Last "
//                            + "-alpha 0.90 "
//                            + "-gamma 0.99 "
//                            + "-p 100 "
//                            + "-c2irate %.2f "
//                            + "-epsilon %.2f "
//                            + "-edecay 0.99 "
//                            + "-e 1000 "
//                            + "-r 10 "
//                            + "-d ~/experimentsATT/results "
//                            + "-f -l -o\n", communicationRate[cr], epsilonInitial[ep]);
//                }
//            }
//            String s = "java -XX:+UseParallelGC -Xmx8000m -cp tap-simulator.jar experiments.CommandLineExperiment -n OW -a QLStatefullC2I -info.type Last -alpha 0.90 -gamma 0.99 -p 100 -c2irate %.2f -epsilon %.2f "
//                    + "-edecay 0.99 -e 1000 -r 10 -d ~/experimentsATT/results -f -l -o";
        String[] arg = {"-h"};
        CommandLineExperiment.main(arg);
    }
}
