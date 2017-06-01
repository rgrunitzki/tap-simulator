
import util.math.SimpleMovingAverage;
import java.util.Random;
import util.math.DynamicList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
 */
public class TestMovingAverage {

    public static void main(String[] args) {
        int period = 1;
        DynamicList dl = new DynamicList(period);
        for (int i = 0; i < 10; i++) {
            double value = new Random().nextInt(10);
            dl.add(value);
            System.out.print(i + ":\t" + value);
            System.out.println("\t\t" + dl.getElements());
        }
        System.out.println("");
        System.out.println(dl.getElements());
//        System.out.println("Last " + period + ":\t" + ma.getAverage());
    }

}
