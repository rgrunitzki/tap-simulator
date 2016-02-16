
import org.apache.commons.cli.ParseException;
import simulation.Params;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rgrunitzki
 */
public class ArgumentParser {

    public static void main(String[] args) throws ParseException {
        String[] args2 = {"-h"};

        Params.parseParams(args2);

    }
}
