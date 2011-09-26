Uses the domain.properties file to show explicitly defining a contrbutions dependent contribution URIS.
  
Contributions export1.jar and export2.jar both export the Java package "sample" and both have
a class sample.HelloworldImpl but the classes return a sayHello string "Hello 1" in export1.jar 
and "Hello 2" in export2.jar.

Contributions import1.jar and import2.jar both import the package sample and use the 
sample.HelloworldImpl class in the component implementation.

The *.dependencies files explicitly set the dependency URIs used by 
Contributions import1.jar and import2.jar, without the explicit property the imports would just t
use the first contribution found that exports the sample package.

See section 10.2.1 and 10.4 in the Assembly spec.  
