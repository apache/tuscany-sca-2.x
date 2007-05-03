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
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.assembly.xml.ComponentTypeDocumentProcessor;
import org.apache.tuscany.assembly.xml.ComponentTypeProcessor;
import org.apache.tuscany.assembly.xml.CompositeDocumentProcessor;
import org.apache.tuscany.assembly.xml.CompositeProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeDocumentProcessor;
import org.apache.tuscany.assembly.xml.ConstrainingTypeProcessor;
import org.apache.tuscany.contribution.Contribution;
import org.apache.tuscany.contribution.DeployedArtifact;
import org.apache.tuscany.contribution.impl.DefaultContributionFactory;
import org.apache.tuscany.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.DefaultURLArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.PackageProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.impl.DefaultPackageProcessorExtensionPoint;
import org.apache.tuscany.contribution.processor.impl.FolderContributionProcessor;
import org.apache.tuscany.contribution.processor.impl.JarContributionProcessor;
import org.apache.tuscany.contribution.resolver.DefaultArtifactResolver;
import org.apache.tuscany.contribution.service.ContributionRepository;
import org.apache.tuscany.contribution.service.ContributionService;
import org.apache.tuscany.contribution.service.impl.ContributionRepositoryImpl;
import org.apache.tuscany.contribution.service.impl.ContributionServiceImpl;
import org.apache.tuscany.contribution.service.impl.PackageTypeDescriberImpl;
import org.apache.tuscany.contribution.service.util.FileHelper;
import org.apache.tuscany.contribution.service.util.IOHelper;
import org.apache.tuscany.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.core.ExtensionPointRegistry;
import org.apache.tuscany.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.interfacedef.impl.DefaultInterfaceContractMapper;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;

/**
 * This is more intended to be a integration test then a unit test. *
 */
public class ContributionServiceTestCase extends TestCase {
    private static final String CONTRIBUTION_001_ID = "contribution001/";
    private static final String CONTRIBUTION_002_ID = "contribution002/";
    private static final String JAR_CONTRIBUTION = "/repository/sample-calculator.jar";
    private static final String FOLDER_CONTRIBUTION = "target/classes/";

    private ContributionService contributionService;
    
    protected void setUp() throws Exception {
        
        // Create default factories
        AssemblyFactory assemblyFactory = new DefaultAssemblyFactory();
        PolicyFactory policyFactory = new DefaultPolicyFactory();
        InterfaceContractMapper mapper = new DefaultInterfaceContractMapper();
        
        // Create an extension point registry
        ExtensionPointRegistry extensionRegistry = new DefaultExtensionPointRegistry();

        // Add artifact processor extension points
        DefaultStAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint();
        extensionRegistry.addExtensionPoint(StAXArtifactProcessorExtensionPoint.class, staxProcessors);
        DefaultURLArtifactProcessorExtensionPoint documentProcessors = new DefaultURLArtifactProcessorExtensionPoint();
        extensionRegistry.addExtensionPoint(URLArtifactProcessorExtensionPoint.class, documentProcessors);

        // Register base artifact processors
        staxProcessors.addExtension(new CompositeProcessor(assemblyFactory, policyFactory, mapper, staxProcessors));
        staxProcessors.addExtension(new ComponentTypeProcessor(assemblyFactory, policyFactory, staxProcessors));
        staxProcessors.addExtension(new ConstrainingTypeProcessor(assemblyFactory, policyFactory, staxProcessors));

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        documentProcessors.addExtension(new CompositeDocumentProcessor(staxProcessors, inputFactory));
        documentProcessors.addExtension(new ComponentTypeDocumentProcessor(staxProcessors, inputFactory));
        documentProcessors.addExtension(new ConstrainingTypeDocumentProcessor(staxProcessors, inputFactory));

        // Create package processor extension point
        PackageTypeDescriberImpl describer = new PackageTypeDescriberImpl();
        PackageProcessorExtensionPoint packageProcessors = new DefaultPackageProcessorExtensionPoint(describer);
        extensionRegistry.addExtensionPoint(PackageProcessorExtensionPoint.class, packageProcessors);
        
        // Register base package processors
        new JarContributionProcessor(packageProcessors);
        new FolderContributionProcessor(packageProcessors);

        // Create a repository
        ContributionRepository repository = new ContributionRepositoryImpl("target");
        
        // Create an artifact resolver and contribution service
        DefaultArtifactResolver artifactResolver = new DefaultArtifactResolver(getClass().getClassLoader());
        this.contributionService = new ContributionServiceImpl(repository, packageProcessors,
                                                                              documentProcessors, artifactResolver, assemblyFactory, new DefaultContributionFactory());
    }

