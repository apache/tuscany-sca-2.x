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

package org.apache.tuscany.sca.interfacedef.wsdl.xml;

import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.XSDefinition;

/**
 * An ArtifactProcessor for XSD documents.
 * 
 * @version $Rev$ $Date$
 */
public class XSDDocumentProcessor implements URLArtifactProcessor<XSDefinition> {

    private WSDLFactory factory;

    public XSDDocumentProcessor(ModelFactoryExtensionPoint modelFactories) {
        this.factory = modelFactories.getFactory(WSDLFactory.class);
    }

    public XSDefinition read(URL contributionURL, URI artifactURI, URL artifactURL) throws ContributionReadException {
        try {
            return indexRead(artifactURL);
        } catch (Exception e) {
            throw new ContributionReadException(e);
        }
    }

    public void resolve(XSDefinition model, ModelResolver resolver) throws ContributionResolveException {
    }

    public String getArtifactType() {
        return ".xsd";
    }

    public Class<XSDefinition> getModelType() {
        return XSDefinition.class;
    }

    public static final QName XSD = new QName("http://www.w3.org/2001/XMLSchema", "schema");

    protected XSDefinition indexRead(URL doc) throws Exception {
        XSDefinition xsd = factory.createXSDefinition();
        xsd.setUnresolved(true);
        xsd.setNamespace(XMLDocumentHelper.readTargetNamespace(doc, XSD, true, "targetNamespace"));
        xsd.setLocation(doc.toURI());
        xsd.setUnresolved(false);
        return xsd;
    }
}
