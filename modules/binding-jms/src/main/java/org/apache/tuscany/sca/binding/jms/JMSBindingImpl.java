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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.jms.DeliveryMode;

import org.apache.tuscany.sca.binding.jms.JMSBindingException;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;


/**
 * Models a binding to a JMS resource.
 */

public class JMSBindingImpl implements JMSBinding {
    
    // properties required to implement the Tuscany 
    // binding extension SPI
    private String uri                   = null; 
    private String name                  = null;
    private boolean unresolved           = false;    
    private List<PolicySet> policySets   = new ArrayList<PolicySet>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();  
    private List<Object> extensions      = new ArrayList<Object>();    
    

    
    // Properties required to describe the JMS 
    // binding model
    
    // <binding.jms correlationScheme="string"?            Not yet implemented in binding     
    //              initialContextFactory="xs:anyURI"?     
    //              jndiURL="xs:anyURI"?                   
    //              requestConnection="QName"?             Not yet implemented in binding
    //              responseConnection="QName"?            Not yet implemented in binding
    //              operationProperties="QName"?           Not yet implemented in binding
    //              ...>
    private String correlationScheme         = JMSBindingConstants.CORRELATE_MSG_ID;    
    private String initialContextFactoryName = JMSBindingConstants.DEFAULT_CONTEXT_FACTORY_NAME;
    private String jndiURL                   = JMSBindingConstants.DEFAULT_JNDI_URL;  
    private String requestConnection         = null;
    private String responseConnection        = null;
    private String operationProperties       = null;
    // 
    //     <destination name="xs:anyURI"                   
    //                  type="string"?                     Not yet implemented in binding
    //                  create="string"?>                  Not yet implemented in binding
    //         <property name="NMTOKEN"                    Not yet implemented in binding
    //                   type="NMTOKEN">*                  Not yet implemented in binding
    //     </destination>?
    private String destinationName           = JMSBindingConstants.DEFAULT_DESTINATION_NAME; 
    private String destinationType           = JMSBindingConstants.DESTINATION_TYPE_QUEUE;    
    private String destinationCreate         = JMSBindingConstants.CREATE_NEVER; 
    // 
    //     <connectionFactory name="xs:anyURI"             Not yet implemented in binding
    //                        create="string"?>            Not yet implemented in binding
    //         <property name="NMTOKEN"                    Not yet implemented in binding
    //                   type="NMTOKEN">*                  Not yet implemented in binding
    //     </connectionFactory>?
    private String connectionFactoryName     = JMSBindingConstants.DEFAULT_CONNECTION_FACTORY_NAME;
    private String connectionFactoryCreate   = JMSBindingConstants.CREATE_NEVER;    
    // 
    //     <activationSpec name="xs:anyURI"                Not yet implemented in binding
    //                     create="string"?>               Not yet implemented in binding
    //         <property name="NMTOKEN"                    Not yet implemented in binding
    //                   type="NMTOKEN">*                  Not yet implemented in binding
    //     </activationSpec>?
    private String activationSpecName        = null;
    private String activationSpecCreate      = null;
    // 
    //     <response>
    //         <destination name="xs:anyURI"               
    //                      type="string"?                 Not yet implemented in binding
    //                      create="string"?>              Not yet implemented in binding
    //             <property name="NMTOKEN"                Not yet implemented in binding
    //                       type="NMTOKEN">*              Not yet implemented in binding
    //         </destination>?
    private String responseDestinationName   = JMSBindingConstants.DEFAULT_RESPONSE_DESTINATION_NAME; 
    private String responseDestinationType   = JMSBindingConstants.DESTINATION_TYPE_QUEUE;    
    private String responseDestinationCreate = JMSBindingConstants.CREATE_NEVER;    
    // 
    //         <connectionFactory name="xs:anyURI"         Not yet implemented in binding
    //                            create="string"?>        Not yet implemented in binding
    //             <property name="NMTOKEN"                Not yet implemented in binding
    //                       type="NMTOKEN">*              Not yet implemented in binding
    //         </connectionFactory>?
    private String responseConnectionFactoryName     = JMSBindingConstants.DEFAULT_CONNECTION_FACTORY_NAME;
    private String responseConnectionFactoryCreate   = JMSBindingConstants.CREATE_NEVER;    
    // 
    //         <activationSpec name="xs:anyURI"            Not yet implemented in binding
    //                         create="string"?>           Not yet implemented in binding
    //             <property name="NMTOKEN"                Not yet implemented in binding
    //                       type="NMTOKEN">*              Not yet implemented in binding
    //         </activationSpec>?
    private String responseActivationSpecName        = null;
    private String responseActivationSpecCreate      = null;    
    //     </response>?
    // 
    //     <resourceAdapter name="NMTOKEN">?               Not yet implemented in binding
    //         <property name="NMTOKEN"                    Not yet implemented in binding
    //                   type="NMTOKEN">*                  Not yet implemented in binding
    //     </resourceAdapter>?
    private String resourceAdapterName       = null;
    // 
    //     <headers JMSType="string"?                      Not yet implemented in binding
    //              JMSCorrelationId="string"?             Not yet implemented in binding
    //              JMSDeliveryMode="string"?              Not yet implemented in binding
    //              JMSTimeToLive="int"?                   Not yet implemented in binding
    //              JMSPriority="string"?>                 Not yet implemented in binding
    //         <property name="NMTOKEN"                    Not yet implemented in binding
    //                   type="NMTOKEN">*                  Not yet implemented in binding
    //     </headers>?
    private String jmsType                   = null;
    private String jmsCorrelationId          = null;
    private int    jmsDeliveryMode           = DeliveryMode.NON_PERSISTENT; // Maps to javax.jms.DeliveryMode
    private int    jmsTimeToLive             = JMSBindingConstants.DEFAULT_TIME_TO_LIVE;
    private int    jmsPriority               = JMSBindingConstants.DEFAULT_PRIORITY;    
    // 
    //     <operationProperties name="string"              Not yet implemented in binding
    //                          nativeOperation="string"?> Not yet implemented in binding
    //         <property name="NMTOKEN"                    Not yet implemented in binding
    //                   type="NMTOKEN">*                  Not yet implemented in binding
    //         <headers JMSType="string"?                  Not yet implemented in binding
    //                  JMSCorrelationId="string"?         Not yet implemented in binding
    //                  JMSDeliveryMode="string"?          Not yet implemented in binding
    //                  JMSTimeToLive="int"?               Not yet implemented in binding
    //                  JMSPriority="string"?>             Not yet implemented in binding
    //             <property name="NMTOKEN"                Not yet implemented in binding
    //                       type="NMTOKEN">*              Not yet implemented in binding
    //         </headers>?
    //     </operationProperties>*
    // </binding.jms>

