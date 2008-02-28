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
package org.apache.tuscany.sca.workspace.processor.impl;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.scanner.ContributionScanner;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataDocumentProcessor;
import org.apache.tuscany.sca.workspace.scanner.impl.DirectoryContributionScanner;
import org.apache.tuscany.sca.workspace.scanner.impl.JarContributionScanner;

/**
 * URLArtifactProcessor that handles contribution files and returns a contribution
 * info model.
 * 
 * @version $Rev$ $Date$
 */
public class ContributionInfoProcessor implements URLArtifactProcessor<Contribution>{
    private ContributionMetadataDocumentProcessor metadataProcessor;
    private ContributionFactory contributionFactory;

    public ContributionInfoProcessor(ContributionFactory contributionFactory, ContributionMetadataDocumentProcessor metadataProcessor) {
        this.contributionFactory = contributionFactory;
        this.metadataProcessor = metadataProcessor; 
    }
    
    public String getArtifactType() {
        return null;
    }
    
    public Class<Contribution> getModelType() {
        return Contribution.class;
    }
    
    public Contribution read(URL parentURL, URI contributionURI, URL contributionURL) throws ContributionReadException {
        
        // Create contribution model
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI(contributionURI.toString());
        contribution.setUnresolved(true);

        // Create a contribution scanner
        ContributionScanner scanner;
        if ("file".equals(contributionURL.getProtocol()) && new File(contributionURL.getFile()).isDirectory()) {
            scanner = new DirectoryContributionScanner();
        } else {
            scanner = new JarContributionScanner();
        }
        
        // Read generated and user sca-contribution.xml files
        for (String path: new String[]{
                                       Contribution.SCA_CONTRIBUTION_GENERATED_META,
                                       Contribution.SCA_CONTRIBUTION_META}) {
            URL url = scanner.getArtifactURL(contributionURL, path);
            if (url != null) {
                Contribution c = metadataProcessor.read(contributionURL, URI.create(path), url);
                contribution.getImports().addAll(c.getImports());
                contribution.getExports().addAll(c.getExports());
                contribution.getDeployables().addAll(c.getDeployables());
            }
        }
        
        return contribution;
    }
    
    public void resolve(Contribution contribution, ModelResolver resolver) throws ContributionResolveException {
        metadataProcessor.resolve(contribution, resolver);
    }

}
