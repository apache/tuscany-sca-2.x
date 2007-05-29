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

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.xml.Constants;

public interface JMSBindingConstants {

    // Contants used when describing the JMS binding
    // model and for setting up defaults
    public static final String BINDING_JMS                       = "binding.jms";
    public static final QName  BINDING_JMS_QNAME                 = new QName(Constants.SCA10_NS, BINDING_JMS);    
    public final static String CORRELATE_MSG_ID                  = "requestmsgidtocorrelid";
    public final static String CORRELATE_CORRELATION_ID          = "requestcorrelidtocorrelid";
    public final static String CORRELATE_NONE                    = "none";
    public static final List<String> VALID_CORRELATION_SCHEMES   = Arrays.asList(new String[] {CORRELATE_MSG_ID, 
                                                                                               CORRELATE_CORRELATION_ID, 
                                                                                               CORRELATE_NONE});    
    public final static int    DESTINATION_TYPE_QUEUE            = 0;
    public final static int    DESTINATION_TYPE_TOPIC            = 1;
    public final static String CREATE_ALLWAYS                    = "allways";
    public final static String CREATE_NEVER                      = "never";    
    public final static String DEFAULT_DESTINATION_NAME          = "dynamicQueues/SCARequestQ";
    public final static String DEFAULT_RESPONSE_DESTINATION_NAME = "dynamicQueues/SCAResponseQ";    
    public final static String DEFAULT_CONNECTION_FACTORY_NAME   = "ConnectionFactory";    
    public final static String DEFAULT_CONTEXT_FACTORY_NAME      = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
    public final static String DEFAULT_JNDI_URL                  = "tcp://localhost:61616";
    public final static int    DEFAULT_TIME_TO_LIVE              = 10000; // in milli seconds
    public final static int    DEFAULT_PRIORITY                  = 1; 
    public static final String DEFAULT_RF_CLASSNAME              = JMSResourceFactorySimpleImpl.class.getName();    
    public static final String DEFAULT_MP_CLASSNAME              = JMSMessageProcessorImpl.class.getName();
    public static final String DEFAULT_OPERATION_PROP_NAME       = "scaOperationName";

}
