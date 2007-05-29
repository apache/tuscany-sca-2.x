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

import org.apache.tuscany.sca.assembly.Binding;

/**
 * A model for the JMS binding.
 *
 * @version $Rev$ $Date$
 */
public interface JMSBinding extends Binding {
    
    // XML Related operations
    public void setCorrelationScheme(String correlationScheme);
    public String getCorrelationScheme();
    
    public String getInitialContextFactoryName();
    public void setInitialContextFactoryName(String initialContextFactoryName);
    
    public String getJndiURL();
    public void setJndiURL(String jndiURL);
    
    public String getDestinationName();
    public void setDestinationName(String destinationName);
    
    public int getDestinationType();
    public void setDestinationType(int destinationType);    
    
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
    
    public int getResponseDestinationType(); 
    public void setResponseDestinationType(int type); 
    
    public String getresponseDestinationCreate();   
    public void setresponseDestinationCreate(String create);  
    
    public int getDeliveryMode();
    public void setDeliveryMode(int deliveryMode);
    
    public int getTimeToLive();
    public void setTimeToLive(int timeToLive);
    
    public int getPriority();
    public void setPriority(int priority);
    
    // other operations
    public String getJmsResourceFactoryName();
    public void setJmsResourceFactoryName(String jmsResourceFactoryName);
    public JMSResourceFactory getJmsResourceFactory();     
    
    public void setRequestMessageProcessorName(String name);
    public String getRequestMessageProcessorName();
    public JMSMessageProcessor getRequestMessageProcessor();    

    public void setResponseMessageProcessorName(String name);
    public String getResponseMessageProcessorName();
    public JMSMessageProcessor getResponseMessageProcessor();    
    
    public String getOperationSelectorPropertyName();
    public void setOperationSelectorPropertyName(String operationSelectorPropertyName);
    
    public String getOperationSelectorName();
    public void setOperationSelectorName(String operationSelectorName);
    
    public boolean getXMLFormat();
    public void setXMLFormat(boolean b);     
}
