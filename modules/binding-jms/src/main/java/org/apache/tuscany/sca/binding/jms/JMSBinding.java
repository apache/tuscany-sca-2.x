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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.BindingRRB;
import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.OperationsConfigurator;
import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySubject;

/**
 * Models a binding to a JMS resource.
 *
 * @version $Rev$ $Date$
 */

//public class JMSBinding implements BindingRRB, PolicySubject, OperationsConfigurator, DefinitionElement {
public class JMSBinding implements BindingRRB, PolicySubject, OperationsConfigurator {
    QName TYPE = new QName(SCA11_NS, "binding.jms");

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    // properties required to implement the Tuscany binding extension SPI
    private String uri = null;
    private String name = null;
    private boolean unresolved = false;
    private List<Object> extensions = new ArrayList<Object>();
    
    // properties required by PolicySetAttachPoint
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private List<PolicySet> applicablePolicySets = new ArrayList<PolicySet>();
    
    // properties required by IntentAttachPoint 
    private List<Intent> requiredIntents = new ArrayList<Intent>();

    // properties required to describe configured operations
    private List<ConfiguredOperation>  configuredOperations = new ArrayList<ConfiguredOperation>();
    
    // properties required by DefinitionElement @575803A
    private String targetNamespace;

    // Properties required to describe the JMS binding model

    private String correlationScheme = JMSBindingConstants.CORRELATE_MSG_ID;
    private String initialContextFactoryName;
    private String jndiURL;

    private String destinationName = null;
    private String destinationType = JMSBindingConstants.DESTINATION_TYPE_QUEUE;
    private String destinationCreate = JMSBindingConstants.CREATE_IF_NOT_EXIST;
    private Map<String, BindingProperty> destinationProperties = new HashMap<String, BindingProperty>();

    private String connectionFactoryName = null;
    private String connectionFactoryCreate = JMSBindingConstants.CREATE_IF_NOT_EXIST;
    private Map<String, BindingProperty> connectionFactoryProperties = new HashMap<String, BindingProperty>();

    private String activationSpecName = null;
    private String activationSpecCreate = null;
    private Map<String, BindingProperty> activationSpecProperties = new HashMap<String, BindingProperty>();

    private String resourceAdapterName;;
    private Map<String, BindingProperty> resourceAdapterProperties = new HashMap<String, BindingProperty>();
    
    private String responseActivationSpecName = null;
    private String responseActivationSpecCreate = null;
    private Map<String, BindingProperty> responseActivationSpecProperties = new HashMap<String, BindingProperty>();

    private String responseDestinationName = null;
    private String responseDestinationType = JMSBindingConstants.DESTINATION_TYPE_QUEUE;
    private String responseDestinationCreate = JMSBindingConstants.CREATE_IF_NOT_EXIST;
    private Map<String, BindingProperty> responseDestinationProperties = new HashMap<String, BindingProperty>();

    private String responseConnectionFactoryName = null;
    private String responseConnectionFactoryCreate = JMSBindingConstants.CREATE_IF_NOT_EXIST;
    private Map<String, BindingProperty> responseConnectionFactoryProperties = new HashMap<String, BindingProperty>();

    // Provides the name of the factory that interfaces to the JMS API for us.
    private String jmsResourceFactoryName = JMSBindingConstants.DEFAULT_RF_CLASSNAME;

    // Message processors used to deal with the request and response messages
    public String requestMessageProcessorName = JMSBindingConstants.DEFAULT_MP_CLASSNAME;
    public String responseMessageProcessorName = JMSBindingConstants.DEFAULT_MP_CLASSNAME;

    // The JMS message property used to hold the name of the operation being called
    private String operationSelectorPropertyName = JMSBindingConstants.DEFAULT_OPERATION_PROP_NAME;

    // If the operation selector is derived automatically from the service interface it's stored here
    private String operationSelectorName = null;

    private boolean containsHeaders = false;
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
    private Map<String, Map<String, BindingProperty>> operationPropertiesProperties = new HashMap<String, Map<String,BindingProperty>>();

    private String jmsSelector;
    private QName requestConnectionName;
    private QName responseConnectionName;
    private QName operationPropertiesName;
    private JMSBinding requestConnectionBinding;
    private JMSBinding responseConnectionBinding;
    private JMSBinding operationPropertiesBinding;
    
