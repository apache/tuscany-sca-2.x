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
package org.apache.tuscany.spi.services.discovery;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

/**
 * Defines the abstraction that allows runtimes participating 
 * in the domain to discover each other and broadcast liveness 
 * to the admin server that holds the domain's runtime physical 
 * model and the domain-wide assembly model..
 * 
 * @version $Revision$ $Date$
 *
 */
public interface DiscoveryService {
    
    /**
     * Sends a message to the specified runtime.
     * 
     * @param runtimeId Runtime id of recipient.
     * @param content Message content.
     */
    void sendMessage(String runtimeId, XMLStreamReader content);
    
    /**
     * Registers a listener for async messages.
     * 
     * @param messageType Message type that can be handled by the listener.
     * @param listener Recipient of the async message.
     */
    void registerListener(QName messageType, MessageListener listener);

}
