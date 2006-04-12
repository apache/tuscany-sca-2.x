/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.context;

import org.apache.tuscany.core.context.event.Event;

import java.util.EventObject;

/**
 * Publishes events in the runtime
 *
 * @version $$Rev$$ $$Date$$
 */
public interface EventPublisher {

    public void publish(Event object);

    /**
     * Registers a listener to receive notifications for the context
     */
    public void addListener(RuntimeEventListener listener);

    /**
     * Registers a listener to receive notifications for the context
     */
    public void addListener(EventFilter filter, RuntimeEventListener listener);


    /**
     * Removes a previously registered listener
     */
    public void removeListener(RuntimeEventListener listener);


}
