# ServiceScanner

Automatically configure ServiceLoader providers

A Java annotation processor which automatically discovers all [ServiceLoader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) providers and generates the`META-INF/services` provider-configuration files. A class is determined to be a service provider if it has a public no-argument constructor and [is assignable to](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#isAssignableFrom-java.lang.Class-) a service type. The canonical names of all services must be passed to `javac` (examples below). Supports Java 6+.

##### Maven usage:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.10.1</version>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>com.hunterwb</groupId>
                        <artifactId>servicescanner</artifactId>
                        <version>0.1.3</version>
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
dependencies {
    annotationProcessor 'com.hunterwb:servicescanner:0.1.3'
}

compileJava {
    options.compilerArgs.add('-Aservices=com.example.Service1,com.example.Service2')
}
```
