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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionRuntimeException;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.xml.XMLDocumentHelper.URIResolverImpl;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * An ArtifactProcessor for WSDL documents.
 * 
 * @version $Rev$ $Date$
 */
public class WSDLDocumentProcessor implements URLArtifactProcessor<WSDLDefinition> {

    private javax.wsdl.factory.WSDLFactory wsdlFactory;
    private ExtensionRegistry wsdlExtensionRegistry;
    private WSDLFactory factory;

    private Map<String, WSDLDefinition> loadedDefinitions = new Hashtable<String, WSDLDefinition>();

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
                if (importLocation == null || importLocation.startsWith("/")) {
                    return null;
                }
                URL url = new URL(new URL(parentLocation), importLocation);
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

    public WSDLDocumentProcessor(WSDLFactory factory, javax.wsdl.factory.WSDLFactory wsdlFactory) {
        this.factory = factory;

        if (wsdlFactory != null) {
            this.wsdlFactory = wsdlFactory;
        } else {
            try {
                this.wsdlFactory = javax.wsdl.factory.WSDLFactory.newInstance();
            } catch (WSDLException e) {
                throw new ContributionRuntimeException(e);
            }
        }

        wsdlExtensionRegistry = this.wsdlFactory.newPopulatedExtensionRegistry();
    }

