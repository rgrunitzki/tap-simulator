
import driver.learning.QLStateless;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import scenario.TAP;
import simulation.Simulation;
 
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

    public static void main(String args[]) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        long time = System.currentTimeMillis();

        
        TAP tap = TAP.SF(QLStateless.class);
        Simulation simulation = new Simulation(tap);
        
        simulation.execute();

//        print the simulation time
        System.out.println("Simulation time...\n\t " + (new SimpleDateFormat("mm:ss:SSS")).format(new Date(System.currentTimeMillis() - time)));
    }

}
