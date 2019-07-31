# Slightly more advanced testing

This is intended as an example for how dependency injection can be used. In the context of a data structures and algorithms intermediate project, the actual testing done here is not very relevant as testing of UI elements is not required. However a need to test stuff with dependency injection crops up regularly.

## Dependency injection

In the above test file are some tests for `ConsoleUI`. The UI uses both `java.util.Scanner` and `java.util.Random`. Due to this, automated testing is made farily difficult by conventional means.

In java, using dependency injection requires a constuctor where the dependency injection can be done. E.g. ConsoleUi has two constuctors. One takes no parameters and is the one generally used in practice. The other one takes a `Scanner` object as a parameter and is the one that can be used for testing.

```java
    public ConsoleUI() {
        this(new Scanner(System.in));
    }

    public ConsoleUI(Scanner scan) {
        this.scan = scan;
        this.rand = new Random();
    }
```

Conveniently the built in `Scanner` object can be initialized with a string that the scanner reads from every time it is called.

```java
new Scanner("First\nSecond\nThird")
```

When `nextLine` is called for the scanner, it will first return "First". On the next call "Second" will be returned and so on.

With this we can run any class that takes command line input (using `nextLine`)\*. And if the class writes to a file or returns something we can test it using basic unit testing practices.<br /><sub>\* `nextInt` and similar scanner functions and work differently. It is generally not recommended to use `nextInt` and the like.</sub>

The test coverage for `ConsoleUI` is not particularily impressive. The `update` method for example is completely untested. As an exercise you could, after reading this document, write tests to improve the test coverage.

## Injecting random

The standard random in java does not support initialization with a sequence in a practical way. Random can be made deterministic by injecting a seeded random, this isn't that convenient however since control of the actual output sequence is somewhat limited.

A new class can be created that is subclass of the standard random. This way, the things we pass out new "random" to will accept is as an instance of `java.util.Random` while control of the actual output is controlled by replacing the commands we want to use in testing.

```java
    private class InjectedRandom extends Random {
        private final int[] arr;
        private int current = 0;
        
        public InjectedRandom(int[] arr) {
            this.arr = arr;
        }
        
        @Override
        public int nextInt(int v) {
            current %= arr.length;
            int val = arr[current];
            current++;
            return val;
        }
```

The above class can be used to replace `java.util.Random` in a class we want to test. It is initialized with an array of integers that will be used every time `nextInt` is called, giving complete controll to the testing function.

The `InjectedRandom` class has been implemented in `TestConsoleUI.java` but `ConsoleUI.java` does not support injection of `Random` and the `getRandomArr` has not been tested.

If You want to try it out, after reading the next section, you can add support for random injection to `ConsoleUI` and write a test (or tests) that cover `getRandomArr`.

## Hooking the standard output

Sadly `ConsoleUI` prints to the standard output and doesn't return anything. The only way to evaluate the correctness of execution is to look at the output of the user interface. We have to somehow redirect the standard output to some other place that we can actually access in the tests.

In java this can be accomplished by using `System.setOut` to temporarily redirect the output to a `ByteArrayOutputStream`.

```java
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }
```

Here the `@Before` annotated function is run before every test to redirect `System.out.println` to a new `PrintStream` that writes to a `ByteArraOutputStream` that we control. Conversely the `@After` annotated function will be run after every test to make sure that printing works as usual.

After this setup, the content printed by anyting we call in a test can be accessed by accessing the `ToString` representation of the `ByteArrayOutputStream`.

```java
    @Test
    public void testNumberRead() {
        Scanner scan = new Scanner("s\ne\n2\n3\n4\nf\n\n"}));
        new ConsoleUI(scan).run();
        String arr = outContent.toString().split("\n")[4];
        assertTrue(arr.startsWith("[2, 3, 4]"));
    }
```

Above we check the fifth line of output when the console ui is prompted with `"s", "e", "2", "3", "4", "f", ""`.

Using this in combination with dependency injection, classes that read and write to/from the standard I/O stream can be tested... Provided they support dependency injection.
