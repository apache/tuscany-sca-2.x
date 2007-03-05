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
package org.apache.tuscany.core.loader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.UnrecognizedElementException;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.services.artifact.Artifact;

import org.apache.tuscany.core.implementation.composite.Dependency;

/**
 * Loader for handling <dependency> elements.
 *
 * @version $Rev$ $Date$
 */
public class DependencyLoader extends LoaderExtension<Dependency> {
    private static final String NS = "http://tuscany.apache.org/xmlns/sca/2.0-alpha";
    private static final QName DEPENDENCY = new QName(NS, "dependency");
    private static final QName GROUP = new QName(NS, "group");
    private static final QName NAME = new QName(NS, "name");
    private static final QName VERSION = new QName(NS, "version");
    private static final QName CLASSIFIER = new QName(NS, "classifier");
    private static final QName TYPE = new QName(NS, "type");

    public DependencyLoader(@Reference LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return DEPENDENCY;
    }

    public Dependency load(
        ModelObject object,
        XMLStreamReader reader,
        DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {

        Artifact artifact = new Artifact();
        while (reader.nextTag() == XMLStreamConstants.START_ELEMENT) {
            QName name = reader.getName();
            String text = reader.getElementText();
            if (GROUP.equals(name)) {
                artifact.setGroup(text);
            } else if (NAME.equals(name)) {
                artifact.setName(text);
            } else if (VERSION.equals(name)) {
                artifact.setVersion(text);
            } else if (CLASSIFIER.equals(name)) {
                artifact.setClassifier(text);
            } else if (TYPE.equals(name)) {
                artifact.setType(text);
            } else {
                throw new UnrecognizedElementException(name);
            }
        }
        Dependency dependency = new Dependency();
        dependency.setArtifact(artifact);
        return dependency;
    }
}
