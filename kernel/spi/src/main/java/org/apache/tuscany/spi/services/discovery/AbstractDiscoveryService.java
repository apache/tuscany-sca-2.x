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

import org.apache.tuscany.host.RuntimeInfo;
import org.apache.tuscany.spi.annotation.Autowire;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

/**
 * Abstract implementation of the discovery service.
 * 
 * @version $Revision$ $Date$
 *
 */
public abstract class AbstractDiscoveryService implements DiscoveryService {
    
    /** Runtime info. */
    private RuntimeInfo runtimeInfo;
    
    /** Listeners. */
    private Map<QName, MessageListener> listenerMap = new ConcurrentHashMap<QName, MessageListener>();
    
    /**
     * Registers a listener for async messages.
     * 
     * @param meesageType Message type that can be handled by the listener.
     * @param listener Recipient of the async message.
     */
    public void registerListener(QName messageType, MessageListener listener) {
        listenerMap.put(messageType, listener);
    }
    
    /**
     * Sets the runtime info for the runtime using the discovery service.
     * 
     * @param runtimeInfo Runtime info for the runtime using the discovery service.
     */
    @Autowire
    public final void setRuntimeInfo(RuntimeInfo runtimeInfo) {
        this.runtimeInfo = runtimeInfo;
    }
    
    /**
     * Starts the discovery service.
     */
    @Init
    public final void start() {
        
        onStart();
        
        Runnable shutdownHook = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
        
    }
    
    /**
     * Stops the discovery service.
     */
    @Destroy
    public final void stop() {
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
     * Returns the listener for the specified message type.
     * 
     * @param messageType Message type for the incoming message.
     * @return Listeners inteersted in the message type.
     */
    public final MessageListener getListener(QName messageType) {
        return listenerMap.get(messageType);
    }
    
    /**
     * Required to be overridden by sub-classes.
     *
     */
    protected abstract void onStart();
    
    /**
     * Required to be overridden by sub-classes.
     *
     */
    protected abstract void onStop();
    
    /**
     * Shutdown hook.
     */
    private class ShutdownHook implements Runnable {

        public void run() {
            stop();
        }
        
    }

}
