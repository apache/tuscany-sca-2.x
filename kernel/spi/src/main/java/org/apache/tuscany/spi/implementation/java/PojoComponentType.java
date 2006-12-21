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
package org.apache.tuscany.spi.implementation.java;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * A component type specialization for POJO implementations
 *
 * @version $$Rev$$ $$Date$$
 */
public class PojoComponentType<S extends ServiceDefinition, R extends ReferenceDefinition,  P extends Property<?>>
    extends ComponentType<S, R, P> {

    private Scope implementationScope = Scope.UNDEFINED;
    private boolean allowsPassByReference;
    private ConstructorDefinition<?> constructorDefinition;
    private Method initMethod;
    private Method destroyMethod;
    private final Map<String, Resource> resources = new HashMap<String, Resource>();
    private Member conversationIDMember;

    /**
     * Returns the component implementation scope
     */
    public Scope getImplementationScope() {
        return implementationScope;
    }

    /**
     * Sets the component implementation scope
     */
    public void setImplementationScope(Scope implementationScope) {
        this.implementationScope = implementationScope;
    }

    /**
     * Returns the constructor used to instantiate implementation instances
     */
    public ConstructorDefinition<?> getConstructorDefinition() {
        return constructorDefinition;
    }

    /**
     * Sets the constructor used to instantiate implementation instances
     */
    public void setConstructorDefinition(ConstructorDefinition<?> definition) {
        this.constructorDefinition = definition;
    }

    /**
     * Returns the component initializer method
     */
    public Method getInitMethod() {
        return initMethod;
    }

    /**
     * Sets the component initializer method
     */
    public void setInitMethod(Method initMethod) {
        this.initMethod = initMethod;
    }

    /**
     * Returns the component destructor method
     */
    public Method getDestroyMethod() {
        return destroyMethod;
    }

    /**
     * Sets the component destructor method
     */
    public void setDestroyMethod(Method destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public Map<String, Resource> getResources() {
        return resources;
    }

    public void add(Resource resource) {
        resources.put(resource.getName(), resource);
    }

    public boolean isAllowsPassByReference() {
        return allowsPassByReference;
    }

    public void setAllowsPassByReference(boolean allowsPassByReference) {
        this.allowsPassByReference = allowsPassByReference;
    }
    
    public Member getConversationIDMember() {
        return this.conversationIDMember;
    }

    public void setConversationIDMember(Member conversationIDMember) {
        this.conversationIDMember = conversationIDMember;
    }
}
