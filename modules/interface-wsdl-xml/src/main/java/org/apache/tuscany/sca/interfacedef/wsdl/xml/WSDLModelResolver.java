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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.PortType;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.DeployedArtifact;
import org.apache.tuscany.sca.contribution.Import;
import org.apache.tuscany.sca.contribution.NamespaceImport;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionRuntimeException;
import org.apache.tuscany.sca.interfacedef.wsdl.DefaultWSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * A Model Resolver for WSDL models.
 * 
 * @version $Rev: 557916 $ $Date: 2007-07-20 01:04:40 -0700 (Fri, 20 Jul 2007) $
 */
public class WSDLModelResolver implements ModelResolver {
    private Contribution contribution;
    private Map<String, List<WSDLDefinition>> map = new HashMap<String, List<WSDLDefinition>>();

    public WSDLModelResolver(Contribution contribution) {
        this.contribution = contribution;
        // FIXME: [rfeng] To avoid the hard-coded factories
        try {
            this.factory = new DefaultWSDLFactory();
            this.wsdlFactory = javax.wsdl.factory.WSDLFactory.newInstance();
            wsdlExtensionRegistry = this.wsdlFactory.newPopulatedExtensionRegistry();
        } catch (WSDLException e) {
            throw new ContributionRuntimeException(e);
        }
    }

    /**
     * Implementation of a WSDL locator.
     */
    private class WSDLLocatorImpl implements WSDLLocator {
        private InputStream inputStream;
        private URL base;
        private String latestImportURI;

        public WSDLLocatorImpl(URL base, InputStream is) {
            this.base = base;
            this.inputStream = is;
        }

        public void close() {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Ignore
            }
        }

