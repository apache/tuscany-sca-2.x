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
package org.apache.tuscany.container.java.invocation.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.EventException;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.LifecycleEventListener;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.context.ScopeRuntimeException;
import org.apache.tuscany.core.context.SimpleComponentContext;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.model.assembly.SimpleComponent;

public class MockScopeContext implements ScopeContext {

    Map<String, Object> components;

    public MockScopeContext() {
        components = new HashMap();
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

    public String getName() {
        return "Mock Scope Container";
    }

    public boolean isCacheable() {
        return false;
    }

    public int[] getEventTypes() {
        return null;
    }

    public SimpleComponentContext getContext(String name) {
        return null;
    }

    public Object getInstance(QualifiedName name) throws ScopeRuntimeException {
        return components.get(name.getPartName());
    }

    public Object getInstance(QualifiedName componentName, boolean notify) throws TargetException {
        return getInstance(componentName);
    }
    
    public SimpleComponentContext getContextByKey(String name, Object key) {
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


    public void registerConfigurations(List<RuntimeConfiguration<InstanceContext>> configurations) {
    } 

    public void registerConfiguration(RuntimeConfiguration<InstanceContext> configuration) {
    } 
    
    public int getLifecycleState(){
        return RUNNING;
    }


    public void setLifecycleState(int state) {
    }


    public void setName(String name) {
    }


    public void addContextListener(LifecycleEventListener listener) {
    }


    public void removeContextListener(LifecycleEventListener listener) {
    }

    public Object getImplementationInstance() throws TargetException{
        return this;
    }

    public Object getImplementationInstance(boolean notify) throws TargetException{
        return this;
    }
    

}

