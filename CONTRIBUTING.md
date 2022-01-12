# Contributing to the LaunchDarkly Logging API for Java
 
LaunchDarkly has published an [SDK contributor's guide](https://docs.launchdarkly.com/docs/sdk-contributors-guide) that provides a detailed explanation of how our SDKs work. See below for additional information on how to contribute to this project.
 
## Submitting bug reports and feature requests
 
The LaunchDarkly SDK team monitors the [issue tracker](https://github.com/launchdarkly/java-logging/issues) in the GitHub repository. Bug reports and feature requests specific to this project should be filed in this issue tracker. The SDK team will respond to all newly filed issues within two business days.
 
## Submitting pull requests
 
We encourage pull requests and other contributions from the community. Before submitting pull requests, ensure that all temporary or unintended code is removed. Don't worry about adding reviewers to the pull request; the LaunchDarkly SDK team will add themselves. The SDK team will acknowledge all pull requests within two business days.
 
## Build instructions
 
### Prerequisites
 
The project builds with [Gradle](https://gradle.org/) and should be built against Java 8. Since it can be used in Android as well as server-side Java, do not use any Java 8 APIs that are unsupported in Android (CI tests will enforce this).
 
### Building

To build the project without running any tests:
```
./gradlew jar
```

If you wish to clean your working directory between builds, you can clean it by running:
```
./gradlew clean
```

If you wish to use your generated package in another Maven/Gradle project such as [java-server-sdk](https://github.com/launchdarkly/java-server-sdk), you will likely want to publish the artifact to your local Maven repository so that your other project can access it.
```
./gradlew publishToMavenLocal
```

### Testing
 
To build the project and run all unit tests:
```
./gradlew test
```

## Note on Java version and Android support

This project can be used both in server-side Java and in Android. Its minimum Java version is 8, but not all Java 8 APIs and syntax are supported in Android. The CI jobs for this project include an Android job that runs all of the unit tests in Android, to verify that no unsupported APIs are being used.

## Code coverage

It is important to keep unit test coverage as close to 100% as possible in this project, since the SDK projects will not exercise every `com.launchdarkly.logging` method in their own unit tests.

Sometimes a gap in coverage is unavoidable, usually because the compiler requires us to provide a code path for some condition that in practice can't happen and can't be tested, or because of a known issue with the code coverage tool. Please handle all such cases as follows:

* Mark the code with an explanatory comment beginning with "COVERAGE:".
* Run the code coverage task with `./gradlew jacocoTestCoverageVerification`. It should fail and indicate how many lines of missed coverage exist in the method you modified.
* Add an item in the `knownMissedLinesForMethods` map in `build.gradle` that specifies that number of missed lines for that method signature.

## Note on dependencies

This project's `build.gradle` contains special logic to exclude dependencies from `pom.xml`. This is because it is meant to be used as part of one of the LaunchDarkly SDKs, and the different SDKs have different strategies for either exposing or embedding these dependencies. Therefore, it is the responsibility of each SDK to provide its own dependency for any module that is actually required in order for `com.launchdarkly.logging` to work.
