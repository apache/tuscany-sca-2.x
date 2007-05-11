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
package org.apache.tuscany.sca.core.event;

import java.net.URI;

import org.apache.tuscany.sca.event.Event;

/**
 * Propagated when a component stops
 *
 * @version $$Rev$$ $$Date$$
 */
public class ComponentStop implements Event {

    private Object source; 
    private URI uri;

    /**
     * Creates a component stop event
     *
     * @param source    the source of the event
     * @param componentUri the composite component associated the component being stopped
     */
    public ComponentStop(Object source, URI componentUri) {
        this.source = source;
        this.uri = componentUri;
    }

    public URI getComponentURI() {
        return uri;
    }
    
    public Object getSource() {
        return source;
    }
}