    private void readInlineSchemas(Definition definition, WSDLDefinition wsdlDefinition) {
        Types types = definition.getTypes();
        if (types != null) {
            wsdlDefinition.getInlinedSchemas().setSchemaResolver(new URIResolverImpl());
            for (Object ext : types.getExtensibilityElements()) {
                if (ext instanceof Schema) {
                    Element element = ((Schema)ext).getElement();

                    XmlSchemaCollection schemaCollection = wsdlDefinition.getInlinedSchemas();
                    schemaCollection.setBaseUri(((Schema)ext).getDocumentBaseURI());

                    wsdlDefinition.getInlinedSchemas().read(element, element.getBaseURI());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public WSDLDefinition read(URL contributionURL, URI artifactURI, URL artifactURL) throws ContributionReadException {
        try {

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

            WSDLDefinition wsdlDefinition = loadedDefinitions.get(definition.getTargetNamespace());
            if (wsdlDefinition != null) {
                merge(wsdlDefinition.getDefinition(), definition);
            } else {
                wsdlDefinition = factory.createWSDLDefinition();
                wsdlDefinition.setDefinition(definition);
                loadedDefinitions.put(definition.getTargetNamespace(), wsdlDefinition);
            }

            //Read inline schemas 
            readInlineSchemas(definition, wsdlDefinition);

            //read the inline schemas for wsdl imports
            if (definition.getImports().size() > 0) {
                Iterator<Vector<Import>> importsIterator = definition.getImports().values().iterator();
                Vector<Import> imports = null;
                Import anImport = null;
                while (importsIterator.hasNext()) {
                    imports = importsIterator.next();
                    for (int count = 0; count < imports.size(); ++count) {
                        anImport = imports.elementAt(count);
                        // Read inline schemas 
                        if (anImport.getDefinition() != null) {
                            readInlineSchemas(anImport.getDefinition(), wsdlDefinition);
                        }
                    }
                }
            }

            return wsdlDefinition;

        } catch (WSDLException e) {
            throw new ContributionReadException(e);
        } catch (IOException e) {
            throw new ContributionReadException(e);
        }
    }

    public void resolve(WSDLDefinition model, ModelResolver resolver) throws ContributionResolveException {
        Definition definition = model.getDefinition();
        if (definition != null) {
            for (Object imports : definition.getImports().values()) {
                List importList = (List)imports;
                for (Object i : importList) {
                    Import imp = (Import)i;
                    if (imp.getDefinition() != null) {
                        continue;
                    }
                    if (imp.getLocationURI() == null) {
                        // We need to resolve it by QName
                        WSDLDefinition proxy = factory.createWSDLDefinition();
                        proxy.setUnresolved(true);
                        proxy.setNamespace(imp.getNamespaceURI());
                        WSDLDefinition resolved = resolver.resolveModel(WSDLDefinition.class, proxy);
                        if (resolved != null && !resolved.isUnresolved()) {
                            imp.setDefinition(resolved.getDefinition());
                        }
                    } else {
                        String location = imp.getLocationURI();
                        URI uri = URI.create(location);
                        if (uri.isAbsolute()) {
                            WSDLDefinition resolved;
                            try {
                                resolved = read(null, uri, uri.toURL());
                                imp.setDefinition(resolved.getDefinition());
                            } catch (Exception e) {
                                throw new ContributionResolveException(e);
                            }
                        } else {
                            if (location.startsWith("/")) {
                                // This is a relative URI against a contribution
                                location = location.substring(1);
                                // TODO: Need to resolve it against the contribution
                            } else {
                                // This is a relative URI against the WSDL document
                                URI baseURI = URI.create(model.getDefinition().getDocumentBaseURI());
                                URI locationURI = baseURI.resolve(location);
                                WSDLDefinition resolved;
                                try {
                                    resolved = read(null, locationURI, locationURI.toURL());
                                    imp.setDefinition(resolved.getDefinition());
                                } catch (Exception e) {
                                    throw new ContributionResolveException(e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public String getArtifactType() {
        return ".wsdl";
    }

    public Class<WSDLDefinition> getModelType() {
        return WSDLDefinition.class;
    }

    /**
     * Merge a set of WSDLs into a facade Definition
     * 
     * @param definitions
     * @return
     */
    private Definition merge(Definition target, Definition source) {
        for (Iterator j = source.getImports().values().iterator(); j.hasNext();) {
            List list = (List)j.next();
            for (Iterator k = list.iterator(); k.hasNext();)
                target.addImport((Import)k.next());
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
     * Resolve a definition by QName
     * 
     * @param name
     * @return
     */
    private Definition resolveDefinition(QName name) {
        return null;
    }

    /**
     * Resolve the undefined elements in the WSDL definition
     * 
     * @param definition
     */
    private void resolveDefinition(Definition definition) {
        if (definition == null)
            return;
        for (Iterator i = definition.getMessages().values().iterator(); i.hasNext();) {
            resolveElement(definition, i.next());
        }
        for (Iterator i = definition.getPortTypes().values().iterator(); i.hasNext();) {
            resolveElement(definition, i.next());
        }
        for (Iterator i = definition.getBindings().values().iterator(); i.hasNext();) {
            resolveElement(definition, i.next());
        }
        for (Iterator i = definition.getServices().values().iterator(); i.hasNext();) {
            resolveElement(definition, i.next());
        }
    }

    /**
     * @param elements
     * @param location
     */
    private Object resolveElement(Definition definition, Object element) {
        if (element == null)
            return null;
        QName name = null;
        if (element instanceof Binding) {
            Binding binding = (Binding)element;
            if (binding.isUndefined()) {
                name = binding.getQName();
                Definition resolvedDefinition = resolveDefinition(name);
                if (resolvedDefinition != null && resolvedDefinition != definition) {
                    Binding resovledBinding = resolvedDefinition.getBinding(name);
                    if (resovledBinding != null && resovledBinding != binding) {
                        binding = resovledBinding;
                        if (definition != null) {
                            definition.getBindings().put(name, binding);
                        }
                    }
                }
            }
            PortType portType = binding.getPortType();
            PortType resolvedPortType = (PortType)resolveElement(null, portType);
            if (resolvedPortType != null && resolvedPortType != portType) {
                portType = resolvedPortType;
                binding.setPortType(portType);
            }

            for (Iterator i = binding.getBindingOperations().iterator(); i.hasNext();) {
                BindingOperation bindingOperation = (BindingOperation)i.next();
                Operation operation = bindingOperation.getOperation();
                if (operation != null && operation.isUndefined()) {
                    String inputName =
                        bindingOperation.getBindingInput() == null ? null : bindingOperation.getBindingInput()
                            .getName();
                    String outputName =
                        bindingOperation.getBindingOutput() == null ? null : bindingOperation.getBindingOutput()
                            .getName();
                    Operation resolvedOperation =
                        (Operation)portType.getOperation(operation.getName(), inputName, outputName);
                    if (resolvedOperation != null && operation != resolvedOperation)
                        bindingOperation.setOperation(resolvedOperation);
                }
            }
            return binding;
        } else if (element instanceof Message) {
            Message message = (Message)element;
            if (message.isUndefined()) {
                name = message.getQName();
                Definition resolvedDefinition = resolveDefinition(name);
                if (resolvedDefinition != null && resolvedDefinition != definition) {
                    Message resolvedMessage = resolvedDefinition.getMessage(name);
                    if (resolvedMessage != null && resolvedMessage != message) {
                        message = resolvedMessage;
                        if (definition != null)
                            definition.getMessages().put(name, message);
                    }
                }
            }
            return message;
        } else if (element instanceof PortType) {
            PortType portType = (PortType)element;
            if (portType.isUndefined()) {
                name = portType.getQName();
                Definition resolvedDefinition = resolveDefinition(name);
                if (resolvedDefinition != null && resolvedDefinition != definition) {
                    PortType resolvedPortType = resolvedDefinition.getPortType(name);
                    if (resolvedPortType != null && resolvedPortType != portType) {
                        portType = resolvedPortType;
                        if (definition != null)
                            definition.getPortTypes().put(name, portType);
                    }
                }
            }
            List operations = new ArrayList(portType.getOperations());
            for (Iterator i = operations.iterator(); i.hasNext();) {
                Operation operation = (Operation)i.next();
                Operation resolvedOperation = (Operation)resolveElement(null, operation);
                if (resolvedOperation != null && resolvedOperation != operation) {
                    int index = portType.getOperations().indexOf(operation);
                    portType.getOperations().set(index, resolvedOperation);
                }
            }
            return portType;
        } else if (element instanceof Service) {
            Service service = (Service)element;
            name = service.getQName();
            for (Iterator j = service.getPorts().values().iterator(); j.hasNext();) {
                Port port = (Port)j.next();
                Binding binding = port.getBinding();
                Binding resolvedBinding = (Binding)resolveElement(null, binding);
                if (resolvedBinding != null && resolvedBinding != binding) {
                    port.setBinding(resolvedBinding);
                }
            }
            return service;
        } else if (element instanceof Operation) {
            Operation operation = (Operation)element;
            if (operation.getInput() != null) {
                Message message = operation.getInput().getMessage();
                Message resolvedMessage = (Message)resolveElement(null, message);
                if (resolvedMessage != null && resolvedMessage != message)
                    operation.getInput().setMessage(resolvedMessage);
            }
            if (operation.getOutput() != null) {
                Message message = operation.getOutput().getMessage();
                Message resolvedMessage = (Message)resolveElement(null, message);
                if (resolvedMessage != null && resolvedMessage != message)
                    operation.getOutput().setMessage(resolvedMessage);
            }
            for (Iterator j = operation.getFaults().values().iterator(); j.hasNext();) {
                Fault fault = (Fault)j.next();
                Message message = fault.getMessage();
                Message resolvedMessage = (Message)resolveElement(null, message);
                if (resolvedMessage != null && resolvedMessage != message)
                    fault.setMessage(resolvedMessage);
            }
            if (operation.isUndefined())
                operation.setUndefined(false);
            return operation;
        } else {
            return element;
        }

    }

}
