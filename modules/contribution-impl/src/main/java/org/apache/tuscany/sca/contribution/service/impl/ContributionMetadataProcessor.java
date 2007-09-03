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
import org.apache.tuscany.sca.contribution.processor.BaseStAXArtifactProcessor;
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
public class ContributionMetadataProcessor extends BaseStAXArtifactProcessor implements StAXArtifactProcessor<Contribution> {
    
    private static final String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";
    private static final String TARGET_NAMESPACE = "targetNamespace";
    
    private static final QName CONTRIBUTION = new QName(SCA10_NS, "contribution");
    private static final QName DEPLOYABLE = new QName(SCA10_NS, "deployable");
    
    private final AssemblyFactory assemblyFactory;
    private final ContributionFactory contributionFactory;
    
    private final StAXArtifactProcessor<Object> extensionProcessor;

    public ContributionMetadataProcessor(AssemblyFactory assemblyFactory, ContributionFactory contributionFactory, StAXArtifactProcessor<Object> extensionProcessor) {
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
        Contribution contribution = null;
        
        QName element = null;
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    element = reader.getName();
                    
                    if (CONTRIBUTION.equals(element)) {

                        // Read <contribution>
                        contribution = this.contributionFactory.createContribution();
                        
                    } else if (DEPLOYABLE.equals(element)) {
                        
                        
                        // Read <deployable>
                        QName compositeName = getQName(reader, "composite");
                        if (compositeName == null) {
                            throw new ContributionReadException("Attribute 'composite' is missing");
                        }

                        if (contribution != null) {
                            Composite composite = assemblyFactory.createComposite();
                            composite.setName(compositeName);
                            composite.setUnresolved(true);
                            contribution.getDeployables().add(composite);
                            
                        }
                    } else{

                        // Read an extension element
                        Object extension = extensionProcessor.read(reader);
                        if (extension != null && contribution != null) {
                            if (extension instanceof Import) {
                                contribution.getImports().add((Import)extension);
                            } else if (extension instanceof Export) {
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

    public void write(Contribution contribution, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        
        // Write <contribution>
        writeStartDocument(writer, CONTRIBUTION.getNamespaceURI(), CONTRIBUTION.getLocalPart());

        // Write imports
        for (Import imp: contribution.getImports()) {
            extensionProcessor.write(imp, writer);
        }
        
        // Write exports
        for (Export export: contribution.getExports()) {
            extensionProcessor.write(export, writer);
        }
    
        // Write <deployable> elements
        for (Composite deployable: contribution.getDeployables()) {
            writeStart(writer, DEPLOYABLE.getNamespaceURI(), DEPLOYABLE.getLocalPart(),
                       new XAttr("composite", deployable.getName()));
            writeEnd(writer);
        }
        
        writeEndDocument(writer);
    }

    public void resolve(Contribution model, ModelResolver resolver) throws ContributionResolveException {
    }
}
