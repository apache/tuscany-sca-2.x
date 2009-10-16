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

package org.apache.tuscany.sca.builder.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.builder.BuilderContext;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.assembly.builder.Messages;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.definitions.Definitions;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * Configuration of binding URIs.
 *
 * @version $Rev$ $Date$
 */
public class StructuralURIBuilderImpl implements CompositeBuilder {

    public StructuralURIBuilderImpl(ExtensionPointRegistry registry) {
    }

    /**
     * If a binding name is not provided by the user, construct it based on the service
     * or reference name
     *
     * @param contract the service or reference
     */
    private void constructBindingNames(Contract contract, Monitor monitor) {
        List<Binding> bindings = contract.getBindings();
        Map<String, Binding> bindingMap = new HashMap<String, Binding>();
        for (Binding binding : bindings) {
            // set the default binding name if one is required
            // if there is no name on the binding then set it to the service or reference name
            if (binding.getName() == null) {
                binding.setName(contract.getName());
            }
            Binding existed = bindingMap.put(binding.getName(), binding);
            // Check that multiple bindings do not have the same name
            if (existed != null && existed != binding) {
                if (contract instanceof Service) {
                    Monitor.error(monitor, this, Messages.ASSEMBLY_VALIDATION, "MultipleBindingsForService", contract
                        .getName(), binding.getName());
                } else {
                    Monitor.error(monitor,
                                  this,
                                  Messages.ASSEMBLY_VALIDATION,
                                  "MultipleBindingsForReference",
                                  contract.getName(),
                                  binding.getName());
                }
            }
        }

        if (contract.getCallback() != null) {
            bindings = contract.getCallback().getBindings();
            bindingMap.clear();
            for (Binding binding : bindings) {
                // set the default binding name if one is required
                // if there is no name on the binding then set it to the service or reference name
                if (binding.getName() == null) {
                    binding.setName(contract.getName());
                }
                Binding existed = bindingMap.put(binding.getName(), binding);
                // Check that multiple bindings do not have the same name
                if (existed != null && existed != binding) {
                    if (contract instanceof Service) {
                        Monitor.error(monitor,
                                      this,
                                      Messages.ASSEMBLY_VALIDATION,
                                      "MultipleBindingsForServiceCallback",
                                      contract.getName(),
                                      binding.getName());
                    } else {
                        Monitor.error(monitor,
                                      this,
                                      Messages.ASSEMBLY_VALIDATION,
                                      "MultipleBindingsForReferenceCallback",
                                      contract.getName(),
                                      binding.getName());
                    }
                }
            }
        }
    }

    /**
     * Concatenate binding URI parts together based on Assembly Specification section 1.7.2
     *
     * @param baseURI the base of the binding URI
     * @param componentURI the middle part of the binding URI derived from the component name
     * @param bindingURI the end part of the binding URI
     * @param includeBindingName when set true the binding name part should be used
     * @param bindingName the binding name
     * @return the resulting URI as a string
     */
    private static String constructBindingURI(URI baseURI,
                                              URI componentURI,
                                              URI bindingURI,
                                              String serviceName,
                                              boolean includeBindingName,
                                              String bindingName) {
        String name = includeBindingName ? serviceName + "/" + bindingName : serviceName;
        String uriString;

        if (baseURI == null) {
            if (componentURI == null) {
                if (bindingURI != null) {
                    uriString = name + "/" + bindingURI.toString();
                } else {
                    uriString = name;
                }
            } else {
                if (bindingURI != null) {
                    if (bindingURI.toString().startsWith("/")) {
                        uriString = componentURI.resolve(bindingURI).toString();
                    } else {
                        uriString = componentURI.resolve(name + "/" + bindingURI).toString();
                    }
                } else {
                    uriString = componentURI.resolve(name).toString();
                }
            }
        } else {
            if (componentURI == null) {
                if (bindingURI != null) {
                    uriString = basedURI(baseURI, bindingURI).toString();
                } else {
                    uriString = basedURI(baseURI, URI.create(name)).toString();
                }
            } else {
                if (bindingURI != null) {
                    uriString = basedURI(baseURI, componentURI.resolve(bindingURI)).toString();
                } else {
                    uriString = basedURI(baseURI, componentURI.resolve(name)).toString();
                }
            }
        }

        // tidy up by removing any trailing "/"
        if (uriString.endsWith("/")) {
            uriString = uriString.substring(0, uriString.length() - 1);
        }

        URI uri = URI.create(uriString);
        if (!uri.isAbsolute()) {
            uri = URI.create("/").resolve(uri);
        }
        return uri.toString();
    }

    /**
     * Combine a URI with a base URI.
     *
     * @param baseURI
     * @param uri
     * @return
     */
    private static URI basedURI(URI baseURI, URI uri) {
        if (uri.getScheme() != null) {
            return uri;
        }
        String str = uri.toString();
        if (str.startsWith("/")) {
            str = str.substring(1);
        }
        return URI.create(baseURI.toString() + str).normalize();
    }

    public Composite build(Composite composite, BuilderContext context)
        throws CompositeBuilderException {
        configureStructuralURIs(composite, null, context.getDefinitions(), context.getBindingBaseURIs(), context.getMonitor());
        return composite;
    }

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.StructualURIBuilder";
    }

    private void configureStructuralURIs(Composite composite,
                                         String parentComponentURI,
                                         Definitions definitions,
                                         Map<QName, List<String>> defaultBindings,
                                         Monitor monitor) throws CompositeBuilderException {

        monitor.pushContext("Composite: " + composite.getName().toString());
        try {
            for (Service service : composite.getServices()) {
                constructBindingNames(service, monitor);
            }

            for (Reference reference : composite.getReferences()) {
                constructBindingNames(reference, monitor);
            }

            // Process nested composites recursively
            for (Component component : composite.getComponents()) {

                // Initialize component URI
                String componentURI;
                if (parentComponentURI == null) {
                    componentURI = component.getName();
                } else {
                    componentURI = parentComponentURI + '/' + component.getName();
                }
                component.setURI(componentURI);

                monitor.pushContext("Component: " + component.getName());
                try {
                    for (ComponentService service : component.getServices()) {
                        constructBindingNames(service, monitor);

                        /*
                        // Initialize binding names and URIs
                        for (Binding binding : service.getBindings()) {
                            constructBindingURI(componentURI, service, binding, defaultBindings, monitor);
                        }
                        */
                    }
                    for (ComponentReference service : component.getReferences()) {
                        constructBindingNames(service, monitor);
                    }
                } finally {
                    monitor.popContext();
                }

                Implementation implementation = component.getImplementation();
                if (implementation instanceof Composite) {
                    // Process nested composite
                    configureStructuralURIs((Composite)implementation,
                                            componentURI,
                                            definitions,
                                            defaultBindings,
                                            monitor);
                }
            }

        } finally {
            monitor.popContext();
        }
    }

}
