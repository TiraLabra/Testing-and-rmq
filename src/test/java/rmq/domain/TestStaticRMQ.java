
package rmq.domain;

import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestStaticRMQ {
    
    /**
     * Trivial brute force implementation of rmq.
     * Used here to check proper working of the more advanced structures
     */
    private int bruteforce(int[] arr, int l, int r) {
        int ret = arr[l];
        for (int i = l + 1; i < r + 1; i++) {
            ret = ret < arr[i] ? ret : arr[i];
        }
        return ret;
    }
    
    @Test
    public void tinyTest() {
        int[] arr = {4, 3, 2, 1};
        StaticRMQ rmq = new StaticRMQ(arr);
        assertEquals("Wrong minimum for whole array", 1, rmq.query(0, 3));
        assertEquals("Wrong minimum for first element", 4, rmq.query(0, 0));
    }
    
    @Test
    public void randomTest() {
        Random rand = new Random(7);
        int[] arr = new int[100];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rand.nextInt(10000);
        }
        StaticRMQ rmq = new StaticRMQ(arr);
        for (int l = 0; l < arr.length; l++) {
            for (int r = l; r < arr.length; r++) {
                assertEquals("Error for " + l + ", " + r + "\n" + 
                        Arrays.toString(arr) + rmq.toString() + "\n", 
                        bruteforce(arr, l, r), rmq.query(l, r));
            }
        }
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void invalidLTest() {
        int[] arr = {4, 3, 2, 1};
        StaticRMQ rmq = new StaticRMQ(arr);
        rmq.query(-1, 0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void invalidRTest() {
        int[] arr = {4, 3, 2, 1};
        StaticRMQ rmq = new StaticRMQ(arr);
        rmq.query(0, 4);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void invalidRangeTest() {
        int[] arr = {4, 3, 2, 1};
        StaticRMQ rmq = new StaticRMQ(arr);
        rmq.query(2, 1);
    }
}
