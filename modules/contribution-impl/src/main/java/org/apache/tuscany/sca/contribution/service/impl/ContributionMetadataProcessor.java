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

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.Export;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

/**
 * Loader that handles contribution metadata files
 * 
 * @version $Rev$ $Date$
 */
public class ContributionMetadataProcessor implements StAXArtifactProcessor<Contribution> {
    private static final String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";
    private static final String TARGET_NAMESPACE = "targetNamespace";
    
    private static final QName CONTRIBUTION = new QName(SCA10_NS, "contribution");
    private static final QName DEPLOYABLE = new QName(SCA10_NS, "deployable");
    
    private final AssemblyFactory assemblyFactory;
    private final ContributionFactory contributionFactory;
    
    private final StAXArtifactProcessor extensionProcessor;

    public ContributionMetadataProcessor(AssemblyFactory assemblyFactory, ContributionFactory contributionFactory, StAXArtifactProcessor extensionProcessor) {
        super();
        this.assemblyFactory = assemblyFactory;
        this.contributionFactory = contributionFactory;
        this.extensionProcessor = extensionProcessor;
    }
    
    
    public QName getArtifactType() {
        return CONTRIBUTION;
    }

    public Class<Contribution> getModelType() {
        return Contribution.class;
    }

    public Contribution read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        Contribution contribution = this.contributionFactory.createContribution();
        String targetNameSpaceURI = null;
        QName element = null;

        
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    element = reader.getName();
                    
                    if (CONTRIBUTION.equals(element)) {
                        targetNameSpaceURI = reader.getAttributeValue(null, TARGET_NAMESPACE);    
                    } else if (DEPLOYABLE.equals(element)) {
                        String name = reader.getAttributeValue(null, "composite");
                        if (name == null) {
                            throw new ContributionReadException("Attribute 'composite' is missing");
                        }
                        QName compositeName = null;

                        int index = name.indexOf(':');
                        if (index != -1) {
                            String prefix = name.substring(0, index);
                            String localPart = name.substring(index + 1);
                            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
                            if (ns == null) {
                                throw new ContributionReadException("Invalid prefix: " + prefix);
                            }
                            compositeName = new QName(targetNameSpaceURI, localPart, prefix);
                        } else {
                            String prefix = "";
                            String localPart = name;
                            compositeName = new QName(targetNameSpaceURI, localPart, prefix);
                        }

                        Composite composite = assemblyFactory.createComposite();
                        composite.setName(compositeName);
                        composite.setUnresolved(true);
                        
                        contribution.getDeployables().add(composite);
                    } else{
                        //process extension
                        Object extension = extensionProcessor.read(reader);
                        if (extension != null) {
                            if (extension instanceof Import) {
                                contribution.getImports().add((Import)extension);
                            }else if (extension instanceof Export) {
                                contribution.getExports().add((Export)extension);
                            }
                        }
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (CONTRIBUTION.equals(reader.getName())) {
                        return contribution;
                    }
                    break;        
            }
            
            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
        
        return contribution;
    }

    public void write(Contribution model, XMLStreamWriter outputSource) throws ContributionWriteException, XMLStreamException {
        // TODO Auto-generated method stub
        
    }

    public void resolve(Contribution model, ModelResolver resolver) throws ContributionResolveException {
        // TODO Auto-generated method stub
        
    }
}
