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

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

import static org.ops4j.pax.exam.CoreOptions.equinox;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

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

@RunWith( JUnit4TestRunner.class )
public class HelloworldTestCase {
    
    public static final long DEFAULT_TIMEOUT = 30000;
    
    @Inject
    protected BundleContext bundleContext;
    
    @Configuration
    public static Option[] configuration() {
      Option[] options = options(
          mavenBundle("org.ops4j.pax.logging", "pax-logging-api"),
          mavenBundle("org.ops4j.pax.logging", "pax-logging-service"),
          systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("INFO"),
          
          mavenBundle("org.apache.felix", "org.apache.felix.configadmin"),
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
          
          mavenBundle("org.apache.tuscany.sca", "bundle"),          
      
          /* For debugging, uncomment the next two lines 
          vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=7777"),
          waitForFrameworkStartup(),
          */
          
          equinox().version("3.5.0")); 
      
      return options;
    }    

    @Test
    public void testSayHello() {
        System.out.println("testSayHello - start");
        
        // get the OBR repository admin service
        RepositoryAdmin respositoryAdminService = getOsgiService(RepositoryAdmin.class);
        
        // print currently registered repos
        Repository[] repos = respositoryAdminService.listRepositories();
        System.out.println("initial repos");
        for (Repository repo : repos) {
          System.out.println("Repo >>> " + repo.getName());
        }
       
        // get the repository generator service
        System.out.println("XXXXXX get RepositoryGenerator service =");
        RepositoryGenerator repositoryGenerator = getOsgiService(RepositoryGenerator.class);
        System.out.println("XXXXXX" + repositoryGenerator);
                
        System.out.println("XXXXXX get ModelledResourceManager service =");
        ModelledResourceManager modelledResourceManager = getOsgiService(ModelledResourceManager.class);
        System.out.println("XXXXXX" + modelledResourceManager);
        
        Set<ModelledResource> mrs = new HashSet<ModelledResource>();
        
        try {
            File bundleFile = new File("D:/sca-java-2.x/distribution/all/target/modules/tuscany-assembly-2.0-SNAPSHOT.jar");
            IDirectory jarDir = FileSystem.getFSRoot(bundleFile);
            mrs.add(modelledResourceManager.getModelledResource(bundleFile.toURI().toString(), jarDir));
            File outFile = new File("D://sca-java-2.x//unreleased//testing//itest//bundle//target//myrepository.xml");
            FileOutputStream fout = new FileOutputStream(outFile);
            repositoryGenerator.generateRepository("Test repo description", mrs, fout);
            fout.close();
        
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        
        //repositoryAdmin.addRepository(new File("repository.xml").toURI().toURL());
        
        System.out.println("testSayHello - end");
    }
    
    private <T> T getOsgiService(Class<T> type) {
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
    
    private static MavenArtifactProvisionOption mavenBundle(String groupId, String artifactId) {
      return CoreOptions.mavenBundle().groupId(groupId).artifactId(artifactId).versionAsInProject();
    }    
}


