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
package org.apache.servicemix.sca.assembly;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.apache.tuscany.model.assembly.Binding;

public interface JbiBinding extends Binding {

    /**
     * Returns the URI of the WSDL port for this binding.
     * @return the URI of the WSDL port for this binding
     */
    String getPortURI();

    /**
     * Set the URI of the WSDL port for this binding.
     * @param portURI the URI of the WSDL port
     */
    void setPortURI(String portURI);
    
    /**
     * Returns the service name. 
     * @return the service name
     */
    QName getServiceName();
    
    /**
     * Returns the endpoint name.
     * @return the endpoint name
     */
    String getEndpointName();
    
    /**
     * Returns the interface name.
     * @returnthe interface name
     */
    QName getInterfaceName();
    
    /**
     * Returns the WSDL definition containing the WSDL port.
     * @return the WSDL definition containing the WSDL port
     */
    Definition getDefinition();

    /**
     * Returns the the WSDL service.
     * @return the WSDL service
     */
    Service getService();
    
    /**
     * Returns the WSDL port defining this binding.
     * @return the WSDL port defining this binding
     */
    Port getPort();
    
    /**
     * Returns the WSDL port type.
     * @return the WSDL port type
     */
    PortType getPortType();
    
}
