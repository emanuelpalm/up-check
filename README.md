# UpCheck

UpCheck is a [UPPAAL](http://uppaal.org) model fix suggester. Given some UPPAAL system with one or more unsatisfied verification queries, UpCheck attempts to find some set of system changes that would lead to those queries being satisfied.
 
The application is, at this point, to be regarded as a proof-of-concept.

## Testing, Running and Building

The application is built using [Gradle](http://gradle.org). Most popular Java IDE:s have either native or plugin support for Gradle. If wanting to use the command line, the following command are some examples of what could be executed on macOS or Linux from the project root folder. The commands are also available on Windows, but use `gradlew.exe` instead of `./gradlew`.

```sh
$ ./gradlew test                       # Runs project tests
$ ./gradlew run                        # Runs application
$ ./gradlew build                      # Builds project
$ ./gradlew distZip                    # Creates project ZIP distribution
```

### UPPAAL Dependency Management

In order to be able to interact with UPPAAL systems, the application needs to communicate with the UPPAAL server, which is a simple executable run on the host computer. To make that interaction more straightforward, the `model.jar` library distributed with the UPPAAL application is utilized. This particular JAR cannot be part of the UpCheck repository or application distribution, since UpCheck is distributed under the [GNU GPLv3 license](LICENSE), while UPPAAL is proprietary.

This means that in order for UpCheck to be fully functional, you need to [download](http://www.uppaal.org/download.shtml) and install UPPAAL 4.1+ on your development machine, copy the `model.jar` ([javadoc](http://people.cs.aau.dk/~marius/modeldoc/)) file from the UPPAAL application folder into [lib/uppaal/](lib/uppaal/), being relative to the folder in which this document is located.

When the UpCheck application boots up for the first time, it will prompt you for the location of a local UPPAAL installation. If correctly provided, the application will seamlessly launch a new Java process with a `classpath` modified to include required proprietary Java libraries. This way no proprietary files need to be part of the UpCheck application.

## Application Architecture

The application is loosely built around the Model-View-Controller (MVC) pattern, uses Publish-Subscribe for event propagation, and promises for asynchronous task management. Worthy of special mention is also the bootstrapping procedure used to populate the Java classpath with UPPAAL JARs. More can be read about these topics via the below links.

| Topic                       | Description                                  |
|:----------------------------|:---------------------------------------------|
| [MVC][mvc]                  | General division of application concerns.    |
| [Publish-Subscribe][pubsub] | System for distributing event notifications. |
| [Promises][promises]        | Helps manage asynchronous task execution.    |
| [Bootstrapping][boot]       | The process of loading required UPPAAL JARs. |

[mvc]: docs/MVC.md
[pubsub]: src/main/java/se/ltu/dcc/upcheck/util/EventBroker.java
[promises]: src/main/java/se/ltu/dcc/upcheck/util/Promise.java
[boot]: src/main/java/se/ltu/dcc/upcheck/Bootstrap.java

Most classes should be properly documented. In case of questions, write an issue on GitHub.