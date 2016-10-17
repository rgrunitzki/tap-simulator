/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package driver.learning;

import driver.learning.exploration.EpsilonDecreasing;
import experiments.DefaultExperiment;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import scenario.ImplementedTAP;
import simulation.Params;

/**
 *
 * @author rgrunitzki
 */
public class QLStatefullTest {

    public QLStatefullTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of of class QLStatefull.
     */
    @Test
    public void testOWNetwork() {

        Params.COLUMN_SEPARATOR = "\t";

        Params.PRINT_OD_PAIRS_AVG_COST = false;
        Params.PRINT_FLOWS = false;
        Params.PRINT_ON_TERMINAL = false;
        Params.PRINT_AVERAGE_RESULTS = false;
        Params.PRINT_ON_FILE = false;

        Params.MAX_EPISODES = 150;
        Params.MAX_STEPS = 100;
        EpsilonDecreasing.EPSILON_DECAY = 0.91f;
        Params.DEFAULT_TAP = ImplementedTAP.OW;

        //"QLStatefull"
        EpsilonDecreasing.EPSILON_INITIAL = 1f;
        QLStatefull.ALPHA = 0.9f;
        QLStatefull.GAMMA = 0.99f;
        Params.DEFAULT_ALGORITHM = QLStatefull.class;

        Params.createTap();

        DefaultExperiment experiment = new DefaultExperiment();
        experiment.run();

        assertEquals(67.15, (experiment.averageTravelCost()), 1.0);
    }

}
