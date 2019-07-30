package rmq.domain;

import java.util.Arrays;

/**
 * Dynamic range minimum query structure for given integer array
 * <p>
 * A data structure based on a segment tree, that supports range minimum 
 * queries for a given array in O(log n) time and O(n) space.
 * </p>
 */
public class DynamicRMQ implements RMQ {
    private final int size;
    private final int[] st;

    /**
     * Dynamic range minimum query structure for given integer array
     * <p>
     * A data structure based on a segment tree, that supports range minimum 
     * queries for a given array in O(log n) time and O(n) space.
     * </p>
     * 
     * @param arr The input array to compute the range minima for.
     */
    public DynamicRMQ(int[] arr) {
        this.size = arr.length;
        int s = (int)Math.pow(2, Math.ceil(Math.log(2 * arr.length) / Math.log(2)));
        st = new int[s];
        Arrays.fill(st, Integer.MAX_VALUE);
        System.arraycopy(arr, 0, st, s / 2, arr.length);
        for (int i = s / 2 - 1; i > 0; i--) {
            st[i] = Math.min(st[i * 2], st[i * 2 + 1]);
        }
    }
    
    /**
     * Update given value in arr.
     * 
     * @param idx Index of value to update.
     * @param value Value to set.
     */
    public void update(int idx, int value) {
        idx += st.length / 2;
        st[idx] = value;
        for (idx /= 2; idx > 1; idx /= 2) {
            st[idx] = Math.min(st[idx * 2], st[idx * 2 + 1]);
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
            throw new IndexOutOfBoundsException("l: " + l);
        }
        if (r >= size) {
            throw new IndexOutOfBoundsException("r: " + r);
        }
        if (l > r) {
            throw new IllegalArgumentException("l needs to be greater or equal to r");
        }
        l += st.length / 2;
        r += st.length / 2;
        int res = st[l];
        while (l <= r) {
            if (l % 2 == 1) {
                res = st[l] < res ? st[l] : res;
                l++;
            }
            if (r % 2 == 0) {
                res = st[r] < res ? st[r] : res;
                r--;
            }
            l /= 2;
            r /= 2;
        }
        return res;
    }

    @Override
    public String toString() {
        return Arrays.toString(st);
    }
}
