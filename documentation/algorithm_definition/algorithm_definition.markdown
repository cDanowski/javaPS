JavaPS Documentation - Adding new Processes/Algorithms
======================================================

[Jump back to main JavaPS documentation page](../JavaPS_Documentation.markdown)

#### Table of Contents

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [How to add custom Processes/Algorithms to JavaPS](#how-to-add-custom-processesalgorithms-to-javaps)
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
- [Creating an External Processing Repository (EPR)](#creating-an-external-processing-repository-epr)
  - [Introduction - What is an EPR?](#introduction---what-is-an-epr)
  - [Benefits of using an EPR](#benefits-of-using-an-epr)
  - [Contents of an EPR - How to write/create an EPR for JavaPS](#contents-of-an-epr---how-to-writecreate-an-epr-for-javaps)
    - [Project Structure of exemplar "javaps-jts-backend" Algorithm Repository](#project-structure-of-exemplar-javaps-jts-backend-algorithm-repository)
    - [Java resources - Exemplar Algorithm/Process Definition of "JTSConvexHullAlgorithm"](#java-resources---exemplar-algorithmprocess-definition-of-jtsconvexhullalgorithm)
    - [Registration of an EPR within JavaPS via Maven and Spring configuration](#registration-of-an-epr-within-javaps-via-maven-and-spring-configuration)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

This page aims to guide developers on how to create and add new custom processes to **JavaPS**. The introduction is split into two main parts. First, the creation of [custom algorithms/processes through Java annotations](#custom-algorithm-definitions-through-java-annotations) is focused. Second, the guide provides recommendations on how to bundle custom *algorithm* definitions within a so-called [External Processing Repository (EPR)](#creating-an-external-processing-repository-epr). In general, it takes more than writing a single Java class to create a new custom *algorithm/process*, as a developer also has to include definitions of appropriate *bindings* or *data handling* components assisting in de-/encoding and processing *in-* and *output data*. Here, an **EPR** may collect all relevant Java components within a stand-alone Maven project, which is separated from JavaPS. Registration of its components is accomplished via Maven and Spring configuration, as explained in section [Registration of an EPR within JavaPS via Maven and Spring configuration](#registration-of-an-epr-within-javaps-via-maven-and-spring-configuration).

How to add custom Processes/Algorithms to JavaPS
------------------------------------------------

### Custom Algorithm Definitions through Java Annotations

As described in the [Architectural Details Page](../architecture/architecture.markdown), processes are implementations of the interface ***IAlgorithm***, which prescribes an *execute()* method and a ***TypedProcessDescription***, the Java representation of a process description. The abstract component ***AbstractAlgorithm*** implements the interface and thus provides the rudimentary skeleton for any ***Algorithm*** implementation. However, algorithm developers do not have to extend ***AbstractAlgorithm*** themselves. Instead, **JavaPS** offers a more elegant and simple way of implementing custom ***Algorithms***. Through suitable *Java annotations*, any Java class may become an ***Algorithm*** that is found at application start-up by **JavaPS**. This functionality is provided by component ***AnnotatedAlgorithm***, which extends ***AbstractAlgorithm*** and thus provides a full implementation of the requirements (*process description* and *execute()* method). The necessary information is automatically parsed and derived from the *annotations*.

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

The annotation `@Algorithm` marks a Java class comprises the following additional properties:

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

In theory, new implementations may be added within the packages of **JavaPS** directly. However, it is recommended to outsource the ***Algorithm*** implementations into a so-called **External Processing/Algorithm Repository**, e.g. as stand-alone [Maven](https://maven.apache.org/) project that encapsulates the definitions of all algorithms and necessary *binding* implementations of ***LiteralType*** and ***ComplexData*** for proper de- and encoding. Hints and useful information for creating such an external repository are presented below.

Creating an External Processing Repository (EPR)
------------------------------------------------

### Introduction - What is an EPR?

An **External Processing Repository (EPR)** bundles *algorithm/process* definitions for **JavaPS** in a separate Maven project including necessary *binding* implementations. Within this section, the [benefits](#benefits-of-using-an-epr) and hints for the [creation and contents](#contents-of-an-epr---how-to-writecreate-an-epr-for-javaps) of such an **EPR** are presented.

### Benefits of using an EPR

Possible benefits are the clear separation of concerns, as the WPS *process implementations* are detached from the actual WPS *infrastructure implementation*. Hence, both components can be maintained and developed independent from each other. In addition, by organizing *process implementations* in external repositories, each repository may include only ***Algorithms*** within a certain application context (e.g. one repository for geographic/geometric analysis, another for data/format conversions). By docking one or more such thematically specialized repositories on to **JavaPS** (through suitable Maven and Spring configuration), subject-oriented WPS instances can be set-up.

### Contents of an EPR - How to write/create an EPR for JavaPS

The following sub-sections concentrate on the required contents of an **EPR** and provide developers with a guide on how to create their own *processing repository*.

An exemplar **EPR** for **JavaPS** is the 52&deg;North GitHub-project [javaps-jts-backend](https://github.com/52North/javaps-jts-backend). It comprises a single additional *algorithm/process* definition and required other Java components as well as configuration files. JTS stands for [Java Topology Suite](https://live.osgeo.org/en/overview/jts_overview.html), an open source Java library providing spatial processing and analysis functionalities for 2D linear geometries. This project is used as an example to highlight the assets of an **EPR** subsequently.

In general, an **EPR** adds the following components to the basic **JavaPS** WPS infrastructure:

-	**Algorithm** definitions through annotated Java classes as described above.
-	**Binding** implementations for process *in-* and *outputs*.
-	**In- and Output Handlers**: Together with an associated **Binding** these handlers provide serviceable data required by the associated **algorithm/process**. This is relevant in the context of **data representation** and its **de-** and **encoding**. E.g., while in a WPS **Execute** request, input geometries might be encoded using *Well-Known-Text(WKT)* format, an internal **algorithm/process** within **javaps-jts-backend** may require them as proper *JTS Java objects*. Furthermore, while the process may use these *JTS inputs* to compute certain *JTS output objects*, the **Execute** response object that is returned to the client could display them as *WKT* again (to be precise, the user submits the desired in- and output format within request parameters). In conclusion, **In- and Output Handlers** combined with suitable **Binding** implementations take care of proper format transformations between different internal and external representations of process in- and outputs. Section [Java resources - Exemplar Algorithm/Process Definition of "JTSConvexHullAlgorithm"](#java-resources---exemplar-algorithmprocess-definition-of-jtsconvexhullalgorithm) explains their coexistence in more detail.

The presentation of the exemplar [javaps-jts-backend](https://github.com/52North/javaps-jts-backend) is divided into three parts. First, its [project structure](#project-structure-of-exemplar-javaps-jts-backend-algorithm-repository) is shown followed by an explanation of the required [Java resources](#java-resources---exemplar-algorithmprocess-definition-of-jtsconvexhullalgorithm) and finally ends with the necessary [Maven and Spring configuration](#registration-of-an-epr-within-javaps-via-maven-and-spring-configuration).

#### Project Structure of exemplar "javaps-jts-backend" Algorithm Repository

The [Maven](https://maven.apache.org/) project [javaps-jts-backend](https://github.com/52North/javaps-jts-backend) is organized as follows. Note that the subsequent project outline only comprises the folders and files related to Maven and the algorithm implementation. Other files, such as GitHub README or licensing information files are neglected and can be inspected on the [repository on GitHub](https://github.com/52North/javaps-jts-backend).

<pre>
<i>javaps-jts-backend</i>
        |
        | - <b>"pom.xml"</b>
        | - <i>src</i>
             |
             | - <i>main</i>
                  |
                  | - <i>config</i>
                        |
                        | - ... // additional config file(s), omitted
                  | - <i>java</i>
                        |
                        | - <i>org/n52/geoprocessing/jts</i>
                                |
                                | - <i>algorithm</i>
                                      |
                                      | <b>"JTSConvexHullAlgorithm.java"</b>
                                | - <i>io</i>
                                      |
                                      | - <i>data/binding/complex</i>
                                            |
                                            | <b>"JTSGeometryBinding.java"</b>
                                      | - <i>datahandler</i>
                                            |
                                            | - <i>generator</i>
                                                  |
                                                  | - <b>"WKTGenerator.java"</b>
                                            | - <i>parser</i>
                                                  |
                                                  | - <b>"WKTParser.java"</b>
                  | - <i>resources</i>
                        |
                        | - <b>"wkt.properties"</b>   
                        | - <i>components</i>
                                |
                                | - <b>"jts-backend.xml"</b>
             | - <i>test</i>
                  |
                  | - ... // omitted
</pre>

The general folder structure is predefined by Maven. The project's root tier contains the **pom.xml** file, which comprises the necessary information regarding dependencies and project building. While Java classes are included in `src/main/java` relevant resources are located in `src/main/resources`. `src/test/java` and `src/test/resources` complete Mavens project structure and comprise test cases for [JUnit](http://junit.org/junit4/) tests. In addition to this mandatory structure, the folder `src/main/config` comprises relevant configuration files, e.g. containing information related to the Maven build process. Within the scope of this documentation, only the required steps to produce a valid *processing backend/repository* for **JavaPS** are presented. Thus, the following sections focus only on the necessary Java definitions within `src/main/java` and resource files within `src/main/resources`.

While new **algorithms/processes** and related *binding* and *data handling* components are defined through appropriate [Java implementaions](#java-resources---exemplar-algorithmprocess-definition-of-jtsconvexhullalgorithm)) under `src/main/java`, `src/main/resources` comprises an [XML Spring configuration file](#registration-of-an-epr-within-javaps-via-maven-and-spring-configuration) including bean definitions of the implemented Java classes is required to register those components within **JavaPS**. Both aspects are described in more detail below.

#### Java resources - Exemplar Algorithm/Process Definition of "JTSConvexHullAlgorithm"

Overview of Java related resources:

<pre>
<i>javaps-jts-backend</i>
        |
        | - <b>"pom.xml"</b>
        | - <i>src</i>
             |
             | - <i>main</i>
                  |
                  | - <i>java</i>
                        |
                        | - <i>org/n52/geoprocessing/jts</i>
                                |
                                | - <i>algorithm</i>
                                      |
                                      | <b>"JTSConvexHullAlgorithm.java"</b>
                                | - <i>io</i>
                                      |
                                      | - <i>data/binding/complex</i>
                                            |
                                            | <b>"JTSGeometryBinding.java"</b>
                                      | - <i>datahandler</i>
                                            |
                                            | - <i>generator</i>
                                                  |
                                                  | - <b>"WKTGenerator.java"</b>
                                            | - <i>parser</i>
                                                  |
                                                  | - <b>"WKTParser.java"</b>
</pre>

The **algorithm/process** and related *binding* and *data handling* components are defined in the sub-packages of `src/main/java/`. To be precise, an implementation of a convex hull algorithm is provided in `org/n52/geoprocessing/jts/algorithm/JTSConvexHullAlgorithm.java`.

text about algorithm definition, in- and output bindings and implementations of ***InputOutputHandler*** (WktGenerator and WktParser)

highlight the cooperation of ***InputOutputHandler*** and *binding* implementations regarding the parsing and generation of serviceable data for the process.

Extending subclasses of AbstractGenerator shall provide functionality to generate serviceable output data for the processes offered by the 52N WPS framework. public abstract class AbstractInputOutputHandler implements InputOutputHandler

#### Registration of an EPR within JavaPS via Maven and Spring configuration

information on registering algorithms, bindings, and InputOutputHandlers via Maven and Spring to make them usable within **JavaPS**.

question: Maven configuration: declare EPR as dependency of JavaPS and thats it? Spring configuration file will then be found when it is located in *src/main/resources/components/* (according to the definitions within JavaPS's pom.xml --> it is configured to load all resources from src/main/resources/) --> this file has to contain all bean definitions including algorithms and data handlers!

wkt.Properties file for WktParser and WktGenerator

[Jump back to main JavaPS documentation page](../JavaPS_Documentation.markdown)
