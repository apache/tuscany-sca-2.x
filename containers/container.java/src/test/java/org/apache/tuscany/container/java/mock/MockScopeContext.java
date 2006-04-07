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
package org.apache.tuscany.container.java.mock;

import org.apache.tuscany.container.java.invocation.mock.SimpleTargetImpl;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.context.*;
import org.apache.tuscany.model.assembly.SimpleComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockScopeContext implements ScopeContext {

    Map<String, Object> components;

    public MockScopeContext() {
        components = new HashMap<String, Object>();
        components.put("foo", new SimpleTargetImpl());
        components.put("bar", new SimpleTargetImpl());
    }

    public MockScopeContext(Map<String,Object> instances) {
        components = instances;
    }

    
    public void start() {
    }

    public void stop() {
    }

    public void addListener(RuntimeEventListener listener) throws ContextRuntimeException {
    }

    public void removeListener(RuntimeEventListener listener) throws ContextRuntimeException {
    }

    public String getName() {
        return "Mock Scope Container";
    }

    public boolean isCacheable() {
        return false;
    }

    public int[] getEventTypes() {
        return null;
    }

    public AtomicContext getContext(String name) {
        return null;
    }

    public Object getInstance(QualifiedName name) throws ScopeRuntimeException {
        return components.get(name.getPartName());
    }

    public AtomicContext getContextByKey(String name, Object key) {
        return null;
    }

    public void setComponent(SimpleComponent component) throws ScopeRuntimeException {
    }

    public void removeContext(String name) throws ScopeRuntimeException {
    }

    public void removeContextByKey(String name, Object key) throws ScopeRuntimeException {
    }

    public SimpleComponent[] getComponents() {
        return null;
    }

    public void onEvent(int type, Object message) throws EventException {
    }


    public void registerFactories(List<ContextFactory<Context>> configurations) {
    } 

    public void registerFactory(ContextFactory<Context> configuration) {
    } 
    
    public int getLifecycleState(){
        return RUNNING;
    }


    public void setLifecycleState(int state) {
    }


    public void setName(String name) {
    }


}

