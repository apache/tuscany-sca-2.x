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
package org.apache.tuscany.persistence.store.journal.performance;

import java.util.Map;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.model.Scope;

/**
 * @version $Rev$ $Date$
 */
public class MockSCAObject implements SCAObject {
    public String getName() {
        return null;
    }

    public String getCanonicalName() {
        return "foo";
    }

    public CompositeComponent getParent() {
        return null;
    }

    public Scope getScope() {
        return null;
    }

    public void prepare() {

    }

    public Map<Object, Object> getExtensions() {
        return null;
    }

    public boolean isSystem() {
        return false;
    }

    public void publish(Event object) {

    }

    public void addListener(RuntimeEventListener listener) {

    }

    public void addListener(EventFilter filter, RuntimeEventListener listener) {

    }

    public void removeListener(RuntimeEventListener listener) {

    }

    public int getLifecycleState() {
        return 0;
    }

    public void start() throws CoreRuntimeException {

    }

    public void stop() throws CoreRuntimeException {

    }
}
