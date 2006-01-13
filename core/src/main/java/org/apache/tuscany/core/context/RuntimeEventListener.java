/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.context;

import java.util.EventListener;

/**
 * Listeners observe events fired in the SCA runtime.
 * 
 * @version $Rev$ $Date$
 */
public interface RuntimeEventListener extends EventListener {

    /**
     * A method called when an event for which the <tt>Listener</tt> class is registered to observe is fired in the runtime
     * 
     * @param type the event type identifier
     * @param message the event message
     * @throws EventException if an error occurs processing the event
     */
    public void onEvent(int type, Object message) throws EventException;
}
