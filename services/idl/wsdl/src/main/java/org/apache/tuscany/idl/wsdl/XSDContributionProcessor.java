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

package org.apache.tuscany.idl.wsdl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.ArtifactResolverRegistry;
import org.apache.tuscany.spi.extension.ContributionProcessorExtension;
import org.apache.tuscany.spi.model.Contribution;
import org.apache.tuscany.spi.model.DeployedArtifact;

import org.apache.tuscany.host.deployment.DeploymentException;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaException;
import org.apache.ws.commons.schema.resolver.URIResolver;

/**
 * The XSD processor
 *
 * @version $Rev$ $Date$
 */
public class XSDContributionProcessor extends ContributionProcessorExtension {

    private ArtifactResolverRegistry artifactResolverRegistry;

    public XSDContributionProcessor() throws WSDLException {
    }

    /**
     * URI resolver implementation for xml schema
     */
    protected class URIResolverImpl implements URIResolver {
        private URI contribution;

        public URIResolverImpl(URI contriution) {
            this.contribution = contriution;
        }

        public org.xml.sax.InputSource resolveEntity(java.lang.String targetNamespace,
                                                     java.lang.String schemaLocation,
                                                     java.lang.String baseUri) {
            try {
                URL url = artifactResolverRegistry.resolve(contribution, targetNamespace, schemaLocation, baseUri);
                return new InputSource(url.openStream());
            } catch (IOException e) {
                return null;
            }
        }

    }

    @SuppressWarnings("unchecked")
    public XmlSchema loadSchema(Contribution contribution, String namespace, URI location, InputStream inputStream)
        throws IOException, DeploymentException {
        XmlSchemaCollection collection = new XmlSchemaCollection();
        collection.setSchemaResolver(new URIResolverImpl(contribution.getUri()));
        XmlSchema schema = collection.read(new InputStreamReader(inputStream), null);

        if (namespace != null && schema != null && !namespace.equals(schema.getTargetNamespace())) {
            throw new XmlSchemaException(namespace + " != " + schema.getTargetNamespace());
        }

        DeployedArtifact artifact = contribution.getArtifact(location);
        artifact.addModelObject(XmlSchema.class, schema.getTargetNamespace(), schema);
        return schema;
    }

    public void loadSchemas(Contribution contribution, URI source, Definition definition) {
        Types types = definition.getTypes();
        if (types != null) {
            DeployedArtifact artifact = contribution.getArtifact(source);
            XmlSchemaCollection collection = new XmlSchemaCollection();
            for (Object ext : types.getExtensibilityElements()) {
                if (ext instanceof Schema) {
                    Element element = ((Schema) ext).getElement();
                    XmlSchema s = collection.read(element, element.getBaseURI());
                    artifact.addModelObject(XmlSchema.class, s.getTargetNamespace(), s);
                }
            }
        }
    }

    public String getContentType() {
        return "application/vnd.tuscany.xsd";
    }

    /**
     * @param artifactResolverRegistry the artifactResolverRegistry to set
     */
    @Reference
    public void setArtifactResolverRegistry(ArtifactResolverRegistry artifactResolverRegistry) {
        this.artifactResolverRegistry = artifactResolverRegistry;
    }

    public void processContent(Contribution contribution, URI source, InputStream inputStream)
        throws DeploymentException, IOException {
        loadSchema(contribution, null, source, inputStream);
    }

    public void processModel(Contribution contribution, URI source, Object modelObject) throws DeploymentException,
                                                                                               IOException {
        if (modelObject instanceof Definition) {
            loadSchemas(contribution, source, (Definition) modelObject);
        }
    }

}
