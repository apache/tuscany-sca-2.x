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


import org.osoa.sca.annotations.Remotable;

/**
 * The call back interface for the EventProcessorService that is implemented
 * by the client to receive event notifications
 */
@Remotable
public interface EventProcessorCallBack {
    /**
     * Call back notifying client of an Event
     * 
     * @param aEventName The name of the Event
     * @param aEventData The data for the Event
     */
    void eventNotification(String aEventName, Object aEventData);
}
