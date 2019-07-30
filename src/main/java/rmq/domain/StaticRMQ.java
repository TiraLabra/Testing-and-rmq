package rmq.domain;

import java.util.Arrays;

/**
 * Static range minimum query structure for given integer array
 * <p>
 * A data structure that supports range minimum queries for a given array in 
 * constant time and O(n log n) space.
 * </p>
 */
public class StaticRMQ implements RMQ {
    private final int[][] structure;
    private final int[] arr;
    
    /**
     * Static range minimum query structure for given integer array.
     * <p>
     * A data structure that supports range minimum queries for a given array in 
     * constant time and O(n log n) space.
     * </p>
     * 
     * @param arr The input array to compute the range minima for.
     */
    public StaticRMQ(int[] arr) {
        this.arr = arr.clone();
        int rows = (int)(Math.log(arr.length) / Math.log(2));
        structure = new int[rows + 1][arr.length + 1];
        for (int i = 0; i < structure[0].length; i++) {
            structure[0][i] = i;
        }
        for (int j = 1; j < structure.length; j++) {
            for (int l = 1; l < structure[j].length; l++) {
                int r = l + (int)Math.pow(2, j - 1);
                if (r < structure[j].length && 
                        arr[structure[j - 1][l] - 1] > arr[structure[j - 1][r] - 1]) {
                    structure[j][l] = structure[j - 1][r];
                }
                else {
                    structure[j][l] = structure[j - 1][l];
                }
            }
        }
    }
    
    /**
     * Compute the minimum  value in arr[l..r] in constant time
     * 
     * @param l Left limit of the query range (inclusive).
     * @param r Right limit of the query range (inclusive).
     * @return The minimum value in arr[l..r].
     */
    @Override
    public int query(int l, int r) {
        if (l < 0) {
            throw new IndexOutOfBoundsException("l = " + l);
        }
        if (r >= arr.length) {
            throw new IndexOutOfBoundsException("r = " + r);
        }
        if (l > r) {
            throw new IllegalArgumentException("r needs to be greater or equal to l");
        }
        int k = (int)(Math.log(r - l + 1) / Math.log(2));
        int rr = r - (int)Math.pow(2, k) + 2;
        return Math.min(arr[structure[k][l + 1] - 1], arr[structure[k][rr] - 1]);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] r : structure) {
            sb.append("\n");
            sb.append(Arrays.toString(r));
        }
        return sb.toString();
    }
}
