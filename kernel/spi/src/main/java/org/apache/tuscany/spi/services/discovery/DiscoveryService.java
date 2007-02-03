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
     * Sends a message to the specified runtime. The method returns a unique 
     * message id for the sent message. The consumers can use the message id for
     * correlating responses to sent messages.
     * 
     * @param runtimeId Runtime id of recipient.
     * @param content Message content.
     * @return The message id. 
     * @throws DiscoveryException In case of discovery errors.
     */
    int sendMessage(String runtimeId, XMLStreamReader content) throws DiscoveryException;
    
    /**
     * Broadcasts the messages to all runtimes in the domain.
     * 
     * @param content Message content.
     * @return The message id. 
     * @throws DiscoveryException In case of discovery errors.
     */
    int broadcastMessage(XMLStreamReader content) throws DiscoveryException;
    
    /**
     * Registers a request listener for async messages. Request listeners handle 
     * unslolicited async messages sent by recipients.
     * 
     * @param messageType Message type that can be handled by the listener.
     * @param listener Recipient of the async message.
     */
    void registerRequestListener(QName messageType, RequestListener listener);
    
    /**
     * Registers a response listener for async messages. Response listeners handle 
     * async meesages that are received in response to a request message that was 
     * originally sent.
     * 
     * @param messageType Message type that can be handled by the listener.
     * @param listener Recipient of the async message.
     */
    void registerResponseListener(QName messageType, ResponseListener listener);

}
