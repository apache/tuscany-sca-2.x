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

package org.apache.tuscany.sca.sample.comet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.tuscany.sca.binding.comet.runtime.callback.CometCallback;
import org.apache.tuscany.sca.binding.comet.runtime.callback.Status;
import org.oasisopen.sca.ComponentContext;
import org.oasisopen.sca.annotation.Context;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

@Service({ EventProcessorConsumerService.class, EventProcessorProducerService.class })
@Scope("COMPOSITE")
public class EventProcessor implements EventProcessorConsumerService, EventProcessorProducerService {

    @Context
    protected ComponentContext componentContext;

    private ConcurrentMap<String, CometCallback> clients = new ConcurrentHashMap<String, CometCallback>();
    private Multimap<String, String> eventListeners = Multimaps.synchronizedMultimap(HashMultimap
            .<String, String> create());

    @Override
    public void onEvent(String eventName, String eventData) {
        // System.out.println("EventProcessor: Received event " + eventName +
        // "...");
        List<String> destinations = new ArrayList<String>();
        synchronized (eventListeners) {
            destinations.addAll(eventListeners.get(eventName));
        }
        Event event = new Event();
        event.setName(eventName);
        event.setData(eventData);
        for (String registrationId : destinations) {
            CometCallback client = clients.get(registrationId);
            if (client == null) {
                // client has unregistered from this event
                synchronized (eventListeners) {
                    eventListeners.remove(eventName, registrationId);
                }
            } else {
                Status status = client.sendMessage(event);
                if (status == Status.CLIENT_DISCONNECTED) {
                    unregister(registrationId);
                }
            }
        }
    }

    @Override
    public void register(String eventName) {
        String registrationId = UUID.randomUUID().toString();
        CometCallback callback = componentContext.getRequestContext().getCallback();
        clients.put(registrationId, callback);
        synchronized (eventListeners) {
            eventListeners.put(eventName, registrationId);
        }
        Event event = new Event();
        event.setId(registrationId);
        event.setName(eventName);
        event.setData(new Date().toString());
        callback.sendMessage(event);
    }

    @Override
    public void unregister(String registrationId) {
        clients.remove(registrationId);
        // unregistration from eventListeners done during onEvent
    }

    @Destroy
    public void shutdown() {
        clients.clear();
        eventListeners.clear();
        clients = null;
        eventListeners = null;
    }
}
