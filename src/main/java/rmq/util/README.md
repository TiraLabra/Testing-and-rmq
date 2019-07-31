# Performance testing

Especially in java, performance testing is a tradeoff between the resources spent on running tests and accuracy of results.

If a thing is done once, there is a good chance that the code was actually compiled while timing rendering the results completely useles. This is due to the fact that java is a JIT (Just In Time) compiled language and no code is necessarily compiled untill it is actually called. Due to this it is generally a good idea to time everything at least two times and completely discarding the first result.

Since java is a garbage collected language, it is very possible that some results are significantly skewed due to garbage collection running during timing and blocking the timed function for the duration of garbage collection. The solution to this is to run a lot of tests for the same thing and averaging out the results or pruning outliers.

Additionally when running on a computer with an operating system, the task sceduling and os overhead can have some impact on results. Especially if the system is low on memory.

As a rule oh thumb, run a lot of tests and discard the first result.

## Performance testing for RMQ.

Performance tests for the two RMQ structures are written in `Tester.java`.

In testing two different values are of intrest for data structure comparison. The preprocessing time taken when building the different data structures and the time taken when querying the data structures. Additionally it may be intresting to get timing for updating the dynamic structures but this is skipped since we have no point of comparison.

To see how the data structures scale with array size, all tests are run with arrays of sizes `{10, 100, 1000, 10000, 100000, 1000000, 10000000}`. To get better results, bigger arays could probably be used, but the tests take long enough to run as is (~15 minutes on an old laptop).

### Preprocessing times

For testing the preprocessing time, both of the structures are built 100 times for each of the input array sizes, and the median is stored. Here the median is used instead of the mean to avoid the extreme outlier of JIT compilation and to minimize the impact of garbage collection.

```java
    DynamicRMQ dRMQ = new DynamicRMQ(arr);
    for (int i = 0; i < n; i++) {
        t = System.nanoTime();
        dRMQ = new DynamicRMQ(arr);
        t = System.nanoTime() - t;
        times[i] = t;
    }
    Arrays.sort(times);
    dynamicInits[run] = times[times.length / 2] / 1000000.0;
```

`System.nanotime` is used for timing. Taking a timestamp as close to the code-to-time as possible and comparing the timestamp after the intresting code has run.

### Query times

To test query times, 10000 queries are generated for each of the structures. Each of these queries are run 100 times and the mean is taken for each query time. After the queries have run, the mean and standard deviation of each of the queries are reported.

```java
    times = new long[lArr.length];
    for (int i = 0; i < lArr.length; i++) {
        long tAcc = 0;
        int l = lArr[i];
        int r = rArr[i];
        for (int j = 0; j < n; j++) {
            t = System.nanoTime();
            sRMQ.query(l, r);
            tAcc += System.nanoTime() - t;
        }
        times[i] = tAcc / n;
    }
    staticQueries[run] = getAverage(times);
    staticStd[run] = getStd(times, staticQueries[run]);
```

The multiple level repetition may seem slightly excessive but reducing the number of iteration on the inner loop tends to make results fairly noisy. The inner level averaging is simply there to reduce impact of garbage collection and operating system overhead. If running on a computer with more capacity to spare, fewer iterations may be required.

The timing itself was done exactly the same as for the preprocessing times. Times as close to before and after executing the `query(l, r)` are compared for a "real world" execution time.

## Some analysis of the results

Tests were run on an `Intel(R) Core(TM) i5-3437U` cpu with 8GB of RAM. The computer was fairly old and under fairly heavy load during testing. As such the results here are likely more noisy than if run on a system with more resources.

```
Dynamic RMQ preprocessng times:
      10: 0.014798ms
     100: 0.010918ms
    1000: 0.018245ms
   10000: 0.037604ms
  100000: 0.319337ms
 1000000: 3.934933ms
10000000: 62.961225ms

Static RMQ preprocessing times:
      10: 0.022808ms
     100: 0.02369ms
    1000: 0.298455ms
   10000: 4.356129ms
  100000: 52.450047ms
 1000000: 647.011838ms
10000000: 7818.361586ms
```

The results for preprocessing times are not unexpected. The segment tree used in the dynamic implementation is built using a very efficient linear algorithm where almost all the data fits into the CPU caches for all but the largest array, while the preprocessing for the static RMQ runs in <sub><img src="https://latex.codecogs.com/svg.latex?\mathcal{O}(n&space;\log&space;n)" title="O(n log n)" /></sub> time and runs out of CPU cache space sooner.

```
Dynamic RMQ lookup times:
      10: 46.5028ns, std: 28.960608830727722ns
     100: 52.1385ns, std: 14.596678528087555ns
    1000: 57.8515ns, std: 18.018393768629977ns
   10000: 77.0664ns, std: 47.23534173466832ns
  100000: 87.4741ns, std: 21.990904692142944ns
 1000000: 113.0819ns, std: 36.47509061830483ns
10000000: 124.5604ns, std: 31.629694838862616ns

Static RMQ lookup times:
      10: 93.1286ns, std: 25.14617456094359ns
     100: 113.1642ns, std: 22.816167428158053ns
    1000: 99.168ns, std: 13.6808074487305ns
   10000: 107.9099ns, std: 36.98128639655819ns
  100000: 99.3353ns, std: 14.551111551862904ns
 1000000: 101.7498ns, std: 15.629012334842363ns
10000000: 107.4176ns, std: 23.026359494352093ns
```

For the lookup time the results are perhaps more intresting but significantly more noisy. 

Clearly the dynamic structure is faster for small array sizes. This is likely due to the very good cache performance and low constant factors of the structure. 

The static structure seems to stay fairly consistent at somewhere around 100ns per query. This can likely be expalined by the very high constant factors of calculating base 2 logarithms in java and poor cache performanse. 

It is intresting to note tho that the static structure is faster and with a lower standard deviation for arrays of sizes 1000000 and 10000000. The expectation would be that this difference would be more pronounced as array sizes grow further.

## To reiterate

The more runs, data and iterations, the better. But even for simple data structures and algorithms, running massing numbers of iterations on big datasets takes a significant amount of time.
