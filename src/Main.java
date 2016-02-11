
import driver.learning.QLStateless;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import scenario.TAP;

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

//
        
        System.out.println(File.pathSeparator+File.separator);
        
        long time = System.currentTimeMillis();
//
        TAP tap = TAP.OW(QLStateless.class);
        System.out.println(tap.getNetworkName());
//      Simulation simulation = new Simulation(tap);
//
//      simulation.execute();
//       
//
//      print the simulation time
//      System.out.println("Simulation time...\n\t " + (new SimpleDateFormat("mm:ss:SSS")).format(new Date(System.currentTimeMillis() - time)));
    }
}
