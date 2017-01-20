JavaPS Documentation
====================

#### Table of Contents

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [General Information](#general-information)
- [Links to Subpages of the Documentation](#links-to-subpages-of-the-documentation)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

This documentation guide provides helpful information about the **JavaPS** Java project, developed by **52°North**. This documentation is based on the project state from December 2016. The most current development of **JavaPS** is available from the [official GitHub repository](https://github.com/52North/javaps).

General Information
-------------------

This section provides general information about the *JavaPS* project, which offers a state-of-the-art implementation of an OGC [Web Processing Service (WPS)](http://www.opengeospatial.org/standards/wps) version 2.0.0.

From an architectural point of view, **JavaPS** is based on the generic [Iceland](https://github.com/52North/iceland) project, equally developed and maintained by **52°North**. **Iceland** represents a generic Java framework for OGC Web Services. Hence it offers a generic service infrastructure implementation in accordance to the [OGC Web Service Common](http://www.opengeospatial.org/standards/common) specification. On top of this generic service infrastructure, **Iceland** can be extended to implement concrete web services. As an example, **JavaPS** utilizes **Iceland** to become a well-designed Web Processing Service. For this reason, **JavaPS** provides key Java components such as *request objects*, *response objects*, *decoders*, *encoders*, *parsers* as well as other components related to OGC WPS.

Links to Subpages of the Documentation
--------------------------------------

Within this documentation, several aspects of the implementation are addressed. The documentation is split across several sub pages, each focusing a certain aspect. It comprises the following thematics:

-	notes on architectural aspects are presented within page [Architectural Details](/architecture/architecture.markdown)
-	the dynamic interplay Iceland's and JavaPS's components is focused on page [Sequence Diagrams](/sequence_diagrams/sequence_diagrams.markdown). Is makes use of UML sequence diagrams to show the method calls from ceceiving a WPS request to sending the response.
-	recommendations and explanations on how to add custom processes (as executable algorithms) to JavaPS are provided on page [How to add new Processes/Algorithms](/algorithm_definition/algorithm_definition.markdown).