        public InputSource getBaseInputSource() {
            try {
                return XMLDocumentHelper.getInputSource(base, inputStream);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public String getBaseURI() {
            return base.toString();
        }

        public InputSource getImportInputSource(String parentLocation, String importLocation) {
            try {
                if (importLocation == null) {
                    throw new IllegalArgumentException("Required attribute 'location' is missing.");
                }

                URL url = null;
                if (importLocation.startsWith("/")) {
                    // The URI is relative to the contribution
                    String uri = importLocation.substring(1);
                    for (DeployedArtifact a : contribution.getArtifacts()) {
                        if (a.getURI().equals(uri)) {
                            url = new URL(a.getLocation());
                            break;
                        }
                    }
                } else {
                    url = new URL(new URL(parentLocation), importLocation);
                }
                if (url == null) {
                    return null;
                }
                latestImportURI = url.toString();
                return XMLDocumentHelper.getInputSource(url);
            } catch (Exception e) {
                throw new ContributionRuntimeException(e);
            }
        }

        public String getLatestImportURI() {
            return latestImportURI;
        }

    }

    private javax.wsdl.factory.WSDLFactory wsdlFactory;
    private ExtensionRegistry wsdlExtensionRegistry;
    private WSDLFactory factory;

    public void addModel(Object resolved) {
        WSDLDefinition definition = (WSDLDefinition)resolved;
        List<WSDLDefinition> list = map.get(definition.getNamespace());
        if (list == null) {
            list = new ArrayList<WSDLDefinition>();
            map.put(definition.getNamespace(), list);
        }
        list.add(definition);
    }

    public Object removeModel(Object resolved) {
        WSDLDefinition definition = (WSDLDefinition)resolved;
        List<WSDLDefinition> list = map.get(definition.getNamespace());
        if (list == null) {
            return null;
        } else {
            return list.remove(definition);
        }
    }

    /**
     * Create a facade Definition which imports all the defintions
     * 
     * @param definitions A list of the WSDL definitions under the same target namespace
     * @return The aggregated WSDL definition
     */
    private WSDLDefinition aggregate(List<WSDLDefinition> definitions) {
        if (definitions == null || definitions.size() == 0) {
            return null;
        }
        if (definitions.size() == 1) {
            WSDLDefinition d = definitions.get(0);
            loadOnDemand(d, d.getInlinedSchemas());
            return d;
        }
        WSDLDefinition aggregated = factory.createWSDLDefinition();
        for (WSDLDefinition d : definitions) {
            loadOnDemand(d, aggregated.getInlinedSchemas());
        }
        Definition facade = wsdlFactory.newDefinition();
        String ns = definitions.get(0).getNamespace();
        facade.setQName(new QName(ns, "$aggregated$"));
        facade.setTargetNamespace(ns);

        for (WSDLDefinition d : definitions) {
            if (d.getDefinition() != null) {
                javax.wsdl.Import imp = facade.createImport();
                imp.setNamespaceURI(d.getNamespace());
                imp.setDefinition(d.getDefinition());
                imp.setLocationURI(d.getDefinition().getDocumentBaseURI());
                facade.addImport(imp);
            }
        }
        aggregated.setDefinition(facade);
        definitions.clear();
        definitions.add(aggregated);
        return aggregated;
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {

        // Lookup a definition for the given namespace
        String namespace = ((WSDLDefinition)unresolved).getNamespace();
        List<WSDLDefinition> list = map.get(namespace);
        WSDLDefinition resolved = aggregate(list);
        if (resolved != null && !resolved.isUnresolved()) {
            return modelClass.cast(resolved);
        }

        // No definition found, delegate the resolution to the imports
        for (Import import_ : this.contribution.getImports()) {
            if (import_ instanceof NamespaceImport) {
                NamespaceImport namespaceImport = (NamespaceImport)import_;
                if (namespaceImport.getNamespace().equals(namespace)) {

                    // Delegate the resolution to the import resolver
                    resolved =
                        namespaceImport.getModelResolver().resolveModel(WSDLDefinition.class,
                                                                        (WSDLDefinition)unresolved);
                    if (!resolved.isUnresolved()) {
                        return modelClass.cast(resolved);
                    }
                }
            }
        }
        return modelClass.cast(unresolved);
    }

    /**
     * Load the WSDL definition on demand
     * @param def
     * @param schemaCollection
     */
    private void loadOnDemand(WSDLDefinition def, XmlSchemaCollection schemaCollection) {
        if (def.getDefinition() == null && def.getLocation() != null) {
            // Load the definition on-demand
            try {
                loadDefinition(def, schemaCollection);
            } catch (ContributionReadException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // private Map<String, WSDLDefinition> loadedDefinitions = new Hashtable<String, WSDLDefinition>();

    /**
     * Merge a set of WSDLs into a facade Definition
     * 
     * @param definitions
     * @return
     */
    @SuppressWarnings("unchecked")
    private Definition merge(Definition target, Definition source) {
        for (Iterator j = source.getImports().values().iterator(); j.hasNext();) {
            List list = (List)j.next();
            for (Iterator k = list.iterator(); k.hasNext();)
                target.addImport((javax.wsdl.Import)k.next());
        }

        for (Iterator k = source.getBindings().values().iterator(); k.hasNext();) {
            Binding binding = (Binding)k.next();
            if (!binding.isUndefined())
                target.getBindings().put(binding.getQName(), binding);
        }

        target.getExtensibilityElements().addAll(source.getExtensibilityElements());

        for (Iterator k = source.getMessages().values().iterator(); k.hasNext();) {
            Message msg = (Message)k.next();
            if (!msg.isUndefined())
                target.getMessages().put(msg.getQName(), msg);
        }

        target.getNamespaces().putAll(source.getNamespaces());

        for (Iterator k = source.getPortTypes().values().iterator(); k.hasNext();) {
            PortType portType = (PortType)k.next();
            if (!portType.isUndefined())
                target.getPortTypes().put(portType.getQName(), portType);
        }

        target.getServices().putAll(source.getServices());

        if (target.getTypes() == null) {
            target.setTypes(target.createTypes());
        }
        if (source.getTypes() != null)
            target.getTypes().getExtensibilityElements().addAll(source.getTypes().getExtensibilityElements());
        return target;

    }

    /**
     * Load the WSDL definition and inline schemas
     * 
     * @param wsdlDef
     * @param schemaCollection
     * @throws ContributionReadException
     */
    private void loadDefinition(WSDLDefinition wsdlDef, XmlSchemaCollection schemaCollection)
        throws ContributionReadException {
        if (wsdlDef.getDefinition() != null || wsdlDef.getLocation() == null) {
            return;
        }
        try {
            URL artifactURL = wsdlDef.getLocation().toURL();
            // Read a WSDL document
            InputStream is = artifactURL.openStream();
            WSDLReader reader = wsdlFactory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            reader.setFeature("javax.wsdl.importDocuments", true);
            // FIXME: We need to decide if we should disable the import processing by WSDL4J
            // reader.setFeature("javax.wsdl.importDocuments", false);
            reader.setExtensionRegistry(wsdlExtensionRegistry);

            WSDLLocatorImpl locator = new WSDLLocatorImpl(artifactURL, is);
            Definition definition = reader.readWSDL(locator);
            wsdlDef.setDefinition(definition);

            //Read inline schemas 
            readInlineSchemas(definition, schemaCollection);
        } catch (WSDLException e) {
            throw new ContributionReadException(e);
        } catch (IOException e) {
            throw new ContributionReadException(e);
        }
    }

    /**
     * Populate the inline schemas including those from the imported definitions
     * 
     * @param definition
     * @param schemaCollection
     */
    private void readInlineSchemas(Definition definition, XmlSchemaCollection schemaCollection) {
        Types types = definition.getTypes();
        if (types != null) {
            schemaCollection.setSchemaResolver(new XSDModelResolver.URIResolverImpl(contribution));
            for (Object ext : types.getExtensibilityElements()) {
                if (ext instanceof Schema) {
                    Element element = ((Schema)ext).getElement();
                    schemaCollection.setBaseUri(((Schema)ext).getDocumentBaseURI());
                    schemaCollection.read(element, element.getBaseURI());
                }
            }
        }
        for (Object imports : definition.getImports().values()) {
            List impList = (List)imports;
            for (Object i : impList) {
                javax.wsdl.Import anImport = (javax.wsdl.Import)i;
                // Read inline schemas 
                if (anImport.getDefinition() != null) {
                    readInlineSchemas(anImport.getDefinition(), schemaCollection);
                }
            }
        }
    }

}
