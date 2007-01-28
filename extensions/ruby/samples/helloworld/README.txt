Helloworld Ruby Sample
======================

This sample illustrates the use of an extension to support other implementation types.
In this sample the component is implemented using a script written in the Ruby language.

Building
--------

To build and install the sample using Maven use:
$ mvn install

This will build the sample, package a JAR file for the composite and install it in your
local maven repository for use by other samples.

Running
-------

To unpack the distribution to run the sample use:
$ mvn dependency:unpack

The 1.0-incubator-M2 distribution will be unpacked to the target/distribution directory.

To configure the extension, copy its jar file into the extensions directory.
$ cp target/distribution/contrib/ruby-1.0-incubator-M2.jar target/distribution/extensions/.

You can then run the sample using the launcher:
$ java -jar target/distribution/bin/launcher.jar target/sample-helloworld-ruby.jar

Modifying
---------

The source code for the sample is in the src/main/java directory.
The XML for the SCA composite is in src/main/resouces/META-INF/sca/default.scdl