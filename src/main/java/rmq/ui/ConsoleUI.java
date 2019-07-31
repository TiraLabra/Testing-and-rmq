package rmq.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import rmq.domain.DynamicRMQ;
import rmq.domain.RMQ;
import rmq.domain.StaticRMQ;
import rmq.util.Tester;

public class ConsoleUI {
    private final Scanner scan;
    private final Random rand;
    
    public ConsoleUI() {
        this(new Scanner(System.in));
    }

    public ConsoleUI(Scanner scan) {
        this.scan = scan;
        this.rand = new Random();
    }

    public void run() {
        String sel = "";
        while (!sel.equals("s") && !sel.equals("d") && !sel.equals("p")) {
            System.out.println("Select static (s) or dynamic (d) rmq, " 
                    + "or do performance testing (p). ");
            sel = scan.nextLine();
        }
        if (sel.equals("p")) {
            runPerformanceTests();
            return;
        }
        boolean stat = sel.equals("s");
        while (!sel.equals("r") && !sel.equals("e")) {
            System.out.println("Enter integers manually (e) or generate randomly (r).");
            sel = scan.nextLine();
        }
        int[] arr;
        if (sel.equals("r")) {
            arr = getRandomArr();
        } else {
            arr = getNumbers();
        }
        if (stat) {
            doStatic(new StaticRMQ(arr), arr);
        } else {
            doDynamic(new DynamicRMQ(arr), arr);
        }
    }

    private void doStatic(RMQ rmq, int[] arr) {
        System.out.println("Enter queries. End with invalid input");
        System.out.println(Arrays.toString(arr));
        while (true) {
            try {
                System.out.println("left: ");
                int l = Integer.parseInt(scan.nextLine());
                System.out.println("right: ");
                int r = Integer.parseInt(scan.nextLine());
                System.out.println("arr[" + l + "," + r + "]: " + rmq.query(l, r));
            } catch (Exception ex) {
                break;
            }
            
        }
    }

    private void doDynamic(DynamicRMQ rmq, int[] arr) {
        String sel = "s";
        while (!sel.isEmpty()) {
            System.out.println("Edit values (e) or make queries (q).\nTerminate with empty input.");
            sel = scan.nextLine();
            if (sel.equals("e")) {
                update(rmq, arr);
            } else if (sel.equals("q")) {
                doStatic(rmq, arr);
            }
        }
    }

    private void update(DynamicRMQ rmq, int[] arr) {
        System.out.println("Update values. End with invalid input.");
        System.out.println(Arrays.toString(arr));
        while (true) {
            try {
                System.out.println("index:");
                int idx = Integer.parseInt(scan.nextLine());
                System.out.println("value:");
                int val = Integer.parseInt(scan.nextLine());
                arr[idx] = val;
                rmq.update(idx, val);
            } catch (Exception e) {
                break;
            }
        }
    }

    private int[] getNumbers() {
        ArrayList<Integer> nums = new ArrayList<>();
        System.out.println("Enter integers for array. End with non-integer");
        while (true) {
            try {
                nums.add(Integer.parseInt(scan.nextLine()));
            } catch (Exception e) {
                break;
            }
        }
        int[] arr = new int[nums.size()];
        for (int i = 0; i < nums.size(); i++) {
            arr[i] = nums.get(i);
        }
        return arr;
    }

    private int[] getRandomArr() {
        int n;
        while (true) {
            System.out.println("How many?");
            try {
                n = Integer.parseInt(scan.nextLine());
                break;
            } catch (Exception e) {
                continue;
            }
        }
        int[] arr = new int[n];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rand.nextInt(10000);
        }
        return arr;
    }

    private void runPerformanceTests() {
        Tester tester = new Tester();
        tester.run();
        System.out.println(tester.toString());
    }
    
    
}
