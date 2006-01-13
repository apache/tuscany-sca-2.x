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

/**
 * Implementations are responsible for tracking scope keys associated with the current request.
 * 
 * @version $Rev$ $Date$
 */
public interface EventContext {

    /* An event type fired when a request is first serviced in the runtime */
    public static final int REQUEST_START = 1;

    /* An event type fired when the runtime finishes servicing a request */
    public static final int REQUEST_END = 2;

    /* An event type fired when a session is set for the current context */
    public static final int SESSION_NOTIFY = 3;

    /* An event type fired when a session is invalidated in the runtime */
    public static final int SESSION_END = 4;

    /* An event type fired when the current deployment unit is initialized */
    public static final int MODULE_START = 5;

    /* An event type fired when the current deployment unit is quiesced */
    public static final int MODULE_STOP = 6;

    public static final int SYSTEM_START = 7;
    
    public static final int SYSTEM_STOP = 8;

    /* An identifier type associated with an HTTP session */
    public static final Object HTTP_SESSION = new Object();

    /**
     * Returns the unique key for the given identifier, e.g a session
     */
    public Object getIdentifier(Object type);

    /**
     * Sets the unique key for the given identifier, e.g a session
     */
    public void setIdentifier(Object type, Object identifier);

    /**
     * Clears the unique key for the given identifier, e.g a session
     */
    public void clearIdentifier(Object type);

}
