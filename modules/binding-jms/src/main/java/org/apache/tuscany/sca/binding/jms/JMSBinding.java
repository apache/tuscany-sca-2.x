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

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.Binding;

/**
 * This is the generic JMS binding type. The type is extensible so that JMS
 * binding implementers can add additional JMS provider-specific attributes and
 * elements although such extensions are not guaranteed to be portable across
 * runtimes.
 * 
 * @version $Rev$ $Date$
 */
public interface JMSBinding extends Binding, Base {
    /**
     * Identifies the correlation scheme used when sending reply or callback
     * messages. Valid values are "RequestMsgIDToCorrelID" (the default),
     * "RequestCorrelIDToCorrelID", and "None".
     * 
     * @return
     */
    CorrelationScheme getCorrelationScheme();

    void setCorrelationScheme(CorrelationScheme correlationScheme);

    /**
     * the name of the JNDI initial context factory
     * 
     * @return
     */
    String getInitialContextFactory();

    void setInitialContextFactory(String initialContextFactory);

    /**
     * The URL for the JNDI provider
     * 
     * @return
     */
    String getJndiURL();

    void setJndiURL(String jndiURL);

    /**
     * identifies a binding.jms element that is present in a definition
     * document, whose destination, connectionFactory, activationSpec and
     * resourceAdapter children are used to define the values for this binding.
     * In this case the corresponding elements must not be present within this
     * binding element
     * 
     * @return
     */
    ConnectionInfo getRequestConnection();

    void setRequestConnection(ConnectionInfo requestConnection);

    /**
     * identifies a binding.jms element that is present in a definition
     * document, whose response child element is used to define the values for
     * this binding. In this case no response element must be present within
     * this binding element
     * 
     * @return
     */
    ConnectionInfo getResponseConnection();

    void setResponseConnection(ConnectionInfo responseConnection);

    /**
     * identifies a binding.jms element that is present in a definition
     * document, whose operationProperties children are used to define the
     * values for this binding. In this case no operationProperties elements
     * must be present within this binding element
     * 
     * @return
     */
    OperationProperties getOperationProperties();

    void setOperationProperties(OperationProperties operationProperties);

}