    // Other properties not directly related to the
    // XML definition of the JMS binding
    
    
    // Provides the name of the factory that interfaces to the 
    // JMS API for us. 
    private String jmsResourceFactoryName                  = JMSBindingConstants.DEFAULT_RF_CLASSNAME;
    
    // Message processors used to deal with the request
    // and response messages
    public String requestMessageProcessorName              = JMSBindingConstants.DEFAULT_MP_CLASSNAME;
    public String responseMessageProcessorName             = JMSBindingConstants.DEFAULT_MP_CLASSNAME;
    
    // The JMS message property used to hold the name of the 
    // operation being called
    private String operationSelectorPropertyName           = JMSBindingConstants.DEFAULT_OPERATION_PROP_NAME;
    
    // If the operation selector is derived automatically from the service
    // interface it's stored here
    private String operationSelectorName                   = null;
    
    // Set true if messages are sent/received in XML format
    private boolean xmlFormat                              = false;    
    
    
//TODO .....  
    private String replyTo;


    // Methods required by the Tuscany SPI

    /**
     * No arg constructor used by the JSMBindingFactoryImpl
     * to create JMS binding model objects
     *
     */
    public JMSBindingImpl() {
        super();
    }
    
    /**
     * Returns the binding URI.
     * 
     * @return the binding uri
     */
    public String getURI() {
        return this.uri;
    }

