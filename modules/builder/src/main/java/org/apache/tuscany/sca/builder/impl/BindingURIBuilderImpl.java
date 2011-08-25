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
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.SCABinding;
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
public class BindingURIBuilderImpl implements CompositeBuilder {

    private static final QName DEFAULT = new QName("default");

    public BindingURIBuilderImpl(ExtensionPointRegistry registry) {
    }

    /**
     * Called by CompositeBindingURIBuilderImpl
     *
     * @param composite the composite to be configured
     */
    public Composite build(Composite composite, BuilderContext context)
        throws CompositeBuilderException {
        configureBindingURIs(composite, null, context.getDefinitions(), context.getBindingBaseURIs(), context.getMonitor());
        return composite;
    }


    /**
     * Fully resolve the binding URIs based on available information. This includes information
     * from the ".composite" files, from resources associated with the binding, e.g. WSDL files,
     * from any associated policies and from the default information for each binding type.
     *
     * NOTE: This method repeats some of the processing performed by the configureComponents()
     *       method above.  The duplication is needed because NodeConfigurationServiceImpl
     *       calls this method without previously calling configureComponents().  In the
     *       normal builder sequence used by CompositeBuilderImpl, both of these methods
     *       are called.
     *
     * TODO: Share the URL calculation algorithm with the configureComponents() method above
     *       although keeping the configureComponents() methods signature as is because when
     *       a composite is actually build in a node the node default information is currently
     *       available
     *
     * @param composite the composite to be configured
     * @param uri the path to the composite provided through any nested composite component implementations
     * @param defaultBindings list of default binding configurations
     */
    private void configureBindingURIs(Composite composite,
                                      String uri,
                                      Definitions definitions,
                                      Map<QName, List<String>> defaultBindings,
                                      Monitor monitor) throws CompositeBuilderException {

        String parentComponentURI = uri;

        monitor.pushContext("Composite: " + composite.getName().toString());
        try {
            // Process nested composites recursively
            for (Component component : composite.getComponents()) {
                Implementation implementation = component.getImplementation();
                if (implementation instanceof Composite) {
                    // Process nested composite
                    configureBindingURIs((Composite)implementation, component.getURI(), definitions, defaultBindings, monitor);
                }
            }

            // Initialize composite service binding URIs
            List<Service> compositeServices = composite.getServices();
            for (Service service : compositeServices) {

                // Initialize binding names and URIs
                for (Binding binding : service.getBindings()) {
                    constructBindingURI(parentComponentURI, composite, service, binding, defaultBindings, monitor);
                }
            }

            // Initialize component service binding URIs
            for (Component component : composite.getComponents()) {

                monitor.pushContext("Component: " + component.getName());
                try {
                    for (ComponentService service : component.getServices()) {

                        // Initialize binding names and URIs
                        for (Binding binding : service.getBindings()) {
                            constructBindingURI(component, service, binding, defaultBindings, monitor);
                        }
                    }
                } finally {
                    monitor.popContext();
                }
            }
        } finally {
            monitor.popContext();
        }
    }

    /**
     * URI construction for composite bindings based on Assembly Specification section 1.7.2, This method
     * assumes that the component URI part of the binding URI is formed from the part to the
     * composite in question and just calls the generic constructBindingURI method with this
     * information
     *
     * @param parentComponentURI
     * @param composite
     * @param service
     * @param binding
     * @param defaultBindings
     */
    private void constructBindingURI(String parentComponentURI,
                                     Composite composite,
                                     Service service,
                                     Binding binding,
                                     Map<QName, List<String>> defaultBindings,
                                     Monitor monitor) throws CompositeBuilderException {
        // This is a composite service so there is no component to provide a component URI
        // The path to this composite (through nested composites) is used.
        constructBindingURI(parentComponentURI, service, binding, defaultBindings, monitor);
    }

    /**
     * URI construction for component bindings based on Assembly Specification section 1.7.2. This method
     * calculates the component URI part based on component information before calling the generic
     * constructBindingURI method
     *
     * @param component the component that holds the service
     * @param service the service that holds the binding
     * @param binding the binding for which the URI is being constructed
     * @param defaultBindings the list of default binding configurations
     */
    private void constructBindingURI(Component component,
                                     Service service,
                                     Binding binding,
                                     Map<QName, List<String>> defaultBindings,
                                     Monitor monitor) throws CompositeBuilderException {
        constructBindingURI(component.getURI(), service, binding, defaultBindings, monitor);
    }

    /**
     * Generic URI construction for bindings based on Assembly Specification section 1.7.2
     *
     * @param componentURIString the string version of the URI part that comes from the component name
     * @param service the service in question
     * @param binding the binding for which the URI is being constructed
     * @param includeBindingName when set true the serviceBindingURI part should be used
     * @param defaultBindings the list of default binding configurations
     * @throws CompositeBuilderException
     */
    private void constructBindingURI(String componentURIString,
                                     Service service,
                                     Binding binding,
                                     Map<QName, List<String>> defaultBindings,
                                     Monitor monitor) throws CompositeBuilderException {

        try {

            boolean includeBindingName = !service.getName().equals(binding.getName());

            // calculate the service binding URI
            URI bindingURI = binding.getURI() == null ? null : new URI(binding.getURI());
            if (binding instanceof SCABinding) {
                // Per assembly spec, the @uri for service side binding.sca should be ignored
                bindingURI = null;
            }
            
            // if the user has provided an absolute binding URI then use it
            if (bindingURI != null && bindingURI.isAbsolute()) {
                return;
            }

            String serviceName = service.getName();
            // Get the service binding name
            String bindingName;
            if (binding.getName() != null) {
                bindingName = binding.getName();
            } else {
                bindingName = serviceName;
            }

            // calculate the component URI
            URI componentURI = null;
            if (componentURIString != null) {
                componentURI = new URI(addSlashToPath(componentURIString));
            }

            // calculate the base URI
            URI baseURI = null;
            if (defaultBindings != null) {
                List<String> uris = defaultBindings.get(binding.getType());
                if (uris != null && uris.size() > 0) {
                    baseURI = new URI(addSlashToPath(uris.get(0)));
                } else {
                    uris = defaultBindings.get(DEFAULT);
                    if (uris != null && uris.size() > 0) {
                        baseURI = new URI(addSlashToPath(uris.get(0)));
                    }
                }
            }

            binding.setURI(constructBindingURI(baseURI,
                                               componentURI,
                                               bindingURI,
                                               serviceName,
                                               includeBindingName,
                                               bindingName));
        } catch (URISyntaxException ex) {
            Monitor.error(monitor,
                          this,
                          Messages.ASSEMBLY_VALIDATION,
                          "URLSyntaxException",
                          componentURIString,
                          service.getName(),
                          binding.getName());
        }
    }

    /**
     * Use to ensure that URI paths end in "/" as here we want to maintain the
     * last path element of an base URI when other URI are resolved against it. This is
     * not the default behaviour of URI resolution as defined in RFC 2369
     *
     * @param path the path string to which the "/" is to be added
     * @return the resulting path with a "/" added if it not already there
     */
    private static String addSlashToPath(String path) {
        if (path.endsWith("/") || path.endsWith("#")) {
            return path;
        } else {
            return path + "/";
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

    public String getID() {
        return "org.apache.tuscany.sca.assembly.builder.BindingURIBuilder";
    }

}
