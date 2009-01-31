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

package org.apache.tuscany.sca.test.contribution;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.service.util.FileHelper;
import org.apache.tuscany.sca.contribution.service.util.IOHelper;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;

/**
 * This is more intended to be a integration test then a unit test. *
 */
public class ContributionServiceTestCase extends TestCase {
    private static final String CONTRIBUTION_001_ID = "contribution001/";
    private static final String CONTRIBUTION_002_ID = "contribution002/";
    private static final String JAR_CONTRIBUTION = "/repository/sample-calculator.jar";
    private static final String FOLDER_CONTRIBUTION = "target/classes/";

    private ClassLoader cl;
    private EmbeddedSCADomain domain;
    private ContributionService contributionService;

    /**
     * setUp() is a method in JUnit Frame Work which is executed before all others methods in the class extending
     * unit.framework.TestCase. So this method is used to create a test Embedded SCA Domain, to start the SCA Domain and
     * to get a reference to the contribution service
     */

    @Override
    protected void setUp() throws Exception {
        //Create a test embedded SCA domain
        cl = getClass().getClassLoader();
        domain = new EmbeddedSCADomain(cl, "http://localhost");

        //Start the domain
        domain.start();

        //get a reference to the contribution service
        contributionService = domain.getContributionService();
    }

    /**
     * Method prefixed with 'test' is a test method where testing logic is written using various assert methods. This
     * test verifies the string assigned to contrututionId with the value retrieved from the SCA runtime.
     */
    public void testContributeJAR() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        //URL contributionLocation = new URL("file:/D:/dev/Opensource/Apache/Tuscany/source/java/sca/samples/calculator/target/sample-calculator.jar");
        String contributionId = CONTRIBUTION_001_ID;
        contributionService.contribute(contributionId, contributionLocation, false);
        assertNotNull(contributionService.getContribution(contributionId));
    }

    /**
     * Method prefixed with 'test' is a test method where testing logic is written using various assert methods. This
     * test verifies the string assigned to contrututionId with the value retrieved from the SCA runtime using
     * contributionService.
     */
    public void testStoreContributionPackageInRepository() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        String contributionId = CONTRIBUTION_001_ID;
        contributionService.contribute(contributionId, contributionLocation, true);

        assertTrue(FileHelper.toFile(new URL(contributionService.getContribution(contributionId).getLocation()))
            .exists());

        assertNotNull(contributionId);

        Contribution contributionModel = contributionService.getContribution(contributionId);

        File contributionFile = FileHelper.toFile(new URL(contributionModel.getLocation()));
        assertTrue(contributionFile.exists());
    }

    /**
     * Method prefixed with 'test' is a test method where testing logic is written using various assert methods. This
     * test verifies the string assigned to contrututionId with the value retrieved from the SCA runtime using
     * contributionService.
     */
    public void testStoreContributionStreamInRepository() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        String contributionId = CONTRIBUTION_001_ID;

        InputStream contributionStream = contributionLocation.openStream();
        try {
            contributionService.contribute(contributionId, contributionLocation, contributionStream);
        } finally {
            IOHelper.closeQuietly(contributionStream);
        }

        assertTrue(FileHelper.toFile(new URL(contributionService.getContribution(contributionId).getLocation()))
            .exists());

        assertNotNull(contributionId);

        Contribution contributionModel = contributionService.getContribution(contributionId);

        File contributionFile = FileHelper.toFile(new URL(contributionModel.getLocation()));
        assertTrue(contributionFile.exists());
    }

    /**
     * Method prefixed with 'test' is a test method where testing logic is written using various assert methods. This
     * test verifies the string assigned to contributionId1,contributionId2 with the value retrieved from the SCA
     * runtime using contributionService.
     */
    public void testStoreDuplicatedContributionInRepository() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        String contributionId1 = CONTRIBUTION_001_ID;
        contributionService.contribute(contributionId1, contributionLocation, true);
        assertNotNull(contributionService.getContribution(contributionId1));
        String contributionId2 = CONTRIBUTION_002_ID;
        contributionService.contribute(contributionId2, contributionLocation, true);
        assertNotNull(contributionService.getContribution(contributionId2));
    }

    /**
     * Method prefixed with 'test' is a test method where testing logic is written using various assert methods. This
     * test verifies the string assigned to contributionId with the value retrieved from the SCA runtime using
     * contributionService.
     */
    public void testContributeFolder() throws Exception {
        final File rootContributionFolder = new File(FOLDER_CONTRIBUTION);
        String contributionId = CONTRIBUTION_001_ID;
        //first rename the sca-contribution metadata file 
        //File calculatorMetadataFile = new File("target/classes/calculator/sca-contribution.xml"); 
        //File metadataDirectory = new File("target/classes/META-INF/"); 
        //if (!metadataDirectory.exists()) {
        //    FileHelper.forceMkdir(metadataDirectory); 
        //}
        //FileHelper.copyFileToDirectory(calculatorMetadataFile, metadataDirectory);

        // Requires permission to read user.dir property. Requires PropertyPermision in security policy.
        URL contributionFolderURL;
        try {
            contributionFolderURL = AccessController.doPrivileged(new PrivilegedExceptionAction<URL>() {
                public URL run() throws IOException {
                    return rootContributionFolder.toURL();
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException)e.getException();
        }        
        contributionService.contribute(contributionId, contributionFolderURL, false);
        assertNotNull(contributionService.getContribution(contributionId));
    }

    /**
     * Method prefixed with 'test' is a test method where testing logic is written using various assert methods. This
     * test verifies the string assigned to contributionId, artifactId with the value retrieved from the SCA runtime
     * using contributionService.
     */
    public void testAddDeploymentComposites() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        String contributionId = CONTRIBUTION_001_ID;
        Contribution contribution = contributionService.contribute(contributionId, contributionLocation, false);
        assertNotNull(contributionService.getContribution(contributionId));

        String artifactId = "contributionComposite.composite";
        Composite composite = (new DefaultAssemblyFactory()).createComposite();
        composite.setName(new QName(null, "contributionComposite"));
        composite.setURI("contributionComposite.composite");

        contributionService.addDeploymentComposite(contribution, composite);

        List deployables = contributionService.getContribution(contributionId).getDeployables();
        Composite composite1 = (Composite)deployables.get(deployables.size() - 1);
        assertEquals("contributionComposite", composite1.getName().toString());

        Artifact artifact = null;
        contribution = contributionService.getContribution(contributionId);
        String id = artifactId.toString();
        for (Artifact a : contribution.getArtifacts()) {
            if (id.equals(a.getURI())) {
                artifact = a;
                break;
            }
        }
        Composite composite2 = (Composite)artifact.getModel();
        assertEquals("contributionComposite", composite2.getName().toString());
    }

}
