# Tiny example for project work in data structures and algorithms

Note that the scope and documentation of this project is not appropriate for an actual data structures and algorithms project. This is intended to provide a primer into unit and performance testing with java (and gradle). Additionally there may be some general notes about code and project structure.

Additionally this project has a working [gradle setup](build.gradle) for Jacoco and [Checkstyle](config/checkstyle/checkstyle.xml) that may be useful for people doing the project course.

The code in this repository contains several more or less critical bugs. Finding these bugs could be considered a challenge when reviewing this material. None of the bugs (that are known at least) relate to the actual data structure or algorithm implemetations but rather to how other code interfaces with them.

# RMQ

RMQ or range minimum query simply means that given an intervall in an array, we want to find the minimum in that interval. So for examplme given the array `{2, 4, 5, 1, 8, 3, 7, 2}` and the (zero indexed closed) range `[0,3]` we should find the range minumum of 2 at index 0.

In this example project two different range minimum query structures have been implemented. The specifics of the implementations are not critical for reading and understanding the material presented here. However if intrested the [RMQ wikipedia article](https://en.wikipedia.org/wiki/Range_minimum_query) contains some very good explanations.

The [first implementation](src/main/java/rmq/domain/StaticRMQ.java) is a static structure where values can not be updated after initialization. This structure is based on precalculating and storing specific ranges in such a way that queries can be done in constant time while O(n log n) space is used.

The [second approach](src/main/java/rmq/domain/DynamicRMQ.java) is based on a segment tree that enables updating of values after initialization. The segment tree approach yields O(log n) time complexity for lookups while requiring O(n) space.

# Testing

This project is a [gradle](https://gradle.org/) project and has [Jacoco](https://docs.gradle.org/current/userguide/jacoco_plugin.html) set up to generate test coverage reports.

The project (once cloned) can be compiled and tested by running `./gradlew build`. This will also generate the coverage reports to `build/reports/jacoco/test/html/index.html`. Note that the code for performance testing is not included in the report. This is due to an [exlusion rule in the build.gradle file](build.gradle#L41). Similar exlusion rules are convenient for excluding for example GUI code.

Below you will find links to folders in this repository where more markdown documentation and some relevant code can be found. 

Topics include: 

Basic data structure/algorithm testing using unit tests.

## [Basic data structure testing](src/test/java/rmq/domain)

How to use dependency injection to test "hard to test" stuff

## [Introduction to dependency injection](src/test/java/rmq/ui)

How to do basic timing and very basic analysis for performance tests

## [Performance testing](src/main/java/rmq/util)

If you have looked at the test coverage report you may have noticed that the dynamic RMQ and parts of the UI are not covered. The idea is that coverage for these can be completed as an exercize for someone familiarizing themselves with testing.

# Project structure and code quality

While this project is very small, some attention has been paid to try to follow good progamming practices. [DRY](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself), [Single responsibility](https://en.wikipedia.org/wiki/Single_responsibility_principle) and [clean code](https://medium.com/mindorks/how-to-write-clean-code-lessons-learnt-from-the-clean-code-robert-c-martin-9ffc7aef870c) principles in general.

[Checkstyle](https://docs.gradle.org/current/userguide/checkstyle_plugin.html) is used to autmate some code quality checks. The configuration is a modified version of the [google-checks.xml](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml) file. The checks will be run during `gradle build` and can be run separately with `gradle check`. You may note that checkstyle does produce some warnings for missing javadocs. Adding at least simple javadocs to those places would probably be a good idea.

The [run method](src/main/java/rmq/util/Tester.java#L26) in the Tester class is fairly long and repetetive. It could be a good idea to refactor the preprocessing and query running steps into separate methods to limit repetition. However, this kind of testing code is often left fairly raw since it's not considered actual "production code". 

# Contriburing to the example

If you find issues with the content of this repository or feel that something would require clarification, please submit a pull reques or an issue.
