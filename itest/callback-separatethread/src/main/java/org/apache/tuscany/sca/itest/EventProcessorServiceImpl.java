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

package org.apache.tuscany.sca.itest;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.osoa.sca.CallableReference;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Sample Event Processor Service Implementation 
 */
@Service(EventProcessorService.class)
@Scope("CONVERSATION")
public class EventProcessorServiceImpl implements EventProcessorService {

    /**
     * Reference to the call back
     */
    @Callback
    protected CallableReference<EventProcessorCallBack> clientCallback;
    
    /**
     * This map contains the call backs for each of the registered Event names
     */
    private final Map<String, CallableReference<EventProcessorCallBack>> eventListeners; 
    
    /**
     * The list of all Event Generators we create
     */
    private final EventGenerator[] allEventGenerators;
    
    /**
     * Constructor. Starts the Event Generators
     */
    public EventProcessorServiceImpl() {
        eventListeners = new ConcurrentHashMap<String, CallableReference<EventProcessorCallBack>>();
        
        // We will simulate an Event generator
        allEventGenerators = new EventGenerator[2];
        allEventGenerators[0] = new EventGenerator("ONE", 1);      // Generate the SECOND event every second
        allEventGenerators[1] = new EventGenerator("FIVE", 5);     // Generate the FIVE event every 5 seconds
    }
    
    /**
     * Registers the client to receive notifications for the specified event
     * 
     * @param aEventName The name of the Event to register
     */
    public void registerForEvent(String aEventName)
    {
        // Register for the Event
        eventListeners.put(aEventName, clientCallback);
        
        // Send the "register" started event to the client
        receiveEvent(aEventName, "SameThread: Registered to receive notifications for " + aEventName);
    }

    /**
     * Unregisters the client so it no longer receives notifications for the specified event
     * 
     * @param aEventName The name of the Event to unregister
     */
    public void unregisterForEvent(String aEventName)
    {
        // Send the "register" started event to the client
        receiveEvent(aEventName, "SameThread: Unregister from receiving notifications for " + aEventName);
        
        eventListeners.remove(aEventName);
    }
    
    /**
     * This method is called whenever the EventProcessorService receives an Event
     * 
     * @param aEventName The name of the Event received
     * @param aEventData The Event data
     */
    private void receiveEvent(String aEventName, Object aEventData)
    {
        // Get the listener for the Event
        final CallableReference<EventProcessorCallBack> callback = eventListeners.get(aEventName);
        if (callback == null)
        {
            //System.out.println("No registered listeners for " + aEventName);
            return;
        }
        
        // Trigger the call back
        // System.out.println("Notifying " + callback + " of event " + aEventName);
        callback.getService().eventNotification(aEventName, aEventData);
        // System.out.println("Done notify " + callback + " of event " + aEventName);
    }
    
    /**
     * Shuts down the Event Processor
     */
    @Destroy
    public void shutdown()
    {
        System.out.println("Shutting down the EventProcessor");
        
        // Clear list of call back locations as we don't want to send any more notifications
        eventListeners.clear();

        // Stop the Event Generators
        for (EventGenerator generator : allEventGenerators)
        {
            generator.stop();
        }
    }
    
    /**
     * Utility class for generating Events
     */
    private class EventGenerator
    {
        /**
         * The Timer we are using to generate the events
         */
        private final Timer timer = new Timer();
        
        /**
         * Constructor
         *
         * @param aEventName The name of the Event to generate
         * @param frequencyInSeconds How frequently we should generate the Events
         */
        private EventGenerator(String aEventName, int frequencyInSeconds)
        {
            timer.schedule(new EventGeneratorTimerTask(aEventName), 
                    frequencyInSeconds * 1000, frequencyInSeconds * 1000);
        }
        
        /**
         * Stop this Event Generator
         */
        private void stop()
        {
            timer.cancel();
        }
        
        /**
         * The TimerTask that is invoked by the Timer for the EventGenerator
         */
        private class EventGeneratorTimerTask extends TimerTask
        {
            /**
             * The name of the Event we should generate
             */
            private final String eventName;

            /**
             * Constructor
             *
             * @param aEventName The name of the Event we should generate
             */
            private EventGeneratorTimerTask(String aEventName)
            {
                eventName = aEventName;
            }

            /**
             * Timer calls this method and it will generate an Event
             */
            @Override
            public void run()
            {
                // System.out.println("Generating new event " + eventName);
                receiveEvent(eventName, "Separate Thread Notification: " + UUID.randomUUID().toString());
            }
        }
    }
}
