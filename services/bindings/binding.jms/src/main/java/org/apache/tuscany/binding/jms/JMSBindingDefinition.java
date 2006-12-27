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
package org.apache.tuscany.binding.jms;

import javax.jms.DeliveryMode;

import org.apache.tuscany.spi.model.BindingDefinition;

/**
 * Represents a binding to a JMS resource.
 */

public class JMSBindingDefinition extends BindingDefinition {

    public final static int DESTINATION_TYPE_QUEUE = 0;
    public final static int DESTINATION_TYPE_TOPIC = 1;

    private int destinationType = DESTINATION_TYPE_QUEUE;

    private String destinationName;

    // Topic or Query factory name
    private String connectionFactoryName;

    private String activationSpecName;

    private String initialContextFactoryName; // "org.apache.activemq.jndi.ActiveMQInitialContextFactory"

    private String jNDIProviderURL; // "tcp://hostname:61616"

    // Maps to javax.jms.DeliveryMode
    private int deliveryMode = DeliveryMode.NON_PERSISTENT;

    private int timeToLive = 1000; // in mili seconds

    private int priority;

    private String replyTo;

    private String jmsResourceFactoryName;

    private String operationSelectorName;

    private String operationSelectorPropertyName = DEFAULT_OPERATION_PROP_NAME;

    private String correlationScheme;
    private String responseDestinationName;

    private String requestOperationAndDatabindingName = DEFAULT_ODB_CLASSNAME;
    private String responseOperationAndDatabindingName = DEFAULT_ODB_CLASSNAME;

    private boolean xmlFormat;

    public static final String DEFAULT_ODB_CLASSNAME = DefaultOperationAndDataBinding.class.getName();
    public static final String DEFAULT_OPERATION_PROP_NAME = "scaOperationName";

    public JMSBindingDefinition(int destinationType,
                      String destinationName,
                      String connectionFactoryName,
                      String activationSpecName,
                      String initialContextFactoryName,
                      String providerURL,
                      int deliveryMode,
                      int timeToLive,
                      int priority,
                      String replyTo) {
        super();
        this.destinationType = destinationType;
        this.destinationName = destinationName;
        this.connectionFactoryName = connectionFactoryName;
        this.activationSpecName = activationSpecName;
        this.initialContextFactoryName = initialContextFactoryName;
        this.jNDIProviderURL = providerURL;
        this.deliveryMode = deliveryMode;
        this.timeToLive = timeToLive;
        this.priority = priority;
        this.replyTo = replyTo;
    }

    public JMSBindingDefinition() {
        super();
    }

    public String getActivationSpecName() {
        return activationSpecName;
    }

    public void setActivationSpecName(String activationSpecName) {
        this.activationSpecName = activationSpecName;
    }

    public String getConnectionFactoryName() {
        return connectionFactoryName;
    }

    public void setConnectionFactoryName(String connectionFactoryName) {
        this.connectionFactoryName = connectionFactoryName;
    }

    public int getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(int deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getInitialContextFactoryName() {
        return initialContextFactoryName;
    }

    public void setInitialContextFactoryName(String initialContextFactoryName) {
        this.initialContextFactoryName = initialContextFactoryName;
    }

    public String getJNDIProviderURL() {
        return jNDIProviderURL;
    }

    public void setJNDIProviderURL(String providerURL) {
        jNDIProviderURL = providerURL;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public int getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(int destinationType) {
        this.destinationType = destinationType;
    }

    public String getJmsResourceFactoryName() {
        return jmsResourceFactoryName;
    }

    public void setJmsResourceFactoryName(String jmsResourceFactoryName) {
        this.jmsResourceFactoryName = jmsResourceFactoryName;
    }

    public String getOperationSelectorName() {
        return operationSelectorName;
    }

    public void setOperationSelectorName(String operationSelectorName) {
        this.operationSelectorName = operationSelectorName;
    }

    public String getOperationSelectorPropertyName() {
        return operationSelectorPropertyName;
    }

    public void setOperationSelectorPropertyName(String operationSelectorPropertyName) {
        this.operationSelectorPropertyName = operationSelectorPropertyName;
    }

    public void setCorrelationScheme(String correlationScheme) {
        this.correlationScheme = correlationScheme;
    }

    public String getCorrelationScheme() {
        return correlationScheme;
    }

    public void setCreateDestination(String create) {
    }

    public void setResponseDestinationName(String name) {
        this.responseDestinationName = name;
    }

    public String getResponseDestinationName() {
        return responseDestinationName;
    }

    public void setResponseDestinationType(int destination_type_queue2) {
        // TODO Auto-generated method stub

    }

    public void setCreateResponseDestination(String create) {
        // TODO Auto-generated method stub

    }

    public void setRequestOperationAndDatabindingName(String name) {
        this.requestOperationAndDatabindingName = name;
    }

    public String getRequestOperationAndDatabindingName() {
        return requestOperationAndDatabindingName;
    }

    public void setResponseOperationAndDatabindingName(String name) {
        this.responseOperationAndDatabindingName = name;
    }

    public String getResponseOperationAndDatabindingName() {
        return responseOperationAndDatabindingName;
    }

    public boolean isXMLFormat() {
        return xmlFormat;
    }

    public void setXMLFormat(boolean b) {
        this.xmlFormat = b;
    }

}
