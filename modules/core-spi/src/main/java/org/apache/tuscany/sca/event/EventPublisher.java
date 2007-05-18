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
package org.apache.tuscany.sca.event;

/**
 * Publishes events in the runtime by accepting {@link Event} objects and 
 * forwarding them to all registered {@link RuntimeEventListener} objects.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface EventPublisher {

    /**
     * Publish an event to all regisitered listeners
     * @param object The event to publich
     */
    void publish(Event object);

    /**
     * Registers a listener to receive notifications for the context
     * @param listener The listener to add
     */
    void addListener(RuntimeEventListener listener);

    /**
     * Registers a listener to receive notifications for the context
     * @param filter The filter that will be applied before the lister is called
     * @param listener The lister to add
     */
    void addListener(EventFilter filter, RuntimeEventListener listener);


    /**
     * Removes a previously registered listener
     * @param listener The listener to remove
     */
    void removeListener(RuntimeEventListener listener);

}
