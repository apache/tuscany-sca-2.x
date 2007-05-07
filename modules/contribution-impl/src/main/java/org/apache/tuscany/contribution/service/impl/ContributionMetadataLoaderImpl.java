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
package org.apache.tuscany.contribution.service.impl;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.contribution.Contribution;
import org.apache.tuscany.contribution.ContributionExport;
import org.apache.tuscany.contribution.ContributionFactory;
import org.apache.tuscany.contribution.ContributionImport;
import org.apache.tuscany.contribution.service.ContributionMetadataLoader;
import org.apache.tuscany.contribution.service.ContributionMetadataLoaderException;

/**
 * Loader that handles contribution metadata files
 * 
 * @version $Rev: 515261 $ $Date: 2007-03-06 11:22:46 -0800 (Tue, 06 Mar 2007) $
 */
public class ContributionMetadataLoaderImpl implements ContributionMetadataLoader {
    private static final String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";
    private static final String TARGET_NAMESPACE = "targetNamespace";
    //private static final String NAME = "composite";
    
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

        while (true) {
            int event = reader.next();
            switch (event) {
                case START_ELEMENT:
                    QName element = reader.getName();
                    if (DEPLOYABLE.equals(element)) {
                        String name = reader.getAttributeValue(null, "composite");
                        if (name == null) {
                            throw new InvalidValueException("Attribute 'composite' is missing");
                        }
                        QName compositeName = null;
                        int index = name.indexOf(':');
                        if (index != -1) {
                            String prefix = name.substring(0, index);
                            String localPart = name.substring(index);
                            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
                            if (ns == null) {
                                throw new InvalidValueException("Invalid prefix: " + prefix);
                            }
                            compositeName = new QName(getString(reader, TARGET_NAMESPACE), localPart, prefix);
                        } else {
                            String prefix = "";
                            //String ns = reader.getNamespaceURI();
                            String localPart = name;
                            compositeName = new QName(getString(reader, TARGET_NAMESPACE), localPart, prefix);
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
                        ContributionImport contributionImport = this.contributionFactory.createContributionImport();
                        if (location != null) {
                            contributionImport.setLocation(location);
                        }
                        contributionImport.setNamespace(ns);
                        contribution.getImports().add(contributionImport);
                    } else if (EXPORT.equals(element)) {
                        String ns = reader.getAttributeValue(null, "namespace");
                        if (ns == null) {
                            throw new InvalidValueException("Attribute 'namespace' is missing");
                        }
                        ContributionExport contributionExport = this.contributionFactory.createContributionExport();
                        contributionExport.setNamespace(ns);
                        contribution.getExports().add(contributionExport);
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
    
    /**
     * Returns the string value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    protected String getString(XMLStreamReader reader, String name) {
        return reader.getAttributeValue(null, name);
    }

    /**
     * Returns the qname value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    protected QName getQName(XMLStreamReader reader, String name) {
        String qname = reader.getAttributeValue(null, name);
        return getQNameValue(reader, qname);
    }
    

    /**
     * Returns a qname from a string.  
     * @param reader
     * @param value
     * @return
     */
    protected QName getQNameValue(XMLStreamReader reader, String value) {
        if (value != null) {
            int index = value.indexOf(':');
            String prefix = index == -1 ? "" : value.substring(0, index);
            String localName = index == -1 ? value : value.substring(index + 1);
            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
            if (ns == null) {
                ns = "";
            }
            return new QName(ns, localName, prefix);
        } else {
            return null;
        }
    }

}
