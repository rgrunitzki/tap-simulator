
import java.util.PriorityQueue;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ricardo Grunitzki <rgrunitzki@inf.ufrgs.br>
 */
public class PriorityQueueTest {

    public static void main(String[] args) {
        PriorityQueue<String> queue = new PriorityQueue();
        queue.add("A");
        queue.add("a");
        queue.add("j");
        queue.add("i");
        queue.add("H");
        queue.add("1");

        String s = queue.poll();
        while (s != null) {
            System.out.println("s: " + s);
            s = queue.poll();
        }
    }

}
