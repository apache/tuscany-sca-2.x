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
package org.apache.tuscany.binding.axis2.assembly;

import javax.wsdl.Definition;
import javax.wsdl.Port;

import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.common.resource.ResourceLoader;
import commonj.sdo.helper.TypeHelper;

/**
 * Represents a Web service binding.
 */
public interface WebServiceBinding extends Binding {

    /**
     * Set the URI of the WSDL port for this binding.
     *
     * @param portURI the URI of the WSDL port
     */
    void setPortURI(String portURI);

    /**
     * Returns the WSDL port defining this binding.
     */
    Port getWSDLPort();

    /**
     * Returns the WSDL definition containing the WSDL port.
     * @return the WSDL definition containing the WSDL port
     */
    Definition getWSDLDefinition();

    /**
     * Sets the WSDL port defining this binding.
     */
    void setWSDLPort(Port value);

    /**
     * Sets the WSDL definition containing the WSDL port.
     * @param definition
     */
    void setWSDLDefinition(Definition definition);

    TypeHelper getTypeHelper();

    void setTypeHelper(TypeHelper typeHelper);

    ResourceLoader getResourceLoader();

    void setResourceLoader(ResourceLoader resourceLoader);
}
