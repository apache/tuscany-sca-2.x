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
package org.apache.tuscany.sca.binding.jms;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.assembly.Binding;

/**
 * Models a binding to a JMS resource.
 *
 * @version $Rev$ $Date$
 */

public interface JMSBinding extends Binding {

    public Object clone() throws CloneNotSupportedException;

    /**
     * Returns the binding URI.
     * 
     * @return the binding URI
     */
    public String getURI();

    /**
     * Sets the binding URI.
     * 
     * @param uri the binding URI
     */
    public void setURI(String uri);

    /**
     * Returns the binding name.
     * 
     * @return the binding name
     */
    public String getName();

    /**
     * Sets the binding name.
     * 
     * @param name the binding name
     */
    public void setName(String name);

    public boolean isUnresolved();

    public void setUnresolved(boolean unresolved);

    public List<Object> getExtensions();

    // Methods for getting/setting JMS binding model information
    // as derived from the XML of the binding.jms element

    public void setCorrelationScheme(String correlationScheme);

    public String getCorrelationScheme();

    public String getInitialContextFactoryName();

    public void setInitialContextFactoryName(String initialContextFactoryName);

    public String getJndiURL();

    public void setJndiURL(String jndiURL);

    public String getDestinationName();

    public void setDestinationName(String destinationName);

    public String getDestinationType();

    public void setDestinationType(String destinationType);

    public String getDestinationCreate();

    public void setDestinationCreate(String create);

    public String getConnectionFactoryName();

    public void setConnectionFactoryName(String connectionFactoryName);

    public String getConnectionFactoryCreate();

    public void setConnectionFactoryCreate(String create);

    public String getActivationSpecName();

    public void setActivationSpecName(String activationSpecName);

    public String getActivationSpecCreate();

    public void setActivationSpecCreate(String create);

    public String getResponseDestinationName();

    public void setResponseDestinationName(String name);

    public String getResponseDestinationType();

    public void setResponseDestinationType(String type);

    public String getResponseDestinationCreate();

    public void setResponseDestinationCreate(String create);

    public String getResponseConnectionFactoryName();

    public void setResponseConnectionFactoryName(String connectionFactoryName);

    public String getResponseConnectionFactoryCreate();

    public void setResponseConnectionFactoryCreate(String create);

    public String getResponseActivationSpecName();

    public void setResponseActivationSpecName(String activationSpecName);

    public String getResponseActivationSpecCreate();

    public void setResponseActivationSpecCreate(String create);

    public String getJmsResourceFactoryName();

    public void setJmsResourceFactoryName(String jmsResourceFactoryName);

    public void setRequestMessageProcessorName(String name);

    public String getRequestMessageProcessorName();

    public void setResponseMessageProcessorName(String name);

    public String getResponseMessageProcessorName();

    public String getOperationSelectorPropertyName();

    public void setOperationSelectorPropertyName(String operationSelectorPropertyName);

    public String getOperationSelectorName();

    public void setOperationSelectorName(String operationSelectorName);

    public String getReplyTo();

    public void setReplyTo(String replyTo);

    public String getJMSType();

    public void setJMSType(String jmsType);

    public String getJMSCorrelationId();

    public void setJMSCorrelationId(String jmsCorrelationId);

    public Boolean isdeliveryModePersistent();

    public void setJMSDeliveryMode(boolean persistent);

    public Integer getJMSPriority();

    public void setJMSPriority(int jmsPriority);

    public Long getJMSTimeToLive();

    public void setJMSTimeToLive(long timeToLive);

    public Set<String> getPropertyNames();

    public Object getProperty(String name);

    public void setProperty(String name, Object value);

    public Map<String, Object> getOperationProperties(String opName);

    public void setOperationProperty(String opName, String propName, Object value);

    public boolean hasNativeOperationName(String opName);

    public String getNativeOperationName(String opName);

    public void setNativeOperationName(String opName, String nativeOpName);

    public String getOperationJMSType(String opName);

    public void setOperationJMSType(String opName, String jmsType);

    public String getOperationJMSCorrelationId(String opName);

    public void setOperationJMSCorrelationId(String opName, String jmsCorrelationId);

    public Boolean getOperationJMSDeliveryMode(String opName);

    public void setOperationJMSDeliveryMode(String opName, boolean b);

    public Long getOperationJMSTimeToLive(String opName);

    public void setOperationJMSTimeToLive(String opName, Long ttl);

    public Integer getOperationJMSPriority(String opName);

    public void setOperationJMSPriority(String opName, int p);

    public String getJMSSelector();

    public void setJMSSelector(String jmsSelector);

    public String getRequestConnectionName();

    public void setRequestConnectionName(String requestConnectionName);

    public void setResponseConnectionName(String responseConnectionName);

    public String getResponseConnectionName();

    public void setRequestConnectionBinding(JMSBinding binding);

    public JMSBinding getRequestConnectionBinding();

    public void setResponseConnectionBinding(JMSBinding binding);

    public JMSBinding getResponseConnectionBinding();
}
