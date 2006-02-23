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
package org.apache.tuscany.core.config;

import java.util.Collection;

import org.apache.tuscany.model.assembly.ModuleComponent;

/**
 * Interface for loading configuration information from some external
 * form into a Tuscany logical model.
 *
 * @version $Rev: 368822 $ $Date: 2006-01-13 10:54:38 -0800 (Fri, 13 Jan 2006) $
 */
public interface ModuleComponentConfigurationLoader {

    /**
     * Load a SCDL module component.
     *
     * @param name      the name of the module component
     * @param uri
     * @return a new module component definition
     * @throws ConfigurationException if there was a problem loading the module component.
     */
    ModuleComponent loadModuleComponent(String name, String uri) throws ConfigurationLoadException;
    
    /**
     * Load a System SCDL module component.
     *
     * @param name      the name of the module component
     * @param uri
     * @return a new module component definition
     * @throws ConfigurationException if there was a problem loading the module component.
     */
    ModuleComponent loadSystemModuleComponent(String name, String uri) throws ConfigurationLoadException;
    
    /**
     * Load a SCDL module component.
     *
     * @param name      the name of the module component
     * @param uri
     * @param url
     * @return a new module component definition
     * @throws ConfigurationException if there was a problem loading the module component.
     */
    ModuleComponent loadModuleComponent(String name, String uri, String url) throws ConfigurationLoadException;
    
    /**
     * Load a SCDL module component.
     *
     * @param name      the name of the module component
     * @param uri
     * @param url
     * @param urls
     * @return a new module component definition
     * @throws ConfigurationException if there was a problem loading the module component.
     */
    ModuleComponent loadModuleComponent(String name, String uri, String url, Collection<String> urls) throws ConfigurationLoadException;
    
}
