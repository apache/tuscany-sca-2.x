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
package org.apache.tuscany.binding.jsonrpc.mocks.tuscany;

import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EventFilter;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.RuntimeEventListener;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.ScopeRuntimeException;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.context.event.Event;

public class MockScopeContext implements ScopeContext {

    private Map<String, Context> instanceContexts;

    public MockScopeContext(Map<String, Context> instanceContexts) {
        this.instanceContexts = instanceContexts;
    }

    public boolean isCacheable() {

        return false;
    }

    public void registerFactories(List<ContextFactory<Context>> configurations) {

    }

    public void registerFactory(ContextFactory<Context> configuration) {

    }

    public Context getContext(String name) {

        return instanceContexts.get(name);
    }

    public Context getContextByKey(String name, Object key) {

        return null;
    }

    public void removeContext(String name) throws ScopeRuntimeException {

    }

    public void removeContextByKey(String name, Object key) throws ScopeRuntimeException {

    }

    public String getName() {

        return null;
    }

    public void setName(String name) {

    }

    public int getLifecycleState() {

        return 0;
    }

    public void start() throws CoreRuntimeException {

    }

    public void stop() throws CoreRuntimeException {

    }

    public Object getInstance(QualifiedName qName) throws TargetException {

        return null;
    }

    public void publish(Event object) {

    }

    public void addListener(RuntimeEventListener listener) {

    }

    public void addListener(EventFilter filter, RuntimeEventListener listener) {

    }

    public void removeListener(RuntimeEventListener listener) {

    }

    public void onEvent(Event event) {

    }

}
