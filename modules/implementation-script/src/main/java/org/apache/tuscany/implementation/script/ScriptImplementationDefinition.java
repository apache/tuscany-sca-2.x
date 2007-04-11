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
package org.apache.tuscany.implementation.script;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A component type specialization for POJO implementations
 *
 * @version $$Rev$$ $$Date$$
 */
public class ScriptImplementationDefinition extends ScriptImplementation {
    private ConstructorDefinition<?> constructorDefinition;
    private Map<Constructor, ConstructorDefinition> constructors = new HashMap<Constructor, ConstructorDefinition>();
    private Method initMethod;
    private Method destroyMethod;
    private final Map<String, Resource> resources = new HashMap<String, Resource>();
    private final Map<String, JavaElement> propertyMembers = new HashMap<String, JavaElement>();
    private final Map<String, JavaElement> referenceMembers = new HashMap<String, JavaElement>();
    private final Map<String, JavaElement> callbackMembers = new HashMap<String, JavaElement>();
    private Member conversationIDMember;
    private boolean eagerInit;
    private boolean allowsPassByReference;
    private List<Method> allowsPassByReferenceMethods = new ArrayList<Method>();
    private long maxAge = -1;
    private long maxIdleTime = -1;
    private Scope scope = Scope.STATELESS;

    /**
     * Constructor specifying the java class for the POJO this is describing.
     *
     * @param implClass the java class for the POJO this is describing
     */
    public ScriptImplementationDefinition(String scriptSrc) {
        super();
        setScriptSrc(scriptSrc);
    }
    
    public ScriptImplementationDefinition() {
        super();
    }    

    /**
     * Returns the constructor used to instantiate implementation instances.
     *
     * @return the constructor used to instantiate implementation instances
     */
    public ConstructorDefinition<?> getConstructorDefinition() {
        return constructorDefinition;
    }

    /**
     * Sets the constructor used to instantiate implementation instances
     *
     * @param definition the constructor used to instantiate implementation instances
     */
    public void setConstructorDefinition(ConstructorDefinition<?> definition) {
        this.constructorDefinition = definition;
    }

    /**
     * Returns the component initializer method.
     *
     * @return the component initializer method
     */
    public Method getInitMethod() {
        return initMethod;
    }

    /**
     * Sets the component initializer method.
     *
     * @param initMethod the component initializer method
     */
    public void setInitMethod(Method initMethod) {
        this.initMethod = initMethod;
    }

    /**
     * Returns the component destructor method.
     *
     * @return the component destructor method
     */
    public Method getDestroyMethod() {
        return destroyMethod;
    }

    /**
     * Sets the component destructor method.
     *
     * @param destroyMethod the component destructor method
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

    public Member getConversationIDMember() {
        return this.conversationIDMember;
    }

    public void setConversationIDMember(Member conversationIDMember) {
        this.conversationIDMember = conversationIDMember;
    }

    /**
     * @return the allowsPassByReference
     */
    public boolean isAllowsPassByReference() {
        return allowsPassByReference;
    }

    /**
     * @param allowsPassByReference the allowsPassByReference to set
     */
    public void setAllowsPassByReference(boolean allowsPassByReference) {
        this.allowsPassByReference = allowsPassByReference;
    }

    /**
     * @return the allowsPassByReferenceMethods
     */
    public List<Method> getAllowsPassByReferenceMethods() {
        return allowsPassByReferenceMethods;
    }
    
    public boolean isAllowsPassByReference(Method method) {
        return allowsPassByReference || allowsPassByReferenceMethods.contains(method);
    }

    /**
     * @return the constructors
     */
    public Map<Constructor, ConstructorDefinition> getConstructors() {
        return constructors;
    }

    /**
     * @return the eagerInit
     */
    public boolean isEagerInit() {
        return eagerInit;
    }

    /**
     * @param eagerInit the eagerInit to set
     */
    public void setEagerInit(boolean eagerInit) {
        this.eagerInit = eagerInit;
    }

    /**
     * @return the callbacks
     */
    public Map<String, JavaElement> getCallbackMembers() {
        return callbackMembers;
    }

    /**
     * @return the properties
     */
    public Map<String, JavaElement> getPropertyMembers() {
        return propertyMembers;
    }

    /**
     * @return the references
     */
    public Map<String, JavaElement> getReferenceMembers() {
        return referenceMembers;
    }

    /**
     * @return the scope
     */
    public Scope getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    /**
     * @return the maxAge
     */
    public long getMaxAge() {
        return maxAge;
    }

    /**
     * @param maxAge the maxAge to set
     */
    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * @return the maxIdleTime
     */
    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    /**
     * @param maxIdleTime the maxIdleTime to set
     */
    public void setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }
}
