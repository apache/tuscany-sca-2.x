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

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.NamespaceExport;
import org.apache.tuscany.sca.contribution.NamespaceImport;
import org.apache.tuscany.sca.contribution.service.ContributionMetadataLoader;
import org.apache.tuscany.sca.contribution.service.ContributionMetadataLoaderException;

/**
 * Loader that handles contribution metadata files
 * 
 * @version $Rev: 515261 $ $Date: 2007-03-06 11:22:46 -0800 (Tue, 06 Mar 2007) $
 */
public class ContributionMetadataLoaderImpl implements ContributionMetadataLoader {
    private static final String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";
    private static final String TARGET_NAMESPACE = "targetNamespace";
    
    private static final QName CONTRIBUTION = new QName(SCA10_NS, "contribution");
    private static final QName DEPLOYABLE = new QName(SCA10_NS, "deployable");
    private static final QName IMPORT = new QName(SCA10_NS, "import");
    private static final QName EXPORT = new QName(SCA10_NS, "export");
    
    private final AssemblyFactory assemblyFactory;
    private final ContributionFactory contributionFactory;

    public ContributionMetadataLoaderImpl(AssemblyFactory assemblyFactory, ContributionFactory contributionFactory) {
        super();
        this.assemblyFactory = assemblyFactory;
        this.contributionFactory = contributionFactory;
    }

    public QName getXMLType() {
        return CONTRIBUTION;
    }

    public void load(Contribution contribution, XMLStreamReader reader) throws XMLStreamException, ContributionMetadataLoaderException {
        String targetNameSpaceURI = null;

        while (true) {
            int event = reader.next();
            switch (event) {
                case START_ELEMENT:
                    QName element = reader.getName();
                    if (CONTRIBUTION.equals(element)) {
                        targetNameSpaceURI = reader.getAttributeValue(null, TARGET_NAMESPACE);    
                    } else if (DEPLOYABLE.equals(element)) {
                        String name = reader.getAttributeValue(null, "composite");
                        if (name == null) {
                            throw new InvalidValueException("Attribute 'composite' is missing");
                        }
                        QName compositeName = null;

                        int index = name.indexOf(':');
                        if (index != -1) {
                            String prefix = name.substring(0, index);
                            String localPart = name.substring(index + 1);
                            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
                            if (ns == null) {
                                throw new InvalidValueException("Invalid prefix: " + prefix);
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
                    } else if (IMPORT.equals(element)) {
                        String ns = reader.getAttributeValue(null, "namespace");
                        if (ns == null) {
                            throw new InvalidValueException("Attribute 'namespace' is missing");
                        }
                        String location = reader.getAttributeValue(null, "location");
                        NamespaceImport namespaceImport = this.contributionFactory.createNamespaceImport();
                        if (location != null) {
                            namespaceImport.setLocation(location);
                        }
                        namespaceImport.setNamespace(ns);
                        contribution.getImports().add(namespaceImport);
                    } else if (EXPORT.equals(element)) {
                        String ns = reader.getAttributeValue(null, "namespace");
                        if (ns == null) {
                            throw new InvalidValueException("Attribute 'namespace' is missing");
                        }
                        NamespaceExport namespaceExport = this.contributionFactory.createNamespaceExport();
                        namespaceExport.setNamespace(ns);
                        contribution.getExports().add(namespaceExport);
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (CONTRIBUTION.equals(reader.getName())) {
                        return;
                    }
                    break;

            }
        }
    }
}
