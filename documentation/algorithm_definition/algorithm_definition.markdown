JavaPS Documentation - Adding new Processes/Algorithms
======================================================

[Jump back to main JavaPS documentation page](../JavaPS_Documentation.markdown)

#### Table of Contents

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [How to add new Processes/Algorithms](#how-to-add-new-processesalgorithms)
  - [Custom Algorithm Definitions through Java Annotations](#custom-algorithm-definitions-through-java-annotations)
  - [Class Annotation **@Algorithm**](#class-annotation-algorithm)
  - [Annotations for the Definition of *Process Inputs*](#annotations-for-the-definition-of-process-inputs)
    - [Setter Annotation **@LiteralInput**](#setter-annotation-literalinput)
    - [Setter Annotation **@ComplexInput**](#setter-annotation-complexinput)
  - [Annotations for the Definition of *Process Outputs*](#annotations-for-the-definition-of-process-outputs)
    - [Setter Annotation **@LiteralOutput**](#setter-annotation-literaloutput)
    - [Setter Annotation **@ComplexOutput**](#setter-annotation-complexoutput)
  - [Annotation **@Execute**](#annotation-execute)
  - [Conclusion and Recommendation for an **External Processing Repository**](#conclusion-and-recommendation-for-an-external-processing-repository)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

How to add new Processes/Algorithms
-----------------------------------

### Custom Algorithm Definitions through Java Annotations

This page aims to guide developers on how to create and add new custom processes to **JavaPS**. As described in the [Architectural Details Page](../architecture/architecture.markdown), processes are implementations of the interface ***IAlgorithm***, which prescribes an *execute()* method and a ***TypedProcessDescription***, the Java representation of a process description. The abstract component ***AbstractAlgorithm*** implements the interface and thus provides the rudimentary skeleton for any ***Algorithm*** implementation. However, algorithm developers do not have to extend ***AbstractAlgorithm*** themselves. Instead, **JavaPS** offers a more elegant and simple way of implementing custom ***Algorithms***. Through suitable *Java annotations*, any Java class may become an ***Algorithm*** that is found at application start-up by **JavaPS**. This functionality is provided by component ***AnnotatedAlgorithm***, which extends ***AbstractAlgorithm*** and thus provides a full implementation of the requirements (*process description* and *execute()* method). The necessary information is automatically parsed and derived from the *annotations*.

The following example demonstrates the definition of an *annotated algorithm*. The exemplar *"TestAlgorithm"* is marked as an implementation of ***IAlgorithm*** by annotating certain methods with key annotation, such as **@Algorithm**, **@LiteralInput**, **@LiteralOutput** and **@Execute**.

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

Basically, the Java class is annotated with `@Algorithm` to tell **JavaPS**, that this class is an ***Algorithm***. During initialization, **JavaPS** scans through available resources to find all classes marked with `@Algorithm` and uses the remaining annotated elements to automatically derive the associated ***ProcessDescription***. In consequence, certain annotations are expected and investigated in order to create the components of the ***ProcessDescription***, such as a single mandatory ***@Excecute*** annotation to declare the functional behaviour of the ***Algorithm*** or an arbitrary number of process *input* and *output* definitions. Each of the relevant annotations is described in detail below.

### Class Annotation **@Algorithm**

The annotation `@Algorithm` marks a Kava class comprises the following additional properties:

-	String **identifier**: specifies the unique *job identifier* of the process; if not set, the *fully qualified class name* (Java package and class name) is used
-	String **title**: the title of the process; can be chosen arbitrarily; if not set, the *fully qualified class name* (Java package and class name) is used
-	String **abstrakt**: a description of the process to let others know what it can do and how to serve it
-	String **version**: the version of the process
-	Boolean **storeSupported**: *true*, if the process allows persistence of the results; *true* per default
-	Boolean **statusSupported**: *true*, if the process supports status; *true* per default

### Annotations for the Definition of *Process Inputs*

again say, that currently no BBOX is supported

Typically, a process has one or more *inputs* and produces one or more *outputs*. Consequently, the *in-* and *outputs* can be defined via appropriate annotations as well. E.g., the annotation `@LiteralInput(identifier = "X")` marks a Java *setter-method* that sets the literal input with identifier "X". The input itself is stored as a Java class property, which is set by the annotated method when parsing the request and instantiating the ***Algorithm***. However, as of January 2017, only *literal* and *complex inputs* can be defined within **annotated algorithms**. Support for *bounding box inputs* is not yet implemented. To define a *complex input*, an *input* *setter-method* may be annotated with `@ComplexInput`. In the same way, any *output* may be specified by annotating a suitable *getter-method* with `@LiteralOutput` or `@ComplexOutput`. Similar to `@Algorithm` the *in-* and *output* definitions provide additional properties to specify details. Subsequently, these properties are explained:

#### Setter Annotation **@LiteralInput**

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

#### Setter Annotation **@ComplexInput**

Annotation `@ComplexInput` Properties

-	String **identifier**: specifies the unique *input identifier* of the input
-	String **title**: the title of the input; can be chosen arbitrarily
-	String **abstrakt**: a description of the input
-	long **minOccurs**: the minimum number of occurrences within an *Execute* request; default value is "1"
-	long **maxOccurs**: the maximum number of occurrences within an *Execute* request; default value is "1"
-	long **maximumMegaBytes**: a limitation of the maximum size of the complex input's payload
-	Class **binding**: reference to a *binding* class that implements/extends *ComplexData.class* and thus is able to parse the complex input from an *Execute* request correctly; basically this *binding* component acts as a wrapper for the input

### Annotations for the Definition of *Process Outputs*

#### Setter Annotation **@LiteralOutput**

Annotation `@LiteralOutput` Properties

-	String **identifier**: specifies the unique *output identifier* of the output
-	String **title**: the title of the output; can be chosen arbitrarily
-	String **abstrakt**: a description of the output
-	String **uom**: specification of the *unit of measure*
-	Class **binding**: reference to a *binding* class that implements/extends *LiteralType.class* and thus is able to encode the output correctly; basically this *binding* component acts as a wrapper for the output

#### Setter Annotation **@ComplexOutput**

Annotation `@ComplexOutput` Properties

-	String **identifier**: specifies the unique *output identifier* of the output
-	String **title**: the title of the output; can be chosen arbitrarily
-	String **abstrakt**: a description of the output
-	Class **binding**: reference to a *binding* class that implements/extends *ComplexData.class* and thus is able to encode the output correctly; basically this *binding* component acts as a wrapper for the output

### Annotation **@Execute**

After specifying the *in-* and *outputs* of the process, only one mandatory annotation/specification is required. Per ***Algorithm*** one method has to exist that is annotated by `@Execute`. This method is called when the process is executed and hence its method body has to compute its *result(s)/output(s)*.

### Conclusion and Recommendation for an **External Processing Repository**

Concluding, the whole definition of an ***Algorithm*** can be specified through the appropriate Java annotations. On start-up, the application automatically scans Java resources for classes annotated with `@Algorithm` and thus makes them operatable. If necessary, new implementations of ***LiteralType*** or ***ComplexData*** (so-called *bindings*) may be provided alongside to correctly parse and encode the in- and outputs of the process.

In theory, new implementations may be added within the packages of **JavaPS** directly. However, it is recommended to outsource the ***Algorithm*** implementations as **External Processing/Algorithm Repository**, e.g. as stand-alone [Maven](https://maven.apache.org/)-project that encapsulates the definitions of all algorithms and necessary *binding* implementations of ***LiteralType*** and ***ComplexData*** for proper de- and encoding. Possible benefits are the clear separation of concerns, as the actual WPS *process implementations* are detached from the actual WPS *infrastructure implementation*. Hence, both components can be maintained and developed independent from each other. In addition, by organizing *process implementations* in external repositories, each repository may include only ***Algorithms*** within a certain application context (e.g. one repository for geographic/geometric analysis, another for data/format conversions). By docking one or more such thematically specialized repositories on to **JavaPS** (through suitable Maven and Spring configuration), subject-oriented WPS instances can be set-up.

[Jump back to main JavaPS documentation page](../JavaPS_Documentation.markdown)
