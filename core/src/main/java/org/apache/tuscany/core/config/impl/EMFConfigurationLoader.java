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
package org.apache.tuscany.core.config.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.apache.tuscany.common.resource.loader.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.ConfigurationLoader;
import org.apache.tuscany.core.config.InvalidRootElementException;
import org.apache.tuscany.core.config.MissingResourceException;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyLoader;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.ModuleFragment;

/**
 * Implementation of a {@link ConfigurationLoader} that uses EMF to load the XML documents.
 *
 * @version $Rev$ $Date$
 */
public class EMFConfigurationLoader implements ConfigurationLoader {
    private static final String SCA_MODULE_FILE_NAME = "sca.module";

    // @FIXME fragments can have a variable prefix name
    private static final String SCA_FRAGMENT_FILE_NAME = "sca.fragment";

    private final AssemblyModelContext modelContext;
    private final AssemblyFactory factory;
    private final AssemblyLoader loader;
    private final ResourceLoader resourceLoader;

    public EMFConfigurationLoader(AssemblyModelContext modelContext) {
        this.modelContext = modelContext;
        this.factory = modelContext.getAssemblyFactory();
        this.resourceLoader = modelContext.getResourceLoader();
        loader = modelContext.getAssemblyLoader();

    }

    public ModuleComponent loadModuleComponent(String name, String uri) throws ConfigurationException {
        // load the sca.module resource
        ModuleComponent moduleComponent;
        URL url;
        try {
            url = resourceLoader.getResource(SCA_MODULE_FILE_NAME);
        } catch (IOException e) {
            throw new ConfigurationLoadException(SCA_MODULE_FILE_NAME, e);
        }
        if (url == null) {
            throw new MissingResourceException(SCA_MODULE_FILE_NAME);
        }
        moduleComponent = loadModule(name, uri, url);

        // merge in any sca.fragment resources
        Iterator<URL> i;
        try {
            i = resourceLoader.getAllResources(SCA_FRAGMENT_FILE_NAME);
        } catch (IOException e) {
            throw new ConfigurationLoadException(SCA_FRAGMENT_FILE_NAME, e);
        }
        while (i.hasNext()) {
            url = i.next();
            mergeFragment(moduleComponent, url);
        }

        moduleComponent.getModuleImplementation().initialize(modelContext);
        moduleComponent.initialize(modelContext);

        return moduleComponent;
    }

    public ModuleComponent loadModule(String name, String uri, URL moduleXML) throws ConfigurationException {

        // load the XML document and validate the root element type
        Module module = loader.getModule(moduleXML.toString());
        if (module == null) {
            throw new InvalidRootElementException(moduleXML.toString(), "module");
        }

        ModuleComponent moduleComponent = factory.createModuleComponent();
        moduleComponent.setName(name);
        moduleComponent.setURI(uri);
        moduleComponent.setModuleImplementation(module);
        moduleComponent.setComponentImplementation(module); // TODO why do we need to set both Module and Implementation?
        return moduleComponent;
    }

    public void mergeFragment(ModuleComponent moduleComponent, URL fragmentXML) throws ConfigurationException {

        // load the XML document and validate the root element type
        ModuleFragment fragment = loader.getModuleFragment(fragmentXML.toString());
        if (fragment == null) {
            throw new InvalidRootElementException(fragmentXML.toString(), "moduleFragment");
        }
        fragment.initialize(modelContext);

        // Add the fragment to the module
        moduleComponent.getModuleImplementation().getModuleFragments().add(fragment);
    }

    public ComponentType loadComponentType(URL componentTypeXML) throws ConfigurationException {
        ComponentType componentType = loader.getComponentType(componentTypeXML.toString());
        if (componentType == null) {
            throw new InvalidRootElementException(componentTypeXML.toString(), "componentType");
        }
        componentType.initialize(modelContext);

        return componentType;
    }
}
