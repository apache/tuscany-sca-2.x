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
package org.apache.tuscany.sca.implementation.java.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.implementation.java.JavaImplementation;

/**
 * A component type specialization for POJO implementations
 *
 * @version $$Rev$$ $$Date$$
 */
public class JavaImplementationImpl extends BaseJavaImplementationImpl implements JavaImplementation {
    private JavaConstructorImpl<?> constructorDefinition;
    private Map<Constructor, JavaConstructorImpl> constructors = new HashMap<Constructor, JavaConstructorImpl>();
    private Method initMethod;
    private Method destroyMethod;
    private final Map<String, JavaResourceImpl> resources = new HashMap<String, JavaResourceImpl>();
    private final Map<String, JavaElementImpl> propertyMembers = new HashMap<String, JavaElementImpl>();
    private final Map<String, JavaElementImpl> referenceMembers = new HashMap<String, JavaElementImpl>();
    private final Map<String, JavaElementImpl> callbackMembers = new HashMap<String, JavaElementImpl>();
    private List<Member> conversationIDMember = new ArrayList<Member>();
    private boolean eagerInit;
    private boolean allowsPassByReference;
    private List<Method> allowsPassByReferenceMethods = new ArrayList<Method>();
    private long maxAge = -1;
    private long maxIdleTime = -1;
    private JavaScopeImpl scope = JavaScopeImpl.STATELESS;

    protected JavaImplementationImpl() {
        super();
    }    

    public JavaConstructorImpl<?> getConstructor() {
        return constructorDefinition;
    }

    public void setConstructor(JavaConstructorImpl<?> definition) {
        this.constructorDefinition = definition;
    }

    public Method getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(Method initMethod) {
        this.initMethod = initMethod;
    }

    public Method getDestroyMethod() {
        return destroyMethod;
    }

    public void setDestroyMethod(Method destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public Map<String, JavaResourceImpl> getResources() {
        return resources;
    }

    public List<Member> getConversationIDMembers() {
        return this.conversationIDMember;
    }

    public void addConversationIDMember(Member conversationIDMember) {
        this.conversationIDMember.add(conversationIDMember);
    }

    public boolean isAllowsPassByReference() {
        return allowsPassByReference;
    }

    public void setAllowsPassByReference(boolean allowsPassByReference) {
        this.allowsPassByReference = allowsPassByReference;
    }

    public List<Method> getAllowsPassByReferenceMethods() {
        return allowsPassByReferenceMethods;
    }
    
    public boolean isAllowsPassByReference(Method method) {
        return allowsPassByReference || allowsPassByReferenceMethods.contains(method);
    }

    public Map<Constructor, JavaConstructorImpl> getConstructors() {
        return constructors;
    }

    public boolean isEagerInit() {
        return eagerInit;
    }

    public void setEagerInit(boolean eagerInit) {
        this.eagerInit = eagerInit;
    }

    public Map<String, JavaElementImpl> getCallbackMembers() {
        return callbackMembers;
    }

    public Map<String, JavaElementImpl> getPropertyMembers() {
        return propertyMembers;
    }

    public Map<String, JavaElementImpl> getReferenceMembers() {
        return referenceMembers;
    }

    public JavaScopeImpl getJavaScope() {
        return scope;
    }

    public void setJavaScope(JavaScopeImpl scope) {
        this.scope = scope;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public void setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }
}
