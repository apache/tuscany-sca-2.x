Apache Tuscany Base Jar
-----------------------

This jar is an agregation of the minimal set of Tuscany module jars that are required to use a Tuscany runtime. 
Included in this jar are the modules to support using the Tuscany standalone, embedded, and webapp runtimes, 
distributed domain support, SCA assembly support for contributions, composites, implementation.java, and binding.rmi.

Support for the JMS binding is also included but requires that the runtime environment supports JMS and JNDI, for example
when running within a Java EE container or by including a JMS provider (eg Apache ActiveMQ) in the runtime classpath.

This jar has the following dependencies:

- asm:asm:jar:3.1
- cglib:cglib:jar:2.2
- org.apache.ws.commons.schema:XmlSchema:jar:1.4.2
- com.hazelcast:hazelcast:jar:1.8 (optional, for distributed domain support) 

When running with less than Java 1.6 the following are also required:

- org.apache.geronimo.specs:geronimo-stax-api_1.0_spec:jar:1.0.1
- org.codehaus.woodstox:wstx-asl:jar:3.2.4
- javax.xml.bind:jaxb-api:jar:2.1
- javax.activation:activation:jar:1.1
- com.sun.xml.bind:jaxb-impl:jar:2.1.12
- javax.xml.ws:jaxws-api:jar:2.1
- javax.annotation:jsr250-api:jar:1.0
- javax.jws:jsr181-api:jar:1.0-MR1
- javax.xml.stream:stax-api:jar:1.0-2



