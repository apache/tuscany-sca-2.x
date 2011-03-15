/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package sample;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import static org.ops4j.pax.exam.CoreOptions.equinox;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.workingDirectory;

import org.apache.aries.application.filesystem.IDirectory;
import org.apache.aries.application.management.spi.repository.RepositoryGenerator;
import org.apache.aries.application.modelling.ModelledResourceManager;
import org.apache.aries.application.modelling.ModelledResource;
import org.apache.aries.application.utils.filesystem.FileSystem;

import org.apache.felix.bundlerepository.Repository;
import org.apache.felix.bundlerepository.RepositoryAdmin;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.container.def.options.WorkingDirectoryOption;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/* For debugging, uncomment the next two lines and add these imports:
 */
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;


/**
 * Note that much of the technical detail/instruction comes from...
 * 
 * http://svn.apache.org/repos/asf/aries/trunk/application/application-itests/src/test/java/org/apache/aries/application/runtime/itests/OBRResolverTest.java
 *
 */

//@RunWith( JUnit4TestRunner.class )
public class TestBaseClass {
    
    public static final long DEFAULT_TIMEOUT = 30000;
    
    @Inject
    protected BundleContext bundleContext;
    
    @Configuration
    public static Option[] configuration() {
      Option[] options = options(
          mavenBundle("org.ops4j.pax.logging", "pax-logging-api"),
          mavenBundle("org.ops4j.pax.logging", "pax-logging-service"),
          systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("INFO"),
          
          mavenBundle("org.ops4j.pax.url", "pax-url-mvn"),
         
          mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint"),
          mavenBundle("asm", "asm-all"),
          mavenBundle("org.apache.aries.proxy", "org.apache.aries.proxy"),
          mavenBundle("org.apache.aries", "org.apache.aries.util"),
        
          mavenBundle("org.apache.aries.application", "org.apache.aries.application.api"),
          mavenBundle("org.apache.aries.application", "org.apache.aries.application.utils"),
          mavenBundle("org.apache.aries.application", "org.apache.aries.application.modeller"),
          mavenBundle("org.apache.aries.application", "org.apache.aries.application.default.local.platform"),
          mavenBundle("org.apache.felix", "org.apache.felix.bundlerepository"),          
          mavenBundle("org.apache.aries.application", "org.apache.aries.application.resolver.obr"),
          mavenBundle("org.apache.aries.application", "org.apache.aries.application.deployment.management"),
          mavenBundle("org.apache.aries.application", "org.apache.aries.application.management"),
          mavenBundle("org.apache.aries.application", "org.apache.aries.application.runtime"),
          mavenBundle("org.apache.aries.application", "org.apache.aries.application.runtime.itest.interfaces"),
          mavenBundle("org.osgi", "org.osgi.compendium"),
          mavenBundle("org.apache.aries.testsupport", "org.apache.aries.testsupport.unit"),          
          
          mavenBundle("org.apache.tuscany.sca", "tuscany-sca-api"),
          
          //mavenBundle("org.apache.tuscany.sca", "bundle"),          
      
          /* For debugging, uncomment the next two lines 
          vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=7777"),
          waitForFrameworkStartup(),
          */
          
          workingDirectory("D://sca-java-2.x//unreleased//distribution//osgi//target"),
          
          equinox().version("3.5.0")); 
      
      return options;
    }    

    public void generateRepositoryXML(String featureName) {
        System.out.println("generateRepositoryXML - start");
        System.out.println("test dir = " + System.getProperty("user.dir"));
        System.out.println("current dir = " + this.getClass().getProtectionDomain().getCodeSource().getLocation());
        
        // get the OBR repository admin service
        RepositoryAdmin respositoryAdminService = getOsgiService(RepositoryAdmin.class);
       
        // get the repository generator service
        System.out.println("get RepositoryGenerator service =");
        RepositoryGenerator repositoryGenerator = getOsgiService(RepositoryGenerator.class);
        System.out.println(repositoryGenerator);
                
        System.out.println("get ModelledResourceManager service =");
        ModelledResourceManager modelledResourceManager = getOsgiService(ModelledResourceManager.class);
        System.out.println(modelledResourceManager);
        
        Set<ModelledResource> mrs = new HashSet<ModelledResource>();
        
        try {
            // create mrs based on base runtime
            // directories relative to working directory
            populateMRS(modelledResourceManager,
                        mrs, 
                        "../../../../distribution/all/target/features/" + featureName + "/which-jars", 
                        "../../../../distribution/all/target/modules");
            FileOutputStream fout = new FileOutputStream("./features/" +featureName + "/repository.xml");
            repositoryGenerator.generateRepository(featureName + " repository", mrs, fout);
            fout.close();
        
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        System.out.println("generateRepositoryXML - end");
    }    
    
    public void populateMRS(ModelledResourceManager modelledResourceManager,
                             Set<ModelledResource> mrs, 
                             String whichJars, 
                             String modules){
        try {
            BufferedReader in = new BufferedReader(new FileReader(whichJars));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.endsWith(".jar")){
                    int dirSeparatorIndex = line.indexOf("/");
                    if (dirSeparatorIndex > 0){
                        line = line.substring(0, dirSeparatorIndex);
                    }
                    System.out.println("Processing - " + line);
                    File bundleFile = new File(modules + "\\" + line);
                    IDirectory jarDir = FileSystem.getFSRoot(bundleFile);
                    mrs.add(modelledResourceManager.getModelledResource(bundleFile.toURI().toString(), jarDir));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public <T> T getOsgiService(Class<T> type) {
        try {
            String filterString = "(" + Constants.OBJECTCLASS + "=" + type.getName() + ")";     
            Filter osgiFilter = FrameworkUtil.createFilter(filterString);
            ServiceTracker tracker = new ServiceTracker(bundleContext, osgiFilter, null);
            tracker.open();
            Object tmp =  tracker.waitForService(DEFAULT_TIMEOUT);
            return type.cast(tmp);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }            
    }    
    
    public static MavenArtifactProvisionOption mavenBundle(String groupId, String artifactId) {
      return CoreOptions.mavenBundle().groupId(groupId).artifactId(artifactId).versionAsInProject();
    }    
}


