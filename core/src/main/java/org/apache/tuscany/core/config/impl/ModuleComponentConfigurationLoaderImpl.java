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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.ModuleComponentConfigurationLoader;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;

/**
 */
public class ModuleComponentConfigurationLoaderImpl implements ModuleComponentConfigurationLoader {
    
    private static final String SCA_MODULE_FILE_NAME = "sca.module";
    //FIXME can fragments have a variable prefix name?
    private static final String SCA_FRAGMENT_FILE_NAME = "sca.fragment";
    private static final String SYSTEM_MODULE_FILE_NAME = "system.module";
    //FIXME can fragments have a variable prefix name?
    private static final String SYSTEM_FRAGMENT_FILE_NAME = "system.fragment";
    
    private AssemblyModelContext modelContext;
    private ResourceLoader resourceLoader;
    private AssemblyFactory assemblyFactory;
    private AssemblyModelLoader modelLoader;
    
    /**
     * Constructor
     */
    public ModuleComponentConfigurationLoaderImpl(AssemblyModelContext modelContext) {
        this.modelContext=modelContext;
        this.modelLoader=this.modelContext.getAssemblyLoader();
        this.assemblyFactory=this.modelContext.getAssemblyFactory();
        this.resourceLoader=this.modelContext.getApplicationResourceLoader();
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.loader.AssemblyModelLoader#loadModuleComponent(java.lang.String, java.lang.String)
     */
    public ModuleComponent loadSystemModuleComponent(String name, String uri) throws ConfigurationLoadException {
        return loadModuleComponent(SYSTEM_MODULE_FILE_NAME, SYSTEM_FRAGMENT_FILE_NAME, name, uri);
    }

    /**
     * @see org.apache.tuscany.model.assembly.loader.AssemblyModelLoader#loadModuleComponent(java.lang.String, java.lang.String)
     */
    public ModuleComponent loadModuleComponent(String name, String uri) throws ConfigurationLoadException {
        return loadModuleComponent(SCA_MODULE_FILE_NAME, SCA_FRAGMENT_FILE_NAME, name, uri);
    }

    /**
     * Load a module component.
     */
    private ModuleComponent loadModuleComponent(String moduleFileName, String fragmentFileName, String name, String uri) throws ConfigurationLoadException {

        // Load the sca.module file
        URL moduleUrl;
        try {
            moduleUrl = resourceLoader.getResource(moduleFileName);
        } catch (IOException e) {
            throw new ConfigurationLoadException(moduleFileName, e);
        }
        if (moduleUrl == null) {
            throw new ConfigurationLoadException(moduleFileName);
        }
        String moduleUri=moduleUrl.toString();

        // Load the sca.fragment files
        Iterator<URL> i;
        try {
            i = resourceLoader.getAllResources(fragmentFileName);
        } catch (IOException e) {
            throw new ConfigurationLoadException(fragmentFileName, e);
        }
        List<String> moduleFragmentUris=new ArrayList<String>();
        for (; i.hasNext(); ) {
            URL url=i.next();
            moduleFragmentUris.add(url.toString());
        }
        
        return loadModuleComponent(name, uri, moduleUri, moduleFragmentUris);
    }

    /**
     * @see org.apache.tuscany.core.config.ModuleComponentConfigurationLoader#loadModuleComponent(java.lang.String, java.lang.String, java.lang.String)
     */
    public ModuleComponent loadModuleComponent(String name, String uri, String url) throws ConfigurationLoadException {
        return loadModuleComponent( name, uri, url, (Collection)null);
    }
    
    /**
     * @see org.apache.tuscany.core.config.ModuleComponentConfigurationLoader#loadModuleComponent(java.lang.String, java.lang.String, java.lang.String, java.util.Collection)
     */
    public ModuleComponent loadModuleComponent(String name, String uri, String moduleUri, Collection<String> moduleFragmentUris) throws ConfigurationLoadException {

        // Load the module file
        Module module=modelLoader.loadModule(moduleUri);

        // Load the sca.fragment files
        if (moduleFragmentUris!=null) {
            for (String moduleFragmentUri : moduleFragmentUris) {
                ModuleFragment moduleFragment=modelLoader.loadModuleFragment(moduleFragmentUri);
                module.getModuleFragments().add(moduleFragment);
            }
        }

        // Create the module component
        ModuleComponent moduleComponent=assemblyFactory.createModuleComponent();
        moduleComponent.setName(name);
        moduleComponent.setURI(uri);
        moduleComponent.setComponentImplementation(module);
        moduleComponent.initialize(modelContext);

        return moduleComponent;
    }
    
}
