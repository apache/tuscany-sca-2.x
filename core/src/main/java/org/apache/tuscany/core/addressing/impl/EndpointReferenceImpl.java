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
package org.apache.tuscany.core.addressing.impl;

import java.util.Map;

import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.addressing.sdo.impl.EndpointReferenceElementImpl;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.handler.MessageHandler;
import org.apache.tuscany.model.assembly.ConfiguredPort;

/**
 * An implementation of the model object '<em><b>Endpoint Reference</b></em>'.
 */
public class EndpointReferenceImpl extends EndpointReferenceElementImpl implements EndpointReference {

    private ConfiguredPort configuredPort;
    private MessageHandler messageHandler;

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#getAddress()
     */
    public String getAddress() {
        return super.getAddress();
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#setAddress(java.lang.String)
     */
    public void setAddress(String value) {
        super.setAddress(value);
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#getPortTypeName()
     */
    public String getPortTypeName() {
        return super.getPortTypeName();
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#setPortTypeName(java.lang.String)
     */
    public void setPortTypeName(String value) {
        super.setPortName(value);
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#getServiceName()
     */
    public String getServiceName() {
        return super.getServiceName();
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#getPortName()
     */
    public String getPortName() {
        return super.getPortName();
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#setServiceName(java.lang.String)
     */
    public void setServiceName(String value) {
        super.setServiceName(value);
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#setPortName(java.lang.String)
     */
    public void setPortName(String portName) {
        super.setPortName(portName);
    }

    /**
     * @see org.apache.tuscany.core.client.addressing.sdo.EndpointReferenceType#getReferenceParameters()
     */
    public Map<String, Object> getReferenceParameters() {
        return super.getReferenceParameters();
    }

    /**
     * @see org.apache.tuscany.core.addressing.EndpointReference#getConfiguredPort()
     */
    public ConfiguredPort getConfiguredPort() {
        return configuredPort;
    }

    /**
     * @see org.apache.tuscany.core.addressing.EndpointReference#setConfiguredPort(org.apache.tuscany.model.assembly.ConfiguredPort)
     */
    public void setConfiguredPort(ConfiguredPort configuredPort) {
        this.configuredPort = configuredPort;
    }

    /**
     * @see org.apache.tuscany.core.addressing.EndpointReference#getMessageHandler()
     */
    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    /**
     * @see org.apache.tuscany.core.addressing.EndpointReference#setMessageHandler(org.apache.tuscany.core.message.handler.MessageHandler)
     */
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * @see org.osoa.sca.ServiceReference#getSessionID()
     */
    public Object getSessionID() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.osoa.sca.ServiceReference#endSession()
     */
    public void endSession() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.osoa.sca.ServiceReference#getCallbackID()
     */
    public Object getCallbackID() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.osoa.sca.ServiceReference#setCallbackID(java.lang.Object)
     */
    public void setCallbackID(Object callbackID) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.osoa.sca.ServiceReference#getCallback()
     */
    public Object getCallback() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.osoa.sca.ServiceReference#setCallback(java.lang.Object)
     */
    public void setCallback(Object callback) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.apache.tuscany.core.message.handler.MessageHandler#processMessage(org.apache.tuscany.core.message.Message)
     */
    public boolean processMessage(Message message) {
        return messageHandler.processMessage(message);
    }

} //EndpointReferenceImpl
