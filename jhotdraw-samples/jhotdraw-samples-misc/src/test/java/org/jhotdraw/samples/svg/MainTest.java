package org.jhotdraw.samples.svg;

import org.junit.Test;
import java.awt.GraphicsEnvironment;

public class MainTest {

    /**
     * Test of main method, of class Main.
     */
    @Test
    public void testMain() {
        // Check if the environment is headless
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Running in headless mode. Skipping UI test.");
            return; // Skip the test if headless
        }

        System.out.println("main");
        String[] args = {};
        Main.main(args);

    }
}
