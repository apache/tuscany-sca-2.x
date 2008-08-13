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
package org.apache.tuscany.sca.binding.jms.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.assembly.Binding;

/**
 * Models a binding to a JMS resource.
 *
 * @version $Rev$ $Date$
 */

public class JMSBinding implements Binding {

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    // properties required to implement the Tuscany
    // binding extension SPI
    private String uri = null;
    private String name = null;
    private boolean unresolved = false;
    private List<Object> extensions = new ArrayList<Object>();

    // Properties required to describe the JMS binding model

    private String correlationScheme = JMSBindingConstants.CORRELATE_MSG_ID;
    private String initialContextFactoryName;
    private String jndiURL;

    private String destinationName = JMSBindingConstants.DEFAULT_DESTINATION_NAME;
    private String destinationType = JMSBindingConstants.DESTINATION_TYPE_QUEUE;
    private String destinationCreate = JMSBindingConstants.CREATE_IF_NOT_EXIST;

    private String connectionFactoryName = JMSBindingConstants.DEFAULT_CONNECTION_FACTORY_NAME;
    private String connectionFactoryCreate = JMSBindingConstants.CREATE_IF_NOT_EXIST;

    private String activationSpecName = null;
    private String activationSpecCreate = null;

    private String responseActivationSpecName = null;
    private String responseActivationSpecCreate = null;

    private String responseDestinationName = JMSBindingConstants.DEFAULT_RESPONSE_DESTINATION_NAME;
    private String responseDestinationType = JMSBindingConstants.DESTINATION_TYPE_QUEUE;
    private String responseDestinationCreate = JMSBindingConstants.CREATE_IF_NOT_EXIST;

    private String responseConnectionFactoryName = JMSBindingConstants.DEFAULT_CONNECTION_FACTORY_NAME;
    private String responseConnectionFactoryCreate = JMSBindingConstants.CREATE_IF_NOT_EXIST;

    // Provides the name of the factory that interfaces to the
    // JMS API for us.
    private String jmsResourceFactoryName = JMSBindingConstants.DEFAULT_RF_CLASSNAME;

    // Message processors used to deal with the request and response messages
    public String requestMessageProcessorName = JMSBindingConstants.DEFAULT_MP_CLASSNAME;
    public String responseMessageProcessorName = JMSBindingConstants.DEFAULT_MP_CLASSNAME;

    // The JMS message property used to hold the name of the operation being called
    private String operationSelectorPropertyName = JMSBindingConstants.DEFAULT_OPERATION_PROP_NAME;

    // If the operation selector is derived automatically from the service interface it's stored here
    private String operationSelectorName = null;

    private String replyTo;
    private String jmsType;
    private String jmsCorrelationId;
    private Boolean deliveryModePersistent;
    private Long timeToLive;
    private Integer jmsPriority;

    private Map<String, Object> properties = new HashMap<String, Object>();
    private Map<String, Map<String, Object>> operationProperties = new HashMap<String, Map<String,Object>>();
    private Map<String, String> nativeOperationNames = new HashMap<String, String>();
    private Map<String, String> operationJMSTypes = new HashMap<String, String>();
    private Map<String, String> operationJMSCorrelationIds = new HashMap<String, String>();
    private Map<String, Boolean> operationJMSDeliveryModes = new HashMap<String, Boolean>();
    private Map<String, Long> operationJMSTimeToLives = new HashMap<String, Long>();
    private Map<String, Integer> operationJMSPriorities = new HashMap<String, Integer>();
    private String jmsSelector;
    
    public JMSBinding() {
        super();
    }

    /**
     * Returns the binding URI.
     * 
     * @return the binding URI
     */
    public String getURI() {
        return this.uri;
    }

    /**
     * Sets the binding URI.
     * 
     * @param uri the binding URI
     */
    public void setURI(String uri) {
        this.uri = uri;
    }

    /**
     * Returns the binding name.
     * 
     * @return the binding name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the binding name.
     * 
     * @param name the binding name
     */
    public void setName(String name) {
        this.name = name;
    }

