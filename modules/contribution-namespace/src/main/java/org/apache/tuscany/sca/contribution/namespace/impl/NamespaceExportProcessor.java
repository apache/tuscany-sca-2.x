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

package org.apache.tuscany.sca.contribution.namespace.impl;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.namespace.NamespaceExport;
import org.apache.tuscany.sca.contribution.namespace.NamespaceImportExportFactory;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;

/**
 * Artifact processor for Namespace export
 * 
 * @version $Rev$ $Date$
 */
public class NamespaceExportProcessor implements StAXArtifactProcessor<NamespaceExport> {
    private static final String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";
    
    private static final QName EXPORT = new QName(SCA10_NS, "export");
    
    private static final String NAMESPACE = "namespace";
    
    private final NamespaceImportExportFactory factory;
    
    public NamespaceExportProcessor(NamespaceImportExportFactory factory) {
        super();
        this.factory = factory;
    }

    public QName getArtifactType() {
        return EXPORT;
    }
    
    public Class<NamespaceExport> getModelType() {
        return NamespaceExport.class;
    }
    
    /**
     * Process <export namespace=""/>
     */
    public NamespaceExport read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        NamespaceExport namespaceExport = this.factory.createNamespaceExport();
        QName element = null;

        
        while (reader.hasNext()) {
            int event = reader.getEventType();
            switch (event) {
                case START_ELEMENT:
                    element = reader.getName();
                    
                    if (EXPORT.equals(element)) {
                        String ns = reader.getAttributeValue(null, NAMESPACE);
                        if (ns == null) {
                            throw new ContributionReadException("Attribute 'namespace' is missing");
                        }
                        namespaceExport.setNamespace(ns);
                    } 
                    
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (EXPORT.equals(reader.getName())) {
                        return namespaceExport;
                    }
                    break;        
            }
            
            //Read the next element
            if (reader.hasNext()) {
                reader.next();
            }
        }
        
        return namespaceExport;
    }

    public void write(NamespaceExport model, XMLStreamWriter outputSource) throws ContributionWriteException, XMLStreamException {
        
    }

    public void resolve(NamespaceExport model, ModelResolver resolver) throws ContributionResolveException {
        
    }
}
