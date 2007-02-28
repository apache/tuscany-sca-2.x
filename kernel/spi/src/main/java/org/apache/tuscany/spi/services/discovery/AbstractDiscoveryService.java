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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.host.RuntimeInfo;

/**
 * Abstract implementation of the discovery service.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public abstract class AbstractDiscoveryService implements DiscoveryService {

    /**
     * Runtime info.
     */
    private RuntimeInfo runtimeInfo;

    /**
     * Request listeners.
     */
    private Map<QName, RequestListener> requestListenerMap = new ConcurrentHashMap<QName, RequestListener>();

    /**
     * Response listeners.
     */
    private Map<QName, ResponseListener> responseListenerMap = new ConcurrentHashMap<QName, ResponseListener>();

    /**
     * Registers a request listener for async messages.
     *
     * @param messageType Message type that can be handled by the listener.
     * @param listener    Recipient of the async message.
     */
    public void registerRequestListener(QName messageType, RequestListener listener) {
        requestListenerMap.put(messageType, listener);
    }

    /**
     * Registers a response listener for async messages.
     *
     * @param messageType Message type that can be handled by the listener.
     * @param listener    Recipient of the async message.
     */
    public void registerResponseListener(QName messageType, ResponseListener listener) {
        responseListenerMap.put(messageType, listener);
    }

    /**
     * Sets the runtime info for the runtime using the discovery service.
     *
     * @param runtimeInfo Runtime info for the runtime using the discovery service.
     */
    @Reference
    public final void setRuntimeInfo(RuntimeInfo runtimeInfo) {
        this.runtimeInfo = runtimeInfo;
    }

    /**
     * Starts the discovery service.
     */
    @Init
    public final void start() throws DiscoveryException {
        onStart();
    }

    /**
     * Stops the discovery service.
     */
    @Destroy
    public final void stop() throws DiscoveryException {
        onStop();
    }

    /**
     * Gets the runtime info for the runtime using the discovery service.
     *
     * @return Runtime info for the runtime using the discovery service.
     */
    protected final RuntimeInfo getRuntimeInfo() {
        return runtimeInfo;
    }

    /**
     * Returns the request listener for the specified message type.
     *
     * @param messageType Message type for the incoming message.
     * @return Listener interested in the message type.
     */
    public final RequestListener getRequestListener(QName messageType) {
        return requestListenerMap.get(messageType);
    }

    /**
     * Returns the request listener for the specified message type.
     *
     * @param messageType Message type for the incoming message.
     * @return Listener interested in the message type.
     */
    public final ResponseListener getResponseListener(QName messageType) {
        return responseListenerMap.get(messageType);
    }

    /**
     * Broadcasts the messages to all runtimes in the domain.
     *
     * @param content Message content.
     * @return The message id.
     * @throws DiscoveryException In case of discovery errors.
     */
    public int broadcastMessage(XMLStreamReader content) throws DiscoveryException {
        return sendMessage(null, content);
    }

    /**
     * Required to be overridden by sub-classes.
     */
    protected abstract void onStart() throws DiscoveryException;

    /**
     * Required to be overridden by sub-classes.
     */
    protected abstract void onStop() throws DiscoveryException;

}
