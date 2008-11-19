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
package org.apache.tuscany.sca.store;

import org.apache.tuscany.sca.event.Event;
import org.apache.tuscany.sca.runtime.RuntimeComponent;

/**
 * Fired when a store implementation expires a resource
 *
 * @version $Rev$ $Date$
 */
public class StoreExpirationEvent implements Event {
    private Object source;
    private RuntimeComponent owner;
    private Object instance;

    /**
     * Constructor.
     *
     * @param source   the source of the event
     * @param owner    the owner of the expiring object
     * @param instance the expiring object
     */
    public StoreExpirationEvent(Object source, RuntimeComponent owner, Object instance) {
        assert source != null;
        assert owner != null;
        assert instance != null;
        this.source = source;
        this.owner = owner;
        this.instance = instance;
    }

    public Object getSource() {
        return source;
    }

    /**
     * Returns the owner of the expiring object.
     *
     * @return the owner of the expiring object.
     */
    public RuntimeComponent getOwner() {
        return owner;
    }

    /**
     * Returns the expiring object.
     *
     * @return the expiring object.
     */
    public Object getInstance() {
        return instance;
    }
}
