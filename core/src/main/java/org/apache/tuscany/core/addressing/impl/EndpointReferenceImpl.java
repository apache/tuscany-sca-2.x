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

import org.apache.tuscany.core.addressing.EndpointReference;
import org.apache.tuscany.core.invocation.MessageHandler;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.model.assembly.ConfiguredPort;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of EndpointReference.
 */
public class EndpointReferenceImpl implements EndpointReference {

    private ConfiguredPort configuredPort;
    private MessageHandler messageHandler;
    private String address;
    private String portTypeName;
    private String portName;
    private String serviceName;
    private Map<String, Object> referenceParameters;

    /**
     * @see org.apache.tuscany.core.addressing.EndpointReference#getAddress()
     */
    public String getAddress() {
        return address;
    }

    /**
     * @see org.apache.tuscany.core.addressing.EndpointReference#setAddress(java.lang.String)
     */
    public void setAddress(String value) {
        this.address=value;
    }

    /**
     * @see org.apache.tuscany.core.addressing.EndpointReference#getPortTypeName()
     */
    public String getPortTypeName() {
        return portTypeName;
    }

    /**
     * @see org.apache.tuscany.core.addressing.EndpointReference#setPortTypeName(java.lang.String)
     */
    public void setPortTypeName(String value) {
        this.portTypeName=value;
    }

    /**
     * @see org.apache.tuscany.core.addressing.EndpointReference#getServiceName()
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @see org.apache.tuscany.core.addressing.EndpointReference#getPortName()
     */
    public String getPortName() {
        return portName;
    }

    /**
     * @see org.apache.tuscany.core.addressing.EndpointReference#setServiceName(java.lang.String)
     */
    public void setServiceName(String value) {
        this.serviceName=value;
    }

    /**
     * @see org.apache.tuscany.core.addressing.EndpointReference#setPortName(java.lang.String)
     */
    public void setPortName(String portName) {
        this.portName=portName;
    }

    /**
     * @see org.apache.tuscany.core.addressing.EndpointReference#getReferenceParameters()
     */
    public Map<String, Object> getReferenceParameters() {
        if (referenceParameters==null)
            referenceParameters=new HashMap<String, Object>();
        return referenceParameters;
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
     * @see org.apache.tuscany.core.addressing.EndpointReference#setMessageHandler(org.apache.tuscany.core.invocation.MessageHandler)
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
     * @see org.apache.tuscany.core.invocation.MessageHandler#processMessage(org.apache.tuscany.core.message.Message)
     */
    public boolean processMessage(Message message) {
        return messageHandler.processMessage(message);
    }

}