    /**
     * Sets the binding URI.
     * 
     * @param uri the binding uri
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
    
    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public boolean isUnresolved() {
        return this.unresolved;
    }
    
    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }  

    public List<PolicySet> getPolicySets() {
        return policySets;
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
    
    public int getDeliveryMode() {
        return jmsDeliveryMode;
    }
    public void setDeliveryMode(int deliveryMode) {
        this.jmsDeliveryMode = deliveryMode;
    } 
    
    public int getTimeToLive() {
        return jmsTimeToLive;
    }
    public void setTimeToLive(int timeToLive) {
        this.jmsTimeToLive = timeToLive;
    } 
    
    public int getPriority() {
        return jmsPriority;
    }
    public void setPriority(int priority) {
        this.jmsPriority = priority;
    }    
    
    // operations to manage the other information required by the 
    // JMS binding
    
    public String getJmsResourceFactoryName() {
        return jmsResourceFactoryName;
    }
    public void setJmsResourceFactoryName(String jmsResourceFactoryName) {
        this.jmsResourceFactoryName = jmsResourceFactoryName;
    }    
    public JMSResourceFactory getJmsResourceFactory() {
        return (JMSResourceFactory)instantiate(null,jmsResourceFactoryName);
    }    
    
    public void setRequestMessageProcessorName(String name) {
        this.requestMessageProcessorName = name;
    }
    public String getRequestMessageProcessorName() {
        return requestMessageProcessorName;
    }
    public JMSMessageProcessor getRequestMessageProcessor() {
        return (JMSMessageProcessor)instantiate(null,requestMessageProcessorName);
    }    
    
    public void setResponseMessageProcessorName(String name) {
        this.responseMessageProcessorName = name;
    }
    public String getResponseMessageProcessorName() {
        return responseMessageProcessorName;
    }    
    public JMSMessageProcessor getResponseMessageProcessor() {
        return (JMSMessageProcessor)instantiate(null,responseMessageProcessorName);
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
    
    public boolean getXMLFormat() {
        return xmlFormat;
    }
    public void setXMLFormat(boolean b) {
        this.xmlFormat = b;
    }  
    
    /**
     * Used to create instances of the JMSResourceFactory
     * and RequestMessageProcessor and ResponseMessageProcessor
     * from sting based classname provided in the configuration
     * 
     * @param cl classloader 
     * @param className the string based classname to load and instantiate
     * @return the new object
     */
    protected Object instantiate(ClassLoader cl, String className) {
        Object instance;
        if (cl == null) {
            cl = this.getClass().getClassLoader();
        }

        try {
            Class clazz;
            
            try {
                clazz = cl.loadClass(className);
            } catch (ClassNotFoundException e) {
                clazz = this.getClass().getClassLoader().loadClass(className);
            }
            
            Constructor constructor = clazz.getDeclaredConstructor(new Class[]{JMSBinding.class});
            instance = constructor.newInstance(this);

        } catch (Throwable e) {
            throw new JMSBindingException("Exception instantiating OperationAndDataBinding class", e);
        }
        
        return instance;
    }   
    
    /**
     * The validation rules for the JMS model are relatively complicated to 
     * they all live together here
     */
    public void validate() throws JMSBindingException {
        /*
         * first fix up anything now the model 
         * has been read
         */
        
        if (getDestinationName().equals(JMSBindingConstants.DEFAULT_DESTINATION_NAME)){
            /*
             * No desitnation name has been set so make sure that
             * the runtime is able to create one automatically
             */
            setDestinationCreate(JMSBindingConstants.CREATE_ALLWAYS);
        }
        
        if (getResponseDestinationName().equals(JMSBindingConstants.DEFAULT_RESPONSE_DESTINATION_NAME)){
            /*
             * No repsonse desitination name has been set so make sure that
             * the runtime is able to create one automatically
             */
            setResponseDestinationCreate(JMSBindingConstants.CREATE_ALLWAYS);
        }   
        
        
        /*
         * Now some cross field validation
         */
        
        // connection factory doesn't contradict detsination type
        
        // connection factory and activation spec are mutually exclusive
        
        // TODO check spec for all validations
    }
    
    
// TODO...    


    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    

    
}
