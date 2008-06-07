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

package org.apache.tuscany.sca.xsd.xml;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DefaultImport;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.namespace.NamespaceImport;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionRuntimeException;
import org.apache.tuscany.sca.xsd.DefaultXSDFactory;
import org.apache.tuscany.sca.xsd.XSDFactory;
import org.apache.tuscany.sca.xsd.XSDefinition;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

/**
 * A Model Resolver for XSD models.
 * 
 * @version $Rev$ $Date$
 */
public class XSDModelResolver implements ModelResolver {
    private XSDFactory factory;
    private Contribution contribution;
    private Map<String, List<XSDefinition>> map = new HashMap<String, List<XSDefinition>>();
    private XmlSchemaCollection schemaCollection;

    public XSDModelResolver(Contribution contribution, ModelFactoryExtensionPoint modelFactories) {
        this.contribution = contribution;
        this.schemaCollection = new XmlSchemaCollection();
        schemaCollection.setSchemaResolver(new URIResolverImpl(contribution));
        this.factory = new DefaultXSDFactory();
    }

    public void addModel(Object resolved) {
        XSDefinition definition = (XSDefinition)resolved;
        List<XSDefinition> list = map.get(definition.getNamespace());
        if (list == null) {
            list = new ArrayList<XSDefinition>();
            map.put(definition.getNamespace(), list);
        }
        list.add(definition);
    }

    public Object removeModel(Object resolved) {
        XSDefinition definition = (XSDefinition)resolved;
        List<XSDefinition> list = map.get(definition.getNamespace());
        if (list == null) {
            return null;
        } else {
            return list.remove(definition);
        }
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {

        XSDefinition definition = (XSDefinition)unresolved;
        // Lookup a definition for the given namespace
        String namespace = definition.getNamespace();
        List<XSDefinition> list = map.get(namespace);
        if (list != null && definition.getDocument() != null) {
            // Set the document for the inline schema
            int index = list.indexOf(definition);
            if (index != -1) {
                list.get(index).setDocument(definition.getDocument());
            }
        }
        if (list == null && definition.getDocument() != null) {
            // Hit for the 1st time
            list = new ArrayList<XSDefinition>();
            list.add(definition);
            map.put(namespace, list);
        }
        XSDefinition resolved = null;
        try {
            resolved = aggregate(list);
        } catch (IOException e) {
            throw new ContributionRuntimeException(e);
        }
        if (resolved != null && !resolved.isUnresolved()) {
            return modelClass.cast(resolved);
        }

        // No definition found, delegate the resolution to the imports
        for (Import import_ : this.contribution.getImports()) {
            if (import_ instanceof NamespaceImport) {
                NamespaceImport namespaceImport = (NamespaceImport)import_;
                if (namespaceImport.getNamespace().equals(namespace)) {

                    // Delegate the resolution to the namespace import resolver
                    resolved =
                        namespaceImport.getModelResolver().resolveModel(XSDefinition.class, (XSDefinition)unresolved);
                    if (!resolved.isUnresolved()) {
                        return modelClass.cast(resolved);
                    }
                }
            } else if (import_ instanceof DefaultImport) {

                // Delegate the resolution to the default import resolver
                resolved =
                    import_.getModelResolver().resolveModel(XSDefinition.class, (XSDefinition)unresolved);
                if (!resolved.isUnresolved()) {
                    return modelClass.cast(resolved);
                }
            }
        }
        return modelClass.cast(unresolved);
    }

    private void loadOnDemand(XSDefinition definition) throws IOException {
        if (definition.getSchema() != null) {
            return;
        }
        if (definition.getDocument() != null) {
            String uri = null;
            if (definition.getLocation() != null) {
                uri = definition.getLocation().toString();
            }
            XmlSchema schema = schemaCollection.read(definition.getDocument(), uri, null);
            definition.setSchemaCollection(schemaCollection);
            definition.setSchema(schema);
            definition.setUnresolved(false);
        } else if (definition.getLocation() != null) {
            if (definition.getLocation().getFragment() != null) {
                // It's an inline schema
                return;
            }
            // Read an XSD document
            InputSource xsd = XMLDocumentHelper.getInputSource(definition.getLocation().toURL());
            XmlSchema schema = schemaCollection.read(xsd, null);
            definition.setSchemaCollection(schemaCollection);
            definition.setSchema(schema);
        }
    }

    /**
     * Create a facade XmlSchema which includes all the definitions
     * 
     * @param definitions A list of the XmlSchema under the same target
     *                namespace
     * @return The aggregated XmlSchema
     */
    private XSDefinition aggregate(List<XSDefinition> definitions) throws IOException {
        if (definitions == null || definitions.size() == 0) {
            return null;
        }
        if (definitions.size() == 1) {
            XSDefinition d = definitions.get(0);
            loadOnDemand(d);
            return d;
        }
        XSDefinition aggregated = factory.createXSDefinition();
        for (XSDefinition d : definitions) {
            loadOnDemand(d);
        }
        String ns = definitions.get(0).getNamespace();
        XmlSchema facade = new XmlSchema(ns, schemaCollection);

        for (XmlSchema d : schemaCollection.getXmlSchemas()) {
            if (ns.equals(d.getTargetNamespace())) {
                XmlSchemaInclude include = new XmlSchemaInclude();
                include.setSchema(d);
                include.setSourceURI(d.getSourceURI());
                include.setSchemaLocation(d.getSourceURI());
                facade.getIncludes().add(include);
                facade.getItems().add(include);
            }
        }
        aggregated.setUnresolved(true);
        aggregated.setSchema(facade);
        aggregated.setNamespace(ns);
        aggregated.setAggregatedDefinitions(definitions);
        aggregated.setUnresolved(false);

        // FIXME: [rfeng] This is hacky
        //definitions.clear();
        //definitions.add(aggregated);
        return aggregated;
    }

    /**
     * URI resolver implementation for XML schema
     */
    public static class URIResolverImpl implements URIResolver {
        private Contribution contribution;

        public URIResolverImpl(Contribution contribution) {
            this.contribution = contribution;
        }

        public org.xml.sax.InputSource resolveEntity(java.lang.String targetNamespace,
                                                     java.lang.String schemaLocation,
                                                     java.lang.String baseUri) {
            try {
                if (schemaLocation == null || schemaLocation.startsWith("/")) {
                    return null;
                }
                URL url = null;
                if (schemaLocation.startsWith("/")) {
                    // The URI is relative to the contribution
                    String uri = schemaLocation.substring(1);
                    for (Artifact a : contribution.getArtifacts()) {
                        if (a.getURI().equals(uri)) {
                            url = new URL(a.getLocation());
                            break;
                        }
                    }
                } else {
                    url = new URL(new URL(baseUri), schemaLocation);
                }
                return XMLDocumentHelper.getInputSource(url);
            } catch (IOException e) {
                return null;
            }
        }
    }

}