    private WireFormat requestWireFormat;
    private WireFormat responseWireFormat;
    private OperationSelector operationSelector;
    private ExtensionType extensionType;
    
    public JMSBinding() {
        super();
    }

    // operations required by Binding 
    public String getURI() {
        return this.uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return this.name;
    }

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
    
    public WireFormat getRequestWireFormat() {
        return requestWireFormat;
    }
    
    public void setRequestWireFormat(WireFormat wireFormat) {
        this.requestWireFormat = wireFormat;
    }
    
    public WireFormat getResponseWireFormat() {
        return responseWireFormat;
    }
    
    public void setResponseWireFormat(WireFormat wireFormat) {
        this.responseWireFormat = wireFormat;
    }    
    
    public OperationSelector getOperationSelector() {
        return operationSelector;
    }
    
    public void setOperationSelector(OperationSelector operationSelector) {
        this.operationSelector = operationSelector;
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
        if (requestConnectionBinding != null && requestConnectionBinding.getDestinationName() != null) {
            return requestConnectionBinding.getDestinationName();
        } else {
            return destinationName;
        }
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDestinationType() {
        if (requestConnectionBinding != null && requestConnectionBinding.getDestinationType() != null) {
            return requestConnectionBinding.getDestinationType();
        } else {
            return destinationType;
        }
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    public String getDestinationCreate() {
        if (requestConnectionBinding != null && requestConnectionBinding.getDestinationCreate() != null) {
            return requestConnectionBinding.getDestinationCreate();
        } else {
            return this.destinationCreate;
        }
    }

    public void setDestinationCreate(String create) {
        this.destinationCreate = create;
    }

    public String getConnectionFactoryName() {
        if (requestConnectionBinding != null && requestConnectionBinding.getConnectionFactoryName() != null) {
            return requestConnectionBinding.getConnectionFactoryName();
        } else {
            return connectionFactoryName;
        }
    }

    public void setConnectionFactoryName(String connectionFactoryName) {
        this.connectionFactoryName = connectionFactoryName;
    }

    public String getConnectionFactoryCreate() {
        if (requestConnectionBinding != null && requestConnectionBinding.getConnectionFactoryCreate() != null) {
            return requestConnectionBinding.getConnectionFactoryCreate();
        } else {
            return this.connectionFactoryCreate;
        }
    }

    public void setConnectionFactoryCreate(String create) {
        this.connectionFactoryCreate = create;
    }

    public String getResourceAdapterName() {
        return resourceAdapterName;
    }

    public void setResourceAdapterName(String name) {
        resourceAdapterName = name;
    }

    public String getActivationSpecName() {
        if (requestConnectionBinding != null && requestConnectionBinding.getActivationSpecName() != null) {
            return requestConnectionBinding.getActivationSpecName();
        } else {
            return activationSpecName;
        }
    }

    public void setActivationSpecName(String activationSpecName) {
        this.activationSpecName = activationSpecName;
    }

    public String getActivationSpecCreate() {
        if (requestConnectionBinding != null && requestConnectionBinding.getActivationSpecCreate() != null) {
            return requestConnectionBinding.getActivationSpecCreate();
        } else {
            return this.activationSpecCreate;
        }
    }

    public void setActivationSpecCreate(String create) {
        this.activationSpecCreate = create;
    }

    public String getResponseDestinationName() {
        if (requestConnectionBinding != null && requestConnectionBinding.getResponseDestinationName() != null) {
            return requestConnectionBinding.getResponseDestinationName();
        } else {
            return this.responseDestinationName;
        }
    }

    public void setResponseDestinationName(String name) {
        this.responseDestinationName = name;
    }

    public String getResponseDestinationType() {
        if (requestConnectionBinding != null && requestConnectionBinding.getResponseDestinationType() != null) {
            return requestConnectionBinding.getResponseDestinationType();
        } else {
            return this.responseDestinationType;
        }
    }

    public void setResponseDestinationType(String type) {
        this.responseDestinationType = type;
    }

    public String getResponseDestinationCreate() {
        if (requestConnectionBinding != null && requestConnectionBinding.getResponseDestinationCreate() != null) {
            return requestConnectionBinding.getResponseDestinationCreate();
        } else {
            return this.responseDestinationCreate;
        }
    }

    public void setResponseDestinationCreate(String create) {
        this.responseDestinationCreate = create;
    }

    public String getResponseConnectionFactoryName() {
        if (requestConnectionBinding != null && requestConnectionBinding.getResponseConnectionFactoryName() != null) {
            return requestConnectionBinding.getResponseConnectionFactoryName();
        } else {
            return responseConnectionFactoryName;
        }
    }

    public void setResponseConnectionFactoryName(String connectionFactoryName) {
        this.responseConnectionFactoryName = connectionFactoryName;
    }

    public String getResponseConnectionFactoryCreate() {
        if (requestConnectionBinding != null && requestConnectionBinding.getResponseConnectionFactoryCreate() != null) {
            return requestConnectionBinding.getResponseConnectionFactoryCreate();
        } else {
            return this.responseConnectionFactoryCreate;
        }
    }

    public void setResponseConnectionFactoryCreate(String create) {
        this.responseConnectionFactoryCreate = create;
    }

    public String getResponseActivationSpecName() {
        if (requestConnectionBinding != null && requestConnectionBinding.getResponseActivationSpecName() != null) {
            return requestConnectionBinding.getResponseActivationSpecName();
        } else {
            return responseActivationSpecName;
        }
    }

    public void setResponseActivationSpecName(String activationSpecName) {
        this.responseActivationSpecName = activationSpecName;
    }

    public String getResponseActivationSpecCreate() {
        if (requestConnectionBinding != null && requestConnectionBinding.getResponseActivationSpecCreate() != null) {
            return requestConnectionBinding.getResponseActivationSpecCreate();
        } else {
            return this.responseActivationSpecCreate;
        }
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

    public void setHeaders( boolean containsHeaders ) {
        this.containsHeaders = containsHeaders;
    }

    public boolean containsHeaders() {
        return this.containsHeaders;
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
        setHeaders( true );
        this.jmsType = jmsType;
    }

    public String getJMSCorrelationId() {
        return jmsCorrelationId;
    }
    
    public void setJMSCorrelationId(String jmsCorrelationId) {
        setHeaders( true );
        this.jmsCorrelationId = jmsCorrelationId;
    }

    public Boolean isdeliveryModePersistent() {
        return deliveryModePersistent;
    }
    public void setJMSDeliveryMode(boolean persistent) {
        setHeaders( true );
        this.deliveryModePersistent = Boolean.valueOf(persistent);
    }

    public Integer getJMSPriority() {
        return jmsPriority;
    }

    public void setJMSPriority(int jmsPriority) {
        setHeaders( true );
        this.jmsPriority = Integer.valueOf(jmsPriority);
    }

    public Long getJMSTimeToLive() {
        return timeToLive;
    }

    public void setJMSTimeToLive(long timeToLive) {
        setHeaders( true );
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

    protected Map<String, Object> getProperties() {
        return properties;
    }
    
    /**
     * Adds an operationName to this binding.
     * @param opName
     */
    public void addOperationName(String opName) {
        Map<String, Object> props = operationProperties.get(opName);
        if (props == null) {
            props = new HashMap<String, Object>();
            operationProperties.put(opName, props);
        }
    }
    
    /**
     * Provides set of operation names in this binding.
     * @return a Set<String> of operation names
     */
    public Set<String> getOperationNames() {
        if (operationPropertiesBinding != null) {
            return operationPropertiesBinding.getOperationNames();
        } else {
            // Make a defensive copy since key changes affect map, map changes affect keys.
            Set<String> opNames = operationProperties.keySet();
            Set<String> opNamesCopy = new TreeSet<String>( opNames );
            return opNamesCopy;
        }
    }
    
    public Map<String, Object> getOperationProperties(String opName) {
        if (operationPropertiesBinding != null) {
            return operationPropertiesBinding.getOperationProperties(opName);
        } else {
            return operationProperties.get(opName);
        }
    }

    public void setOperationProperty(String opName, String propName, Object value) {
        Map<String, Object> props = operationProperties.get(opName);
        if (props == null) {
            props = new HashMap<String, Object>();
            operationProperties.put(opName, props);
        }
        props.put(propName, value);
    }

    /**
     * Provides the value of a property for a given operation
     * @param opName is the name of the operation in this binding.
     * @param propName is the key name for the property
     * @return Object representing the property value for this property name. Returns
     * null for non existant operation name or property name.
     */
    public Object getOperationProperty(String opName, String propName ) {
        if (operationPropertiesBinding != null) {
            return operationPropertiesBinding.getOperationProperty(opName, propName);
        } else {
            Map<String, Object> props = operationProperties.get(opName);
            if (props == null) { 
                return null;
            }
            return props.get(propName);
        }
    }

    public boolean hasNativeOperationName(String opName) {
        if (operationPropertiesBinding != null) {
            return operationPropertiesBinding.hasNativeOperationName(opName);
        } else {
            return nativeOperationNames.containsKey(opName);
        }
    }

    public String getNativeOperationName(String opName) {
        if (operationPropertiesBinding != null && operationPropertiesBinding.getNativeOperationName(opName) != null) {
            return operationPropertiesBinding.getNativeOperationName(opName);
        } else {
            if (nativeOperationNames.containsKey(opName)) {
                return nativeOperationNames.get(opName);
            } else {
                return opName;
            }
        }
    }

    public void setNativeOperationName(String opName, String nativeOpName) {
        this.nativeOperationNames .put(opName, nativeOpName);
    }

    public String getOperationJMSType(String opName) {
        if (operationPropertiesBinding != null && operationPropertiesBinding.getOperationJMSType(opName) != null) {
            return operationPropertiesBinding.getOperationJMSType(opName);
        } else {
            if (operationJMSTypes.containsKey(opName)) {
                return operationJMSTypes.get(opName);
            } else {
                return jmsType;
            }
        }
    }
    public void setOperationJMSType(String opName, String jmsType) {
        this.operationJMSTypes.put(opName, jmsType);
    }

    public String getOperationJMSCorrelationId(String opName) {
        if (operationPropertiesBinding != null) {
            if (operationPropertiesBinding.getOperationJMSCorrelationId(opName) != null) {
                return operationPropertiesBinding.getOperationJMSCorrelationId(opName);
            } else {
                return jmsCorrelationId;
            }
        } else {
            if (operationJMSCorrelationIds.containsKey(opName)) {
                return operationJMSCorrelationIds.get(opName);
            } else {
                return jmsCorrelationId;
            }
        }
    }
    public void setOperationJMSCorrelationId(String opName, String jmsCorrelationId) {
        operationJMSCorrelationIds.put(opName, jmsCorrelationId);
    }

    public Boolean getOperationJMSDeliveryMode(String opName) {
        if (operationPropertiesBinding != null) {
            if (operationPropertiesBinding.getOperationJMSDeliveryMode(opName) != null) {
                return operationPropertiesBinding.getOperationJMSDeliveryMode(opName);
            } else {
                return deliveryModePersistent;
            }
        } else {
            if (operationJMSDeliveryModes.containsKey(opName)) {
                return operationJMSDeliveryModes.get(opName);
            } else {
                return deliveryModePersistent;
            }
        }
    }
    public void setOperationJMSDeliveryMode(String opName, boolean b) {
        operationJMSDeliveryModes.put(opName, b);
    }

    public Long getOperationJMSTimeToLive(String opName) {
        if (operationPropertiesBinding != null) {
            if (operationPropertiesBinding.getOperationJMSTimeToLive(opName) != null) {
                return operationPropertiesBinding.getOperationJMSTimeToLive(opName);
            } else {
                return timeToLive;
            }
        } else {
            if (operationJMSTimeToLives.containsKey(opName)) {
                return operationJMSTimeToLives.get(opName);
            } else {
                return timeToLive;
            }
        }
    }
    public void setOperationJMSTimeToLive(String opName, Long ttl) {
        operationJMSTimeToLives.put(opName, ttl);
    }

    public Integer getOperationJMSPriority(String opName) {
        if (operationPropertiesBinding != null) {
            if (operationPropertiesBinding.getOperationJMSPriority(opName) != null) {
                return operationPropertiesBinding.getOperationJMSPriority(opName);
            } else {
                return jmsPriority;
            }
        } else {
            if (operationJMSPriorities.containsKey(opName)) {
                return operationJMSPriorities.get(opName);
            } else {
                return jmsPriority;
            }
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

    public QName getRequestConnectionName() {
        return requestConnectionName;
    }

    public void setRequestConnectionName(QName requestConnectionName) {
        this.requestConnectionName = requestConnectionName;
    }

    public void setResponseConnectionName(QName responseConnectionName) {
        this.responseConnectionName = responseConnectionName;
    }

    public QName getResponseConnectionName() {
        return responseConnectionName;
    }

    public void setRequestConnectionBinding(JMSBinding binding) {
        this.requestConnectionBinding = binding;
    }
    public JMSBinding getRequestConnectionBinding() {
        return requestConnectionBinding;
    }

    public void setResponseConnectionBinding(JMSBinding binding) {
        this.responseConnectionBinding = binding;
    }
    public JMSBinding getResponseConnectionBinding() {
        return responseConnectionBinding;
    }
    
    public void setOperationPropertiesName(QName nameValue) {
        this.operationPropertiesName = nameValue;
    }
    public QName getOperationPropertiesName() {
        return operationPropertiesName;
    }

    public void setOperationPropertiesBinding(JMSBinding binding) {
        this.operationPropertiesBinding = binding;
    }
    public JMSBinding getOperationPropertiesBinding() {
        return operationPropertiesBinding;
    }

    // operations required by PolicySetAttachPoint
    public List<PolicySet> getPolicySets() {
        return policySets;
    }
    
    public List<PolicySet> getApplicablePolicySets() {
        return applicablePolicySets;
    }     
    
    // operations required by IntentAttachPoint 
    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    } 

    public QName getType() {
        return TYPE;
    }
    
    public Map<String, BindingProperty> getDestinationProperties() {
        return destinationProperties;
    }

    public Map<String, BindingProperty> getConnectionFactoryProperties() {
        return connectionFactoryProperties;
    }

    public Map<String, BindingProperty> getResourceAdapterProperties() {
        return resourceAdapterProperties;
    }

    public Map<String, BindingProperty> getActivationSpecProperties() {
        return activationSpecProperties;
    }

    public Map<String, BindingProperty> getResponseActivationSpecProperties() {
        return responseActivationSpecProperties;
    }

    public Map<String, BindingProperty> getResponseDestinationProperties() {
        return responseDestinationProperties;
    }

    public Map<String, BindingProperty> getResponseConnectionFactoryProperties() {
        return responseConnectionFactoryProperties;
    }

    public Map<String, BindingProperty> getOperationPropertiesProperties(String opName) {
        if (operationPropertiesProperties.get(opName)==null) {
            operationPropertiesProperties.put(opName, new HashMap<String, BindingProperty>());
        }
        return operationPropertiesProperties.get(opName);
    }

    public List<ConfiguredOperation> getConfiguredOperations() {
        return configuredOperations;
    }

    public void setConfiguredOperations(List<ConfiguredOperation> configuredOperations) {
        this.configuredOperations = configuredOperations;
    }    
    
    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String ns) {
        targetNamespace = ns;
    }

    // hashCode() is here because binding elements in definitions documents are added
    // to the model resolver hashmap.  The namespace and name are keys.
    @Override
    public int hashCode() {
        return (String.valueOf(getTargetNamespace()) + String.valueOf(getName())).hashCode();
    }

    @Override
    public boolean equals( Object object ) {
        return ( object instanceof JMSBinding ) && equals( (JMSBinding) object );
    }

    /**
     * Compares two JMS bindings for equality.
     * Because of the many fields, this comparison is rather large O(n).
     * @param binding test binding for equality comparison 
     * @return boolean stating whether objects are equal
     */
    public boolean equals( JMSBinding binding ) {
        // If the target namespace is set, this binding came from a definitions document.
        // The target namespace and name are used as keys for doing model resolver hashmap lookups.
        // Only the target namespace and name can be compared.
        if (this.targetNamespace != null) {
            if ( !optStringEquals( this.targetNamespace, binding.getTargetNamespace() )) return false;
            if ( !optStringEquals( this.name, binding.getName() )) return false;
            return true;
        }

        // Test all fields for equality.
        // First test simple fields to quickly weed out mismatches.
        if ( !optStringEquals( this.uri, binding.getURI() )) return false;
        if ( !optStringEquals( this.name, binding.getName() )) return false;
        if ( !optStringEquals( this.targetNamespace, binding.getTargetNamespace() )) return false;
        if ( !optStringEquals( this.destinationName, binding.getDestinationName() )) return false;
        if ( !optStringEquals( this.correlationScheme, binding.getCorrelationScheme() )) return false;
        if ( !optStringEquals( this.initialContextFactoryName, binding.getInitialContextFactoryName() )) return false;
        if ( !optStringEquals( this.jndiURL, binding.getJndiURL() )) return false;
        if ( !optStringEquals( this.requestConnectionName, binding.getRequestConnectionName() )) return false;
        if ( !optStringEquals( this.responseConnectionName, binding.getResponseConnectionName() )) return false;
        if ( !optStringEquals( this.jmsSelector, binding.getJMSSelector() )) return false;
        if ( !equals( properties, binding.getProperties()) )
            return false;

        // Test operation properties
        Set<String> operationNamesA = this.getOperationNames();
        Set<String> operationNamesB = binding.getOperationNames();
        if ( operationNamesA != null && operationNamesB != null ) {
            if ( operationNamesA == null && operationNamesB != null ) return false;     
            if ( operationNamesA != null && operationNamesB == null ) return false;     
            if ( operationNamesA.size() != operationNamesB.size() ) return false;     
            for(Iterator<String> it=operationNamesA.iterator(); it.hasNext(); ) {
                String opName = it.next();
                if ( !operationNamesB.contains( opName )) {
                    return false;
                }
            }        
        }

        // Destination properties
        if ( !optStringEquals( this.getDestinationName(), binding.getDestinationName() )) return false;
        if ( !optStringEquals( this.getDestinationType(), binding.getDestinationType() )) return false;

        // Connection factory properties
        if ( !optStringEquals( this.getConnectionFactoryName(), binding.getConnectionFactoryName() )) return false;

        // Activation spec properties
        if ( !optStringEquals( this.getActivationSpecName(), binding.getActivationSpecName() )) return false;

        // Response properties
        if ( !optStringEquals( this.getResponseDestinationName(), binding.getResponseDestinationName() )) return false;
        if ( !optStringEquals( this.getResponseActivationSpecName(), binding.getResponseActivationSpecName() )) return false;
        if ( !optStringEquals( this.getResponseConnectionFactoryName(), binding.getResponseConnectionFactoryName() )) return false;

        // Resource adapter
        if ( !optStringEquals( this.getResourceAdapterName(), binding.getResourceAdapterName() )) return false;

        // Configured operations
        if ( this.configuredOperations.size() != binding.getConfiguredOperations().size() ) return false;
        
        // wire format
        if ( this.getRequestWireFormat().getClass() != binding.getRequestWireFormat().getClass()) return false;
        if ( this.getResponseWireFormat().getClass() != binding.getResponseWireFormat().getClass()) return false;
        
        // operation selector
        if ( this.getOperationSelector().getClass() != binding.getOperationSelector().getClass()) return false;
        
        
        // Other fields could also be checked for equality. See class fields for details.
        return true;
    }
    
    /**
     * Tests if Strings are equal. 
     * Either one may be null. This will match true if both
     * are null or both are non-null and equal.
     * @param p1 property list 1
     * @param p2 property list 2
     * @return whether or not properties are equal
     */
    public static boolean optStringEquals( Object s1, Object s2 ) {
        if ( s1 == null && s2 == null ) return true;
        if ( s1 != null && s2 == null ) return false;
        if ( s1 == null && s2 != null ) return false;
        return s1.equals( s2 );
    }
    
    /**
     * Tests if two property lists are equal.
     * Either one may be null. This will match true if both
     * are null or both are non-null and equal.
     * @param p1 property list 1
     * @param p2 property list 2
     * @return whether or not properties are equal
     */
    public static boolean equals( Map<String, Object> p1, Map<String, Object> p2 ) {
        if ( p1 == null && p2 == null)
            return true;
        if ( p1 == null || p2 == null)
            return false;
        if ( p1.size() != p2.size())
            return false;

        // For both the keys and values of a map
        for (Iterator it=p1.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            Object k1 = entry.getKey();
            Object v1 = entry.getValue();            
            Object v2 = p2.get( k1 );
            
            if ( v1 == null && v2 != null )
                return false;
            if ( v1 != null && v2 == null )
                return false;
            if ( !v1.equals( v2 ))
                return false;            
        }
        
        return true;
    }

    public ExtensionType getExtensionType() {
        return extensionType;
    }

    public void setExtensionType(ExtensionType intentAttachPointType) {
        this.extensionType = intentAttachPointType;
    }
}
