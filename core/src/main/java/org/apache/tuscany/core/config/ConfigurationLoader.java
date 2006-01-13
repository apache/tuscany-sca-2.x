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

import java.net.URL;

import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.ModuleComponent;

/**
 * Interface for loading configuration information from some external
 * form into a Tuscany logical model.
 *
 * @version $Rev$ $Date$
 */
public interface ConfigurationLoader {
    /**
     * Load a SCDL module document and create a ModuleComponent definition.
     *
     * @param name      the name of the module component
     * @param uri
     * @param moduleXML the location of the XML document
     * @return a new module component definition
     * @throws ConfigurationException if there was a problem loading the module
     */
    ModuleComponent loadModule(String name, String uri, URL moduleXML) throws ConfigurationException;

    /**
     * Load a SCDL moduleFragment document and merge it into an existing module component.
     *
     * @param moduleComponent the ModuleComponent definition
     * @param fragmentXML     the location of the XML document
     * @throws ConfigurationException if there was a problem loading the moduleFragment
     */
    void mergeFragment(ModuleComponent moduleComponent, URL fragmentXML) throws ConfigurationException;

    /**
     * Load a SCDL componentType document and create a ComponentType definition.
     *
     * @param componentTypeXML the location of the XML document
     * @return a new componentType definition
     * @throws ConfigurationException if there was a problem loading the componentType
     */
    ComponentType loadComponentType(URL componentTypeXML) throws ConfigurationException;

    ModuleComponent loadModuleComponent(String pName, String uri) throws ConfigurationException;
}
