Rem from axis2 .95 snapshot jars create m2 repository --- temporariy till .95 goes live.
call mvn install:install-file -DgroupId=axis2 -DartifactId=axis2-core -Dversion=0.95-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -Dfile=axis2-core-0.95-SNAPSHOT.jar
call mvn install:install-file -DgroupId=axis2 -DartifactId=axis2-adb -Dversion=0.95-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -Dfile=axis2-adb-0.95-SNAPSHOT.jar
call mvn install:install-file -DgroupId=axis2 -DartifactId=axis2-addressing -Dversion=0.95-SNAPSHOT  -Dpackaging=jar -DgeneratePom=true -Dfile=axis2-addressing-0.95-SNAPSHOT.jar  
call mvn install:install-file -DgroupId=axis2 -DartifactId=axis2-codegen -Dversion=0.95-SNAPSHOT  -Dpackaging=jar -DgeneratePom=true -Dfile=axis2-codegen-0.95-SNAPSHOT.jar
call mvn install:install-file -DgroupId=axis2 -DartifactId=axis2-common -Dversion=0.95-SNAPSHOT  -Dpackaging=jar -DgeneratePom=true -Dfile=axis2-common-0.95-SNAPSHOT.jar
call mvn install:install-file -DgroupId=axis2 -DartifactId=axis2-doom -Dversion=0.95-SNAPSHOT  -Dpackaging=jar -DgeneratePom=true -Dfile=axis2-doom-0.95-SNAPSHOT.jar
call mvn install:install-file -DgroupId=axis2 -DartifactId=axis2-integration -Dversion=0.95-SNAPSHOT  -Dpackaging=jar -DgeneratePom=true -Dfile=axis2-integration-0.95-SNAPSHOT.jar
call mvn install:install-file -DgroupId=axis2 -DartifactId=axis2-saaj -Dversion=0.95-SNAPSHOT  -Dpackaging=jar -DgeneratePom=true -Dfile=axis2-saaj-0.95-SNAPSHOT.jar
call mvn install:install-file -DgroupId=axis2 -DartifactId=axis2-samples -Dversion=0.95-SNAPSHOT  -Dpackaging=jar -DgeneratePom=true -Dfile=axis2-samples-0.95-SNAPSHOT.jar
call mvn install:install-file -DgroupId=axis2 -DartifactId=axis2-security -Dversion=0.95-SNAPSHOT  -Dpackaging=jar -DgeneratePom=true -Dfile=axis2-security-0.95-SNAPSHOT.jar
call mvn install:install-file -DgroupId=axis2 -DartifactId=axis2-tools -Dversion=0.95-SNAPSHOT  -Dpackaging=jar -DgeneratePom=true -Dfile=axis2-tools-0.95-SNAPSHOT.jar
call mvn install:install-file -DgroupId=axis2 -DartifactId=axis2-wsdl -Dversion=0.95-SNAPSHOT  -Dpackaging=jar -DgeneratePom=true -Dfile=axis2-wsdl-0.95-SNAPSHOT.jar


call mvn install:install-file -DgroupId=ws-commons -DartifactId=axiom -Dversion=1.0-SNAPSHOT  -Dpackaging=jar -DgeneratePom=true -Dfile=axiom-1.0-SNAPSHOT.jar
call mvn install:install-file -DgroupId=ws-commons -DartifactId=XmlSchema -Dversion=1.0-SNAPSHOT  -Dpackaging=jar -DgeneratePom=true -Dfile=XmlSchema-1.0-SNAPSHOT.jar
call mvn install:install-file -DgroupId=ws-commons -DartifactId=policy -Dversion=0.92-SNAPSHOT  -Dpackaging=jar -DgeneratePom=true -Dfile=policy-0.92-SNAPSHOT.jar
