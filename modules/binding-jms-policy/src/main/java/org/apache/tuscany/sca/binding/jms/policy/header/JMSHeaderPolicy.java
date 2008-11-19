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
package org.apache.tuscany.sca.binding.jms.policy.header;

import java.util.Hashtable;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.policy.Policy;

/**
 * Implementation for policies that could be injected as parameter
 * into the axis2config.
 *
 * @version $Rev$ $Date$
 */
public class JMSHeaderPolicy implements Policy {
    public static final QName JMS_HEADER_POLICY_QNAME = new QName(Constants.SCA10_TUSCANY_NS, "jmsHeader");
    public static final String JMS_HEADER_JMS_TYPE = "JMSType";
    public static final String JMS_HEADER_JMS_CORRELATION_ID = "JMSCorrelationID";
    public static final String JMS_HEADER_JMS_DELIVERY_MODE = "JMSDeliveryMode";
    public static final String JMS_HEADER_JMS_TIME_TO_LIVE = "JMSTimeToLive";
    public static final String JMS_HEADER_JMS_PRIORITY = "JMSPriority";
    public static final String JMS_HEADER_JMS_PROPERTY = "property";
    public static final String JMS_HEADER_JMS_PROPERTY_NAME = "name";

    private String jmsType = null;
    private String jmsCorrelationId = null;
    private Boolean deliveryModePersistent = null;
    private Long timeToLive = null;
    private Integer jmsPriority = null;
    private Map<String, String> properties = new Hashtable<String, String>();
    
    public String getJmsType() {
        return jmsType;
    }
    
    public void setJmsType(String jmsType) {
        this.jmsType = jmsType;
    }
    
    public String getJmsCorrelationId() {
        return jmsCorrelationId;
    }
    
    public void setJmsCorrelationId(String jmsCorrelationId) {
        this.jmsCorrelationId = jmsCorrelationId;
    }
    
    public Boolean getDeliveryModePersistent() {
        return deliveryModePersistent;
    }
    
    public void setDeliveryModePersistent(Boolean deliveryModePersistent) {
        this.deliveryModePersistent = deliveryModePersistent;
    }
    
    public Long getTimeToLive() {
        return timeToLive;
    }
    
    public void setTimeToLive(Long timeToLive) {
        this.timeToLive = timeToLive;
    }
    
    public Integer getJmsPriority() {
        return jmsPriority;
    }
    
    public void setJmsPriority(Integer jmsPriority) {
        this.jmsPriority = jmsPriority;
    }
    
    public Map<String, String> getProperties() {
        return properties;
    }
  
    public QName getSchemaName() {
        return JMS_HEADER_POLICY_QNAME;
    }

    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean unresolved) {
    }
    
    @Override
    public String toString() {
        String result = "jmsHeader";
        
        result += " JMSType ";
        result += getJmsType();
        result += " JMSDeliveryMode ";
        result += getJmsCorrelationId();
        result += " JMSDeliveryMode ";
        result += getDeliveryModePersistent();
        result += " JMSTimeToLive ";
        result += getTimeToLive();
        result += " JMSPriority ";
        result += getJmsPriority();
        
        for (String propertyName : properties.keySet()){
            result += " property ";
            result += propertyName;
            result += " ";
            result += properties.get(propertyName);
        }
        
        return result;
    }
}
