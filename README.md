# service-scanner

[![](https://jitpack.io/v/com.hunterwb/service-scanner.svg)](https://jitpack.io/#com.hunterwb/service-scanner)
[![Build Status](https://img.shields.io/circleci/project/github/hunterwb/service-scanner.svg)](https://circleci.com/gh/hunterwb/service-scanner)

A Java 6 annotation [processor](https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/Processor.html) which checks every class during compilation to determine if it is a [ServiceLoader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) provider and if so, adds its name to the `META-INF/services` configuration file.

The fully qualified [binary names](https://docs.oracle.com/javase/specs/jls/se8/html/jls-13.html#jls-13.1) of the services to look for must be passed to `javac` in the format:

`-Aservices=com.example.Service1,com.example.Service2`

##### Maven usage:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.0</version>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>com.hunterwb</groupId>
                        <artifactId>service-scanner</artifactId>
                        <version>0.1.2</version>
                    </path>
                </annotationProcessorPaths>
                <showWarnings>true</showWarnings>
                <compilerArgs>
                    <arg>-Aservices=com.example.Service1,com.example.Service2</arg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

##### Gradle usage:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    annotationProcessor "com.hunterwb:service-scanner:${project.version}"
}

compileJava {
    options.compilerArgs.add('-Aservices=com.example.Service1,com.example.Service2')
}
```
