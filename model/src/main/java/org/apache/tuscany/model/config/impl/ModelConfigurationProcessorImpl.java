/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.config.impl;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLResource;

import org.apache.tuscany.model.config.DocumentRoot;
import org.apache.tuscany.model.config.DynamicPackage;
import org.apache.tuscany.model.config.GeneratedPackage;
import org.apache.tuscany.model.config.ModelConfiguration;
import org.apache.tuscany.model.config.ModelConfigurationFactory;
import org.apache.tuscany.model.config.ModelConfigurationPackage;
import org.apache.tuscany.model.config.ModelConfigurationProcessor;
import org.apache.tuscany.model.config.ResourceFactory;
import org.apache.tuscany.model.config.URIMapping;
import org.apache.tuscany.model.util.XMLResourceFactoryImpl;
import org.apache.tuscany.common.resource.loader.ResourceLoader;

/**
 * A model config processor.
 */
public class ModelConfigurationProcessorImpl implements ModelConfigurationProcessor {
    private static final String CONFIG_FILE_NAME = "tuscany-model.config";

    private ModelConfiguration modelConfig;
    private ResourceSet resourceSet;
    private EPackage.Registry packageRegistry;
    private ResourceLoader bundleContext;

    /**
     * Constructor
     */
    public ModelConfigurationProcessorImpl() {
    }

    private ModelConfigurationProcessorImpl(ModelConfiguration config, ResourceLoader bundleContext) {
        modelConfig = config;
        resourceSet = config.eResource().getResourceSet();
        packageRegistry = resourceSet.getPackageRegistry();
        this.bundleContext = bundleContext;
    }