    public boolean isUnresolved() {
        return this.unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    public List<Object> getExtensions() {
        return extensions;
    }

    // Methods for getting/setting JMS binding model information
    // as derived from the XML of the binding.jms element

    public void setCorrelationScheme(String correlationScheme) {
        this.correlationScheme = correlationScheme;
    }

    public String getCorrelationScheme() {
        return correlationScheme;
    }

    public String getInitialContextFactoryName() {
        return initialContextFactoryName;
    }

    public void setInitialContextFactoryName(String initialContextFactoryName) {
        this.initialContextFactoryName = initialContextFactoryName;
    }

    public String getJndiURL() {
        return this.jndiURL;
    }

    public void setJndiURL(String jndiURL) {
        this.jndiURL = jndiURL;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    public String getDestinationCreate() {
        return this.destinationCreate;
    }

    public void setDestinationCreate(String create) {
        this.destinationCreate = create;
    }

    public String getConnectionFactoryName() {
        return connectionFactoryName;
    }

    public void setConnectionFactoryName(String connectionFactoryName) {
        this.connectionFactoryName = connectionFactoryName;
    }

    public String getConnectionFactoryCreate() {
        return this.connectionFactoryCreate;
    }

    public void setConnectionFactoryCreate(String create) {
        this.connectionFactoryCreate = create;
    }

    public String getActivationSpecName() {
        return activationSpecName;
    }

    public void setActivationSpecName(String activationSpecName) {
        this.activationSpecName = activationSpecName;
    }

    public String getActivationSpecCreate() {
        return this.activationSpecCreate;
    }

    public void setActivationSpecCreate(String create) {
        this.activationSpecCreate = create;
    }

    public String getResponseDestinationName() {
        return this.responseDestinationName;
    }

    public void setResponseDestinationName(String name) {
        this.responseDestinationName = name;
    }

    public String getResponseDestinationType() {
        return this.responseDestinationType;
    }

    public void setResponseDestinationType(String type) {
        this.responseDestinationType = type;
    }

    public String getResponseDestinationCreate() {
        return this.responseDestinationCreate;
    }

    public void setResponseDestinationCreate(String create) {
        this.responseDestinationCreate = create;
    }

    public String getResponseConnectionFactoryName() {
        return responseConnectionFactoryName;
    }

    public void setResponseConnectionFactoryName(String connectionFactoryName) {
        this.responseConnectionFactoryName = connectionFactoryName;
    }

    public String getResponseConnectionFactoryCreate() {
        return this.responseConnectionFactoryCreate;
    }

    public void setResponseConnectionFactoryCreate(String create) {
        this.responseConnectionFactoryCreate = create;
    }

    public String getResponseActivationSpecName() {
        return responseActivationSpecName;
    }

    public void setResponseActivationSpecName(String activationSpecName) {
        this.responseActivationSpecName = activationSpecName;
    }

    public String getResponseActivationSpecCreate() {
        return this.responseActivationSpecCreate;
    }

    public void setResponseActivationSpecCreate(String create) {
        this.responseActivationSpecCreate = create;
    }

    public String getJmsResourceFactoryName() {
        return jmsResourceFactoryName;
    }

    public void setJmsResourceFactoryName(String jmsResourceFactoryName) {
        this.jmsResourceFactoryName = jmsResourceFactoryName;
    }

    public void setRequestMessageProcessorName(String name) {
        this.requestMessageProcessorName = name;
    }

    public String getRequestMessageProcessorName() {
        return requestMessageProcessorName;
    }

    public void setResponseMessageProcessorName(String name) {
        this.responseMessageProcessorName = name;
    }

    public String getResponseMessageProcessorName() {
        return responseMessageProcessorName;
    }

    public String getOperationSelectorPropertyName() {
        return operationSelectorPropertyName;
    }

    public void setOperationSelectorPropertyName(String operationSelectorPropertyName) {
        this.operationSelectorPropertyName = operationSelectorPropertyName;
    }

    public String getOperationSelectorName() {
        return operationSelectorName;
    }

    public void setOperationSelectorName(String operationSelectorName) {
        this.operationSelectorName = operationSelectorName;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getJMSType() {
        return jmsType;
    }
    public void setJMSType(String jmsType) {
        this.jmsType = jmsType;
    }

    public String getJMSCorrelationId() {
        return jmsCorrelationId;
    }
    
    public void setJMSCorrelationId(String jmsCorrelationId) {
        this.jmsCorrelationId = jmsCorrelationId;
    }

    public Boolean isdeliveryModePersistent() {
        return deliveryModePersistent;
    }
    public void setJMSDeliveryMode(boolean persistent) {
        this.deliveryModePersistent = Boolean.valueOf(persistent);
    }

    public Integer getJMSPriority() {
        return jmsPriority;
    }

    public void setJMSPriority(int jmsPriority) {
        this.jmsPriority = Integer.valueOf(jmsPriority);
    }

    public Long getJMSTimeToLive() {
        return timeToLive.longValue();
    }

    public void setJMSTimeToLive(long timeToLive) {
        this.timeToLive = Long.valueOf(timeToLive);
    }

    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    public Map<String, Object> getOperationProperties(String opName) {
        return operationProperties.get(opName);
    }

    public void setOperationProperty(String opName, String propName, Object value) {
        Map<String, Object> props = operationProperties.get(opName);
        if (props == null) {
            props = new HashMap<String, Object>();
            operationProperties.put(opName, props);
        }
        props.put(propName, value);
    }

    public boolean hasNativeOperationName(String opName) {
        return nativeOperationNames.containsKey(opName);
    }

    public String getNativeOperationName(String opName) {
        if (nativeOperationNames.containsKey(opName)) {
            return nativeOperationNames.get(opName);
        } else {
            return opName;
        }
    }

    public void setNativeOperationName(String opName, String nativeOpName) {
        this.nativeOperationNames .put(opName, nativeOpName);
    }

    public String getOperationJMSType(String opName) {
        if (operationJMSTypes.containsKey(opName)) {
            return operationJMSTypes.get(opName);
        } else {
            return jmsType;
        }
    }
    public void setOperationJMSType(String opName, String jmsType) {
        this.operationJMSTypes.put(opName, jmsType);
    }

    public String getOperationJMSCorrelationId(String opName) {
        if (operationJMSCorrelationIds.containsKey(opName)) {
            return operationJMSCorrelationIds.get(opName);
        } else {
            return jmsCorrelationId;
        }
    }
    public void setOperationJMSCorrelationId(String opName, String jmsCorrelationId) {
        operationJMSCorrelationIds.put(opName, jmsCorrelationId);
    }

    public Boolean getOperationJMSDeliveryMode(String opName) {
        if (operationJMSDeliveryModes.containsKey(opName)) {
            return operationJMSDeliveryModes.get(opName);
        } else {
            return deliveryModePersistent;
        }
    }
    public void setOperationJMSDeliveryMode(String opName, boolean b) {
        operationJMSDeliveryModes.put(opName, b);
    }

    public Long getOperationJMSTimeToLive(String opName) {
        if (operationJMSTimeToLives.containsKey(opName)) {
            return operationJMSTimeToLives.get(opName);
        } else {
            return timeToLive;
        }
    }
    public void setOperationJMSTimeToLive(String opName, Long ttl) {
        operationJMSTimeToLives.put(opName, ttl);
    }

    public Integer getOperationJMSPriority(String opName) {
        if (operationJMSPriorities.containsKey(opName)) {
            return operationJMSPriorities.get(opName);
        } else {
            return jmsPriority;
        }
    }
    public void setOperationJMSPriority(String opName, int p) {
        operationJMSPriorities.put(opName, p);
    }

    public String getJMSSelector() {
        return jmsSelector;
    }
    public void setJMSSelector(String jmsSelector) {
        this.jmsSelector = jmsSelector;
    }

}
