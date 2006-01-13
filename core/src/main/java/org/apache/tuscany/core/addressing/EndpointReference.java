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
package org.apache.tuscany.core.addressing;

import java.util.Map;

import org.apache.tuscany.core.message.handler.MessageHandler;
import org.apache.tuscany.model.assembly.ConfiguredPort;

/**
 * A representation of the model object '<em><b>Endpoint Reference</b></em>'.
 */
public interface EndpointReference extends org.osoa.sca.ServiceReference, MessageHandler {

    /**
     * Returns the endpoint address.
     *
     * @return The address.
     */
    String getAddress();

    /**
     * Sets the endpoint address.
     *
     * @param address The address.
     */
    void setAddress(String address);

    /**
     * Returns the QName of the WSDL portType associated with this endpoint reference.
     *
     * @return The QName of the portType.
     */
    String getPortTypeName();

    /**
     * Sets the QName of the WSDL portType associated with this endpoint reference.
     *
     * @param qname The QName of the portType.
     */
    void setPortTypeName(String qname);

    /**
     * Returns the QName of the WSDL service associated with this endpoint reference.
     *
     * @return The QName of the service.
     */
    String getServiceName();

    /**
     * Sets the QName of the WSDL service associated with this endpoint reference.
     *
     * @param qname The QName of the service.
     */
    void setServiceName(String qname);

    /**
     * Returns the name of the WSDL port associated with this endpoint reference.
     *
     * @return The name of the port.
     */
    String getPortName();

    /**
     * Sets the name of the WSDL port associated with this endpoint reference.
     *
     * @param name The name of the port.
     */
    void setPortName(String name);

    /**
     * Returns the endpoint reference parameters..
     *
     * @return The collection of reference parameters.
     */
    Map<String, Object> getReferenceParameters();

    /**
     * Returns the configured port corresponding to this endpoint reference.
     *
     * @return
     */
    ConfiguredPort getConfiguredPort();

    /**
     * Returns the configured port corresponding to this endpoint reference.
     *
     * @return
     */
    void setConfiguredPort(ConfiguredPort configuredPort);

    /**
     * Returns the message handler associated with this endpoint reference
     *
     * @return
     */
    MessageHandler getMessageHandler();

    /**
     * Sets the message handler associated with this endpoint reference
     *
     * @param messageHandler
     */
    void setMessageHandler(MessageHandler messageHandler);

} // EndpointReference
