package rmq.util;

import java.util.Arrays;
import java.util.Random;
import rmq.domain.DynamicRMQ;
import rmq.domain.StaticRMQ;

public class Tester {
    private final double[] dynamicInits = new double[6];
    private final double[] staticInits = new double[6];
    private final double[] dynamicQueries = new double[6];
    private final double[] dynamicStd = new double[6];
    private final double[] staticQueries = new double[6];
    private final double[] staticStd = new double[6];
    private final int[] nums = {10, 100, 1000, 10000, 100000, 1000000};
    private final Random rand;

    public Tester(Random rand) {
        this.rand = rand;
    }

    public Tester() {
        this(new Random());
    }
    
    public void run() {
        int n = 100;
        for (int run = 0; run < nums.length; run++) {
            int num = nums[run];
            int[] arr = new int[num];
            long[] times = new long[n];
            long t;
            
            // Measure median preprocessing time for dynamic RMQ.
            DynamicRMQ dRMQ = new DynamicRMQ(arr);
            for (int i = 0; i < n; i++) {
                t = System.nanoTime();
                dRMQ = new DynamicRMQ(arr);
                t = System.nanoTime() - t;
                times[i] = t;
            }
            Arrays.sort(times);
            dynamicInits[run] = times[times.length / 2] / 1000000.0;
            
            // Measure median preprocessing time for static RMQ.
            StaticRMQ sRMQ = new StaticRMQ(arr);
            for (int i = 0; i < n; i++) {
                t = System.nanoTime();
                sRMQ = new StaticRMQ(arr);
                t = System.nanoTime() - t;
                times[i] = t;
            }
            Arrays.sort(times);
            staticInits[run] = times[times.length / 2] / 1000000.0;
            
            //generate queries
            int[] lArr = new int[n * 100];
            int[] rArr = new int[lArr.length];
            for (int i = 0; i < lArr.length; i++) {
                lArr[i] = rand.nextInt(num - 1);
                rArr[i] = lArr[i] + rand.nextInt(num - lArr[i]);
            }
            
            //Measure lookup for dynamic RMQ
            times = new long[lArr.length];
            for (int i = 0; i < lArr.length; i++) {
                int l = lArr[i];
                int r = rArr[i];
                t = System.nanoTime();
                dRMQ.query(l, r);
                t = System.nanoTime() - t;
                times[i] = t;
            }
            dynamicQueries[run] = getAverage(times);
            dynamicStd[run] = getStd(times, dynamicQueries[run]);
            
            //Measure lookup for static RMQ
            times = new long[lArr.length];
            for (int i = 0; i < lArr.length; i++) {
                int l = lArr[i];
                int r = rArr[i];
                t = System.nanoTime();
                sRMQ.query(l, r);
                t = System.nanoTime() - t;
                times[i] = t;
            }
            staticQueries[run] = getAverage(times);
            staticStd[run] = getStd(times, staticQueries[run]);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Dynamic preprocessng times:\n");
        appendResults(sb, dynamicInits, "ms", null);
        
        sb.append("\nStatic preprocessing times:\n");
        appendResults(sb, staticInits, "ms", null);
        
        sb.append("\nDynamic lookup times:\n");
        appendResults(sb, dynamicQueries, "ns", dynamicStd);
        
        sb.append("\nStatic lookup times:\n");
        appendResults(sb, staticQueries, "ns", staticStd);
        
        return sb.toString();
    }

    private void appendResults(StringBuilder sb, double[] arr, String suffix, 
            double[] std) {
        for (int i = 0; i < nums.length; i++) {
            String num = Integer.toString(nums[i]);
            for (int j = 0; j < 7 - num.length(); j++) {
                sb.append(" ");
            }
            sb.append(num);
            sb.append(": ");
            sb.append(arr[i]);
            sb.append(suffix);
            if (std != null) {
                sb.append(", std: ");
                sb.append(std[i]);
                sb.append(suffix);
            }
            sb.append("\n");
        }
    }

    private double getStd(long[] times, double mean) {
        double s = 0;
        for (long time : times) {
            s += Math.pow(time - mean, 2);
        }
        return Math.sqrt(s / (times.length - 1));
    }

    private double getAverage(long[] times) {
        double s = 0;
        for (long time : times) {
            s += time;
        }
        return s / times.length;
    }
}
