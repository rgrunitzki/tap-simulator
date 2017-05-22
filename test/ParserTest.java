
import java.io.File;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import scenario.NewCSVLoader;
import scenario.TAP;
import scenario.network.AbstractCostFunction;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rgrunitzki
 */
public class ParserTest {

    public static void main(String[] args) {
        
         Queue<String> NEIGHBORHOODS_QUEUE = new PriorityBlockingQueue<>();
         NEIGHBORHOODS_QUEUE.add("A");
         NEIGHBORHOODS_QUEUE.add("A");
         System.out.println(NEIGHBORHOODS_QUEUE.add("A"));
         System.out.println(NEIGHBORHOODS_QUEUE);

        String netFile;
        String demandFile;
        AbstractCostFunction costFunction = null;
        netFile = "files/SF.net";
        demandFile = "files/SF.net";
//        costFunction = new LinearCostFunction();

        NewCSVLoader loader = new NewCSVLoader();
        try {
            TAP tap = loader.createTAP(new File(demandFile), new File(netFile), null, null, costFunction);

            System.out.println("sucesso");
        }catch(Exception e){
            System.err.println("falhou");
        }
    }
}
