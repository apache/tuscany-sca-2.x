mvn gpg:sign-and-deploy-file -DgroupId=com.google.gdata -DartifactId=gdata-core -Dversion=1.0 -Dpackaging=jar -Dfile=gdata-core-1.0.jar -DrepositoryId=apache.people -Durl=scp://people.apache.org/home/lresende/public_html/googoe-gdata/maven -Dkeyname=lresende 

mvn gpg:sign-and-deploy-file -DgroupId=com.google.gdata -DartifactId=gdata-client -Dversion=1.0 -Dpackaging=jar -Dfile=gdata-client-1.0.jar -DrepositoryId=apache.people -Durl=scp://people.apache.org/home/lresende/public_html/google-gdata/maven -Dkeyname=lresende
