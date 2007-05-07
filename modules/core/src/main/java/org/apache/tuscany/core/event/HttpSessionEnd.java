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
package org.apache.tuscany.core.event;

import org.apache.tuscany.event.Event;



/**
 * Propagated when an HTTP-based session is expired
 *
 * @version $$Rev$$ $$Date$$
 */
public class HttpSessionEnd implements Event {
    
    private Object source;
    private Object id;

    /**
     * Creates a new event
     *
     * @param source the source of the event
     * @param id     the id of the HTTP session being ended
     */
    public HttpSessionEnd(Object source, Object id) {
        this.source = source;
        this.id = id;
    }

    public Object getSource() {
        return source;
    }
    
    public Object getSessionID() {
        return id;
    }
}
