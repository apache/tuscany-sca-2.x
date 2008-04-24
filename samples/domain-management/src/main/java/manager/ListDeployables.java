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

package manager;

import java.io.File;
import java.net.URI;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.contribution.xml.ContributionGeneratedMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataProcessor;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.workspace.processor.impl.ContributionInfoProcessor;

/**
 * Sample ListDeployables task.
 *
 * This sample shows how to use a subset of Tuscany to read contribution
 * metadata.
 * 
 * The sample reads the SCA metadata for two sample contributions then
 * prints the names of their deployable composites.
 *
 * @version $Rev$ $Date$
 */
public class ListDeployables {
    
    private static URLArtifactProcessor<Contribution> contributionInfoProcessor;

    private static void init() {
        
        // Create extension point registry 
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        
        // Get XML input/output factories
        ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        XMLOutputFactory outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        
        // Get contribution and assembly model factories
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        
        // Create XML and document artifact processors
        StAXArtifactProcessorExtensionPoint xmlProcessorExtensions = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        StAXArtifactProcessor<Object> xmlProcessor = new ExtensibleStAXArtifactProcessor(xmlProcessorExtensions, inputFactory, outputFactory);
        URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        URLArtifactProcessor<Object> docProcessor = new ExtensibleURLArtifactProcessor(docProcessorExtensions);
        
        // Create and register XML artifact processor extension for sca-contribution XML
        xmlProcessorExtensions.addArtifactProcessor(new ContributionMetadataProcessor(assemblyFactory, contributionFactory, xmlProcessor));

        // Create and register document processor extensions for sca-contribution.xml and
        // sca-contribution-generated.xml documents
        docProcessorExtensions.addArtifactProcessor(new ContributionMetadataDocumentProcessor(xmlProcessor, inputFactory));
        docProcessorExtensions.addArtifactProcessor(new ContributionGeneratedMetadataDocumentProcessor(xmlProcessor, inputFactory));
        
        // Create contribution info processor
        ModelResolverExtensionPoint modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        contributionInfoProcessor = new ContributionInfoProcessor(modelFactories, modelResolvers, docProcessor);
    }
    

    public static void main(String[] args) throws Exception {
        init();

        // Read the contribution info for the sample contribution
        URI uri = URI.create("store");
        URL url = new File("./target/sample-domain-management-store.jar").toURI().toURL();
        Contribution contribution = (Contribution)contributionInfoProcessor.read(null, uri, url);
        
        // List the deployables in the contribution
        for (Composite deployable: contribution.getDeployables()) {
            System.out.println("Deployable: " + deployable.getName());
        }
        
    }
    
}
