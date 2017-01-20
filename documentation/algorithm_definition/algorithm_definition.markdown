JavaPS Documentation - Adding new Processes/Algorithms
======================================================

[Jump back to main JavaPS documentation page](../JavaPS_Documentation.markdown)

#### Table of Contents

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [How to add new Processes/Algorithms](#how-to-add-new-processesalgorithms)
  - [Implementing the Algorithm Interface - Define Algorithms by extending ***AbstractAlgorithm***](#implementing-the-algorithm-interface---define-algorithms-by-extending-abstractalgorithm)
  - [Annotated Algorithms - Define Algorithms based on Java Annotations](#annotated-algorithms---define-algorithms-based-on-java-annotations)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

How to add new Processes/Algorithms
-----------------------------------

This page explains how new custom processes can be added to **JavaPS**. As described in the [Architectural Details Page](../architecture/architecture.markdown), processes are implementations of the interface ***IAlgorithm***, which prescribes an *execute()* method and a ***TypedProcessDescription***, the Java representation of a process description. The abstract component ***AbstractAlgorithm*** implements the interface. To add new ***Algorithms***, **JavaPS** offers two distinct ways. First, ***AbstractAlgorithm*** can be extended and the required methods be implemented. Another possibility is to define an arbitrary Java class with arbitrary methods and use appropriate *Java annotations* to mark key methods as the required implementations. Subsequently, both options are demonstrated.

### Implementing the Algorithm Interface - Define Algorithms by extending ***AbstractAlgorithm***
**TODO** if possible, provide example and explanation

### Annotated Algorithms - Define Algorithms based on Java Annotations

Instead of directly deriving new Java classes by extending ***AbstractAlgorithm***, **JavaPS** offers a more elegant way of adding new algorithms. Through suitable *Java annotations*, any Java class may become an ***Algorithm*** that is found at application start-up by **JavaPS**. This functionality is provided by component ***AnnotatedAlgorithm***, which extends ***AbstractAlgorithm*** and thus provides a full implementation of the requirements (*process description* and *execute()* method). The necessary information is automatically parsed from the *annotations*.

The following example demonstrates the definition of an *annotated algorithm*:

```
package org.n52.javaps.service;

//import statements omitted

@Algorithm(version = "1.0.0")
public class TestAlgorithm {
    private String input1;
    private String input2;
    private String output1;
    private String output2;

    @LiteralInput(identifier = "input1")
    public void setInput1(String value) {
         this.input1 = value;
    }

    @LiteralInput(identifier = "input2")
    public void setInput2(String value) {
         this.input2 = value;
    }

    @Execute
    public void execute() {
         this.output1 = input1;
         this.output2 = input2;
    }

    @LiteralOutput(identifier = "output1")
    public String getOutput1() {
         return this.output1;
    }

    @LiteralOutput(identifier = "output2")
    public String getOutput2() {
         return this.output2;
    }

}
```

Basically, the Java class is annotated with `@Algorithm` to tell **JavaPS**, that this class is an ***Algorithm***. If not specified otherwise, the *fully qualified class name* is used as *process identifier* (in this case the algorithm is referenced by identifier "org.n52.javaps.service.TestAlgorithm").

The annotation `@Algorithm` comprises the following additional properties:

-	String **identifier**: specifies the unique *job identifier* of the process; if not set, the *fully qualified class name* (Java package and class name) is used
-	String **title**: the title of the process; can be chosen arbitrarily; if not set, the *fully qualified class name* (Java package and class name) is used
-	String **abstrakt**: a description of the process to let others know what it can do and how to serve it
-	String **version**: the version of the process
-	Boolean **storeSupported**: *true*, if the process allows persistence of the results; *true* per default
-	Boolean **statusSupported**: *true*, if the process supports status; *true* per default

Typically, a process has one or more *inputs* and produces one or more *outputs*. Consequently, the *in-* and *outputs* can be defined via appropriate annotations as well. E.g., the annotation `@LiteralInput(identifier = "X")` marks a Java method that sets the literal input with identifier "X". The input itself is stored as a Java property, which is set by the annotated method when parsing the request and instantiating the ***Algorithm***. However, as of January 2017, only *literal* and *complex inputs* can be defined within **annotated algorithms**. Support for *bounding box inputs* is only available through classical class extension of ***AbstractAlgorithm***. Hence, beneath `@LiteralIntput`, an *input* may also be typed as `@ComplexInput`. In the same way, any *output* may be defined by annotating a suitable method with `@LiteralOutput` or `@ComplexOutput`. Similar to `@Algorithm` the *in-* and *output* definitions provide additional properties to specify details. Subsequently, these properties are explained:

Annotation `@LiteralInput` Properties

-	String **identifier**: specifies the unique *input identifier* of the input
-	String **title**: the title of the input; can be chosen arbitrarily
-	String **abstrakt**: a description of the input
-	long **minOccurs**: the minimum number of occurrences within an *Execute* request; default value is "1"
-	long **maxOccurs**: the maximum number of occurrences within an *Execute* request; default value is "1"
-	String **defaultValue**: a default value that is used when the input is not specified within an *Execute* request
-	String[] **allowedValues**: an array of concrete allowed values for the input;
-	String **uom**: specification of the *unit of measure*
-	Class **binding**: reference to a *binding* class that implements/extends *LiteralType.class* and thus is able to parse the input from an *Execute* request correctly; basically this *binding* component acts as a wrapper for the input

Annotation `@ComplexInput` Properties

-	String **identifier**: specifies the unique *input identifier* of the input
-	String **title**: the title of the input; can be chosen arbitrarily
-	String **abstrakt**: a description of the input
-	long **minOccurs**: the minimum number of occurrences within an *Execute* request; default value is "1"
-	long **maxOccurs**: the maximum number of occurrences within an *Execute* request; default value is "1"
-	long **maximumMegaBytes**: a limitation of the maximum size of the complex input's payload
-	Class **binding**: reference to a *binding* class that implements/extends *ComplexData.class* and thus is able to parse the complex input from an *Execute* request correctly; basically this *binding* component acts as a wrapper for the input

Annotation `@LiteralOutput` Properties

-	String **identifier**: specifies the unique *output identifier* of the output
-	String **title**: the title of the output; can be chosen arbitrarily
-	String **abstrakt**: a description of the output
-	String **uom**: specification of the *unit of measure*
-	Class **binding**: reference to a *binding* class that implements/extends *LiteralType.class* and thus is able to encode the output correctly; basically this *binding* component acts as a wrapper for the output

Annotation `@ComplexOutput` Properties

-	String **identifier**: specifies the unique *output identifier* of the output
-	String **title**: the title of the output; can be chosen arbitrarily
-	String **abstrakt**: a description of the output
-	Class **binding**: reference to a *binding* class that implements/extends *ComplexData.class* and thus is able to encode the output correctly; basically this *binding* component acts as a wrapper for the output

After specifying the *in-* and *outputs* of the process, only one mandatory annotation/specification is required. Per ***Algorithm*** one method has to exist that is annotated by `@Execute`. This method is called when the process is executed and hence its method body has to compute its *result(s)/output(s)*.

Concluding, the whole definition of an ***Algorithm*** can be specified through the appropriate annotations on a Java class. On start-up, the application automatically scans Java resources for classes annotated with `@Algorithm` and thus makes them operatable. If necessary, new implementations of ***LiteralType*** or ***ComplexData*** may be provided alongside to correctly parse and encode the in- and outputs of the process.

In theory, new implementations may be added within the packages of **JavaPS** directly. However, it is recommended to outsource the ***Algorithm*** implementations as **External Processing/Algorithm Repository**, e.g. as stand-alone [Maven](https://maven.apache.org/)-project that encapsulates the definitions of all algorithms and necessary *binding* implementations of ***LiteralType*** and ***ComplexData*** for proper de- and encoding.

[Jump back to main JavaPS documentation page](../JavaPS_Documentation.markdown)