    public void testContributeJAR() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        URI contributionId = URI.create(CONTRIBUTION_001_ID);
        contributionService.contribute(contributionId, contributionLocation, false);
        assertNotNull(contributionService.getContribution(contributionId));
    }

    public void testStoreContributionPackageInRepository() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        URI contributionId = URI.create(CONTRIBUTION_001_ID);
        contributionService.contribute(contributionId, contributionLocation, true);
        
        assertTrue(FileHelper.toFile(new URL(contributionService.getContribution(contributionId).getLocation())).exists());

        assertNotNull(contributionId);

        Contribution contributionModel = contributionService.getContribution(contributionId);
        
        File contributionFile = FileHelper.toFile(new URL(contributionModel.getLocation()));
        assertTrue(contributionFile.exists());
    }
    
    public void testStoreContributionStreamInRepository() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        URI contributionId = URI.create(CONTRIBUTION_001_ID);
        
        InputStream contributionStream = contributionLocation.openStream();
        try {
            contributionService.contribute(contributionId, contributionLocation, contributionStream);
        } finally {
            IOHelper.closeQuietly(contributionStream);
        }
        
        assertTrue(FileHelper.toFile(new URL(contributionService.getContribution(contributionId).getLocation())).exists());

        assertNotNull(contributionId);

        Contribution contributionModel = contributionService.getContribution(contributionId);
        
        File contributionFile = FileHelper.toFile(new URL(contributionModel.getLocation()));
        assertTrue(contributionFile.exists());
    }    
    
    public void testStoreDuplicatedContributionInRepository() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        URI contributionId1 = URI.create(CONTRIBUTION_001_ID);
        contributionService.contribute(contributionId1, contributionLocation, true);
        assertNotNull(contributionService.getContribution(contributionId1));
        URI contributionId2 = URI.create(CONTRIBUTION_002_ID);
        contributionService.contribute(contributionId2, contributionLocation, true);
        assertNotNull(contributionService.getContribution(contributionId2));
    }
    
    
    public void testContributeFolder() throws Exception {
        /*
        File rootContributionFolder = new File(FOLDER_CONTRIBUTION);
        URI contributionId = URI.create(CONTRIBUTION_001_ID);
        
        //first rename the sca-contribution metadata file
        File calculatorMetadataFile = new File("target/classes/calculator/sca-contribution.xml");
        File metadataDirectory = new File("target/classes/META-INF/");
        
        if (! metadataDirectory.exists()) {
            FileHelper.forceMkdir(metadataDirectory);
        }
        FileHelper.copyFileToDirectory(calculatorMetadataFile, metadataDirectory);
        
        contributionService.contribute(contributionId, rootContributionFolder.toURL(), false);
        */
    }
    
    
    public void testAddDeploymentComposites() throws Exception {
        URL contributionLocation = getClass().getResource(JAR_CONTRIBUTION);
        URI contributionId = URI.create(CONTRIBUTION_001_ID);
        contributionService.contribute(contributionId, contributionLocation, false);
        assertNotNull(contributionService.getContribution(contributionId));

        URI artifactId = contributionId.resolve("contributionComposite.composite");
        Composite composite = (new DefaultAssemblyFactory()).createComposite();
        composite.setName(new QName(null, "contributionComposite"));
        
        contributionService.addDeploymentComposite(contributionId, composite);
        
        
        List deployables = contributionService.getContribution(contributionId).getDeployables();
        Composite composite1 = (Composite) deployables.get( deployables.size() -1 );
        assertEquals("contributionComposite", composite1.getName().toString());
        
        DeployedArtifact artifact = null;
        Contribution contribution = contributionService.getContribution(contributionId);
        String id = artifactId.toString();
        for (DeployedArtifact a: contribution.getArtifacts()) {
            if (id.equals(a.getURI())) {
                artifact = a;
                break;
            }
        }
        Composite composite2 = (Composite) artifact.getModel();
        assertEquals("contributionComposite", composite2.getName().toString());
    }

}