    /**
     * Process the model config for the given resourceSet and bundleContext
     */
    public ModelConfiguration process(final ResourceSet resourceSet, final ResourceLoader bundleContext) {

        // Create a resource for the model config and add it to the given resourceSet
        Resource modelConfigResource = new XMLResourceFactoryImpl().createResource(URI.createURI(CONFIG_FILE_NAME));
        DocumentRoot documentRoot = ModelConfigurationFactory.eINSTANCE.createDocumentRoot();
        final ModelConfiguration modelConfiguration = ModelConfigurationFactory.eINSTANCE.createModelConfiguration();
        documentRoot.setModelConfiguration(modelConfiguration);
        modelConfigResource.getContents().add(documentRoot);
        resourceSet.getResources().add(modelConfigResource);

        // Load all config files
        try {
            // SECURITY
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    for (Iterator i = bundleContext.getAllResources(CONFIG_FILE_NAME); i.hasNext();) {
                        URL url = (URL) i.next();

                        // Load each config file
                        final XMLResource resource = (XMLResource) new XMLResourceFactoryImpl().createResource(URI.createURI(url.toString()));
                        XMLResource.XMLMap xmlMap = (XMLResource.XMLMap) resource.getDefaultLoadOptions().get(XMLResource.OPTION_XML_MAP);
                        xmlMap.setNoNamespacePackage(ModelConfigurationPackage.eINSTANCE);
                        resource.load(null);

                        // Move all config entries to our single config object
                        ModelConfiguration c = ((DocumentRoot) resource.getContents().get(0)).getModelConfiguration();
                        modelConfiguration.getDynamicPackages().addAll(c.getDynamicPackages());
                        modelConfiguration.getGeneratedPackages().addAll(c.getGeneratedPackages());
                        modelConfiguration.getDynamicPackageLoaders().addAll(c.getDynamicPackageLoaders());
                        modelConfiguration.getResourceFactories().addAll(c.getResourceFactories());
                        modelConfiguration.getUriMappings().addAll(c.getUriMappings());
                    }
                    return null;
                }
            });
        } catch (PrivilegedActionException e1) {
            throw new WrappedException(e1);
        }

        // Process the configuration
        ModelConfigurationProcessorImpl processor = new ModelConfigurationProcessorImpl(modelConfiguration, bundleContext);
        processor.process();

        return modelConfiguration;
    }

    /**
     * Process the model configuration
     */
    private void process() {
        // Process the URI mappings
        List mappings = modelConfig.getUriMappings();
        for (int x = 0, size = mappings.size(); x < size; x++) {
            URIMapping mapping = (URIMapping) mappings.get(x);
            process(mapping);
        }

        // Process the resource factories
        List resourceFactories = modelConfig.getResourceFactories();
        for (int x = 0, size = resourceFactories.size(); x < size; x++) {
            ResourceFactory resourceFactory = (ResourceFactory) resourceFactories.get(x);
            process(resourceFactory);
        }

        // Process the generated packages
        List generatedPackages = modelConfig.getGeneratedPackages();
        for (int x = 0, size = generatedPackages.size(); x < size; x++) {
            GeneratedPackage generatedPackage = (GeneratedPackage) generatedPackages.get(x);
            process(generatedPackage);
        }

        // Process the dynamic packages
        List dynamicPackages = modelConfig.getDynamicPackages();
        for (int x = 0, size = dynamicPackages.size(); x < size; x++) {
            DynamicPackage dynamicPackage = (DynamicPackage) dynamicPackages.get(x);
            process(dynamicPackage);
        }

        // Preload generated packages
        for (int x = 0, size = generatedPackages.size(); x < size; x++) {
            GeneratedPackage generatedPackage = (GeneratedPackage) generatedPackages.get(x);
            if (generatedPackage.isPreLoad())
                packageRegistry.getEPackage(generatedPackage.getUri());
        }

        // Preload dynamic packages
        for (int x = 0, size = dynamicPackages.size(); x < size; x++) {
            DynamicPackage dynamicPackage = (DynamicPackage) dynamicPackages.get(x);
            if (dynamicPackage.isPreLoad())
                packageRegistry.getEPackage(dynamicPackage.getUri());
        }
    }

    /**
     * Process a URI mapping
     *
     * @param mapping
     */
    private void process(URIMapping mapping) {
        URI sourceURI = URI.createURI(mapping.getSource());
        URI targetURI = URI.createURI(mapping.getTarget());
        Map uriMap = resourceSet.getURIConverter().getURIMap();
        uriMap.put(sourceURI, targetURI);
    }

    /**
     * Process a resource factory
     *
     * @param resourceFactory
     */
    private void process(ResourceFactory resourceFactory) {
        if (resourceFactory.getExtension() != null) {
            ResourceFactoryDescriptorImpl descriptor = new ResourceFactoryDescriptorImpl(resourceFactory, bundleContext);
            Map extensionMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
            extensionMap.put(resourceFactory.getExtension(), descriptor);
        }
        if (resourceFactory.getProtocol() != null) {
            ResourceFactoryDescriptorImpl descriptor = new ResourceFactoryDescriptorImpl(resourceFactory, bundleContext);
            Map protocolMap = resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap();
            protocolMap.put(resourceFactory.getProtocol(), descriptor);
        }
    }

    /**
     * Process a generated package.
     *
     * @param generatedPackage
     */
    private void process(GeneratedPackage generatedPackage) {
        String uri = generatedPackage.getUri();
        EPackage.Descriptor descriptor = new GeneratedPackageDescriptorImpl(generatedPackage, bundleContext);
        register(uri, descriptor);
    }

    /**
     * Process a dynamic package.
     *
     * @param dynamicPackage
     */
    private void process(DynamicPackage dynamicPackage) {
        String uri = dynamicPackage.getUri();
        EPackage.Descriptor descriptor = new DynamicPackageDescriptorImpl(dynamicPackage, bundleContext);
        register(uri, descriptor);
    }

    /**
     * Register an EPackage
     *
     * @param uri
     * @param ePackage
     */
    private void register(String uri, Object ePackage) {
        Object original = packageRegistry.get(uri);
        if (original != null && original != ePackage) {
            if (!original.equals(ePackage)) {

                // Error if a package is already registered with the same URI and different content
                throw new IllegalArgumentException("Duplicate registration of uri " + uri);
            } else {

                // Ignore duplicate registration of exactly the same package content
            }
        } else
            packageRegistry.put(uri, ePackage);
    }

}