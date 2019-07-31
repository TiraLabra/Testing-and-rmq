package rmq.ui;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConsoleUI {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    
    String join(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }
    
    @Test
    public void testMinimaStatic() {
        Scanner scan = new Scanner(join(new String[] {"s", "r", "2", ""}));
        ConsoleUI ui = new ConsoleUI(scan);
        ui.run();
        String[] output = outContent.toString().split("\n");
        assertEquals(6, output.length);
        assertTrue(output[0].startsWith("Select static"));
        assertTrue(output[1].startsWith("Enter integers manually"));
        assertTrue(output[2].startsWith("How many?"));
        assertTrue(output[3].startsWith("Enter queries"));
        assertTrue(output[4].startsWith("["));
        assertTrue(output[5].startsWith("left:"));
    }
    
    @Test
    public void testMinimaDynamic() {
        Scanner scan = new Scanner(join(new String[] {"d", "r", "2", ""}));
        ConsoleUI ui = new ConsoleUI(scan);
        ui.run();
        String[] output = outContent.toString().split("\n");
        assertEquals(5, output.length);
        assertTrue(output[0].startsWith("Select static"));
        assertTrue(output[1].startsWith("Enter integers manually"));
        assertTrue(output[2].startsWith("How many?"));
        assertTrue(output[3].startsWith("Edit values"));
        assertTrue(output[4].startsWith("Terminate"));
    }
    
    @Test
    public void testNumberRead() {
        Scanner scan = new Scanner(join(new String[] {"s", "e", "2", "3", "4", "f", ""}));
        new ConsoleUI(scan).run();
        String arr = outContent.toString().split("\n")[4];
        assertTrue(arr.startsWith("[2, 3, 4]"));
    }
    
    @Test
    public void testStaticQuery() {
        Scanner scan = new Scanner(join(new String[] {
            "s", "e", "2", "1", "3", "4", "5", "", 
            "0", "2", "2", "4", ""
        }));
        new ConsoleUI(scan).run();
        String[] output = outContent.toString().split("\n");
        assertEquals(12, output.length);
        assertTrue(output[7].startsWith("arr[0,2]: 1"));
        assertTrue(output[10].startsWith("arr[2,4]: 3"));
    }
    
    /**
     * Deterministic version of random that can be used for dependency
     * injection in tests that need it.
     */
    private class InjectedRandom extends Random {
        private final int[] arr;
        private int current = 0;
        
        /**
         * Create a new InjectedRandom where the return values for nextInt
         * will infinitely rotate through the parameter array.
         * @param arr 
         */
        public InjectedRandom(int[] arr) {
            this.arr = arr;
        }
        
        /**
         * Overrides the default implementation of Random. Return values 
         * will be picked for the array passed to the constructor.
         * @param v Not used
         * @return The next integer in the array. (First if no calls
         * were previously recorded or if the previous call returned
         * the last element.
         */
        @Override
        public int nextInt(int v) {
            current %= arr.length;
            int val = arr[current];
            current++;
            return val;
        }
    }
}
