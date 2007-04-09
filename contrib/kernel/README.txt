This Release of the Apache Tuscany SCA Kernel is designed as a baseline for extension development 
and early access to the Java programming model based on the SCA 1.0 Java Common Annotations
and API Specification and the Java Component Implementation Specification. Copies of the
specifications may be obtained from http://www.osoa.org.
  
*** Please Note that extension SPIs are subject to change in future alpha and beta releases *** 

Release Features
----------------
A goal of the release has been closer alignment with the SCA specifications, and in particular,
the implementation of new Java annotations and APIs introduced in the 1.0 version.
New features include:

- Support for full composite recursion (N-levels)
- Improved and simplified non-blocking operations
- Support for SCA 1.0 conversational callbacks, including synchronous operations 
- Support for many of the SCA 1.0 Annotations and APIs, including: scopes,
  conversational annotations, ComponentContext, callback annotations,
- Support for SCA 1.0 autowire 
- Support for service target syntax
- Support for reference promotion syntax (multiple level promotion is not yet implemented
  but planned for the next release)
- Support for exception formatting
- Improved exception handling
- Reduced disk and memory footprint


This release also includes significant architectural enhancements and refactors.
Much of this work has been focused on simplification of the core architecture.
In addition, changes have been introduced to support federated service networks
and multi-VM wiring. It is expected that those features will be added in the next alpha release.

- Greatly simplified wiring architecture, including elimination of inbound and outbound wiring 
- Support for sparse component trees distributed across multiple-VMs
- Improved autowire support including changes to enable federated autowire in the next release 
- Introduction of a ComponentManager 
- Refactored Connector
- Refactored Component interface and hierarchy 
- Increased test coverage (~70%)
- An early implementation of federated assembly changeset marshalling 


In bocca al lupo!
The Tuscany Team
