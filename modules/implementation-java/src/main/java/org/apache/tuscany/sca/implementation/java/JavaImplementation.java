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

package org.apache.tuscany.sca.implementation.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.implementation.java.impl.JavaConstructorImpl;
import org.apache.tuscany.sca.implementation.java.impl.JavaElementImpl;
import org.apache.tuscany.sca.implementation.java.impl.JavaResourceImpl;
import org.apache.tuscany.sca.implementation.java.impl.JavaScopeImpl;

/**
 * Represents a Java implementation.
 *
 * @version $Rev$ $Date$
 */
public interface JavaImplementation extends BaseJavaImplementation {

    /**
     * Returns the constructor used to instantiate implementation instances.
     *
     * @return the constructor used to instantiate implementation instances
     */
    public JavaConstructorImpl<?> getConstructor();

    /**
     * Sets the constructor used to instantiate implementation instances
     *
     * @param definition the constructor used to instantiate implementation instances
     */
    public void setConstructor(JavaConstructorImpl<?> definition);

    /**
     * Returns the component initializer method.
     *
     * @return the component initializer method
     */
    public Method getInitMethod();

    /**
     * Sets the component initializer method.
     *
     * @param initMethod the component initializer method
     */
    public void setInitMethod(Method initMethod);

    /**
     * Returns the component destructor method.
     *
     * @return the component destructor method
     */
    public Method getDestroyMethod();

    /**
     * Sets the component destructor method.
     *
     * @param destroyMethod the component destructor method
     */
    public void setDestroyMethod(Method destroyMethod);

    /**
     * Returns the resources injected into this implementation.
     * 
     * @return
     */
    public Map<String, JavaResourceImpl> getResources();

    /**
     * Returns the Java member used to inject a conversation ID.
     * 
     * @return
     */
    public List<Member> getConversationIDMembers();

    /**
     * Sets the Java member used to inject a conversation ID.
     * 
     * @param conversationIDMember
     */
    public void addConversationIDMember(Member conversationIDMember);

    /**
     * Returns true if AllowsPassReference is set.
     *  
     * @return true if AllowsPassByReference is set
     */
    public boolean isAllowsPassByReference();

    /**
     * @param allowsPassByReference the allowsPassByReference to set
     */
    public void setAllowsPassByReference(boolean allowsPassByReference);

    /**
     * @return the allowsPassByReferenceMethods
     */
    public List<Method> getAllowsPassByReferenceMethods();
    
    /**
     * @param method
     * @return
     */
    public boolean isAllowsPassByReference(Method method);

    /**
     * @return the constructors
     */
    public Map<Constructor, JavaConstructorImpl> getConstructors();

    /**
     * @return the eagerInit
     */
    public boolean isEagerInit();

    /**
     * @param eagerInit the eagerInit to set
     */
    public void setEagerInit(boolean eagerInit);

    /**
     * @return the callbacks
     */
    public Map<String, JavaElementImpl> getCallbackMembers();

    /**
     * @return the properties
     */
    public Map<String, JavaElementImpl> getPropertyMembers();

    /**
     * @return the references
     */
    public Map<String, JavaElementImpl> getReferenceMembers();

    /**
     * @return the scope
     */
    public JavaScopeImpl getJavaScope();

    /**
     * @param scope the scope to set
     */
    public void setJavaScope(JavaScopeImpl scope);

    /**
     * @return the maxAge
     */
    public long getMaxAge();

    /**
     * @param maxAge the maxAge to set
     */
    public void setMaxAge(long maxAge);

    /**
     * @return the maxIdleTime
     */
    public long getMaxIdleTime();

    /**
     * @param maxIdleTime the maxIdleTime to set
     */
    public void setMaxIdleTime(long maxIdleTime);

}
