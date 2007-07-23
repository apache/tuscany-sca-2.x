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
package org.apache.tuscany.sca.contribution.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.service.ContributionMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;

/**
 * Loader that handles contribution metadata files
 * 
 * @version $Rev: 515261 $ $Date: 2007-03-06 11:22:46 -0800 (Tue, 06 Mar 2007) $
 */
public class ContributionMetadataDocumentProcessorImpl  implements ContributionMetadataDocumentProcessor {
    protected final URLClassLoader classLoader;
    protected final StAXArtifactProcessor staxProcessor;
    protected final AssemblyFactory assemblyFactory;
    protected final ContributionFactory contributionFactory;
    protected final XMLInputFactory inputFactory;

    public ContributionMetadataDocumentProcessorImpl(URLClassLoader classLoader, StAXArtifactProcessor staxProcessor, AssemblyFactory assemblyFactory, ContributionFactory contributionFactory, XMLInputFactory inputFactory) {
        super();
        this.classLoader = classLoader;
        this.staxProcessor = staxProcessor; 
        this.assemblyFactory = assemblyFactory;
        this.contributionFactory = contributionFactory;
        this.inputFactory = inputFactory;
    }

    private Contribution mergeContributionMetadata(Contribution contrib1, Contribution contrib2  ) {
        contrib1.getDeployables().addAll(contrib2.getDeployables());
        contrib1.getImports().addAll(contrib2.getImports());
        contrib1.getExports().addAll(contrib2.getExports());
        
        return contrib1;
    }
    
    public void read(Contribution contribution) throws XMLStreamException, ContributionReadException {
        List<URL> artifactList = new ArrayList<URL>(2);
        //set generated first, as the user created one ovverrides generated information
        artifactList.add(this.classLoader.getResource(Contribution.SCA_CONTRIBUTION_GENERATED_META));
        artifactList.add(this.classLoader.getResource(Contribution.SCA_CONTRIBUTION_META));
        
        URL artifactURL = null; 
        InputStream artifactStream = null; 
        Iterator artifactIterator = artifactList.iterator();
        while( artifactIterator.hasNext() ){
            
            try {
                artifactURL = (URL) artifactIterator.next();
                if( artifactURL != null) {
                    artifactStream = artifactURL.openStream();
                    XMLStreamReader reader = inputFactory.createXMLStreamReader(artifactStream);
                    reader.nextTag();
                    
                    Contribution contributionMetadata = (Contribution) staxProcessor.read(reader); 
                    if (contributionMetadata != null) {
                        this.mergeContributionMetadata(contribution, contributionMetadata);
                    }
                }
                
            } catch (XMLStreamException e) {
                throw new ContributionReadException(e);
            } catch (IOException e) {
                throw new ContributionReadException(e);
            } finally {
                try {
                    if (artifactStream != null) {
                        artifactStream.close();
                        artifactStream = null;
                    }
                } catch (IOException ioe) {
                    //ignore
                }
            }
        }
    }
}
