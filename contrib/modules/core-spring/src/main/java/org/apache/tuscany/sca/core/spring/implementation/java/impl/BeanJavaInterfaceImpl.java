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
package org.apache.tuscany.sca.core.spring.implementation.java.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.impl.JavaInterfaceImpl;
import org.apache.tuscany.sca.policy.Intent;

/**
 * An alternate implementation of the SCA Java assembly model JavaInterface
 * interface.
 * 
 * @version $Rev$ $Date$
 */
public class BeanJavaInterfaceImpl extends JavaInterfaceImpl implements JavaInterface {

    private String className;
    private Class<?> javaClass;
    private Class<?> callbackClass;
    private boolean conversational;
    private boolean remotable;
    List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<Object> extensions = new ArrayList<Object>();
    private List<Operation> operations = new ArrayList<Operation>();
    private boolean unresolved = false;
    
    protected BeanJavaInterfaceImpl() {
    }

    @Override
    public String getName() {
        if (isUnresolved()) {
            return className;
        }    
        else {
            return javaClass.getName();
        }    
    }

    @Override
    public void setName(String className) {
        if (!isUnresolved())
            throw new IllegalStateException();
        this.className = className;
    }

    @Override
    public Class<?> getJavaClass() {
        return javaClass;
    }

    @Override
    public void setJavaClass(Class<?> javaClass) {
        this.javaClass = javaClass;
    }

    @Override
    public Class<?> getCallbackClass() {
        return callbackClass;
    }

    @Override
    public void setCallbackClass(Class<?> callbackClass) {
        this.callbackClass = callbackClass;
    }

    @Override
    public boolean isConversational() {
        return conversational;
    }

    @Override
    public boolean isRemotable() {
        return remotable;
    }

    @Override
    public void setConversational(boolean conversational) {
        this.conversational = conversational;
    }

    @Override
    public void setRemotable(boolean local) {
        this.remotable = local;
    }

    @Override
    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public List<Object> getExtensions() {
        return extensions;
    }

    @Override
    public List<Operation> getOperations() {
        return operations;
    }

    @Override
    public boolean isUnresolved() {
        return unresolved;
    }

    @Override
    public void setUnresolved(boolean undefined) {
        this.unresolved = undefined;
    }

    @Override
    public void resetDataBinding(String dataBinding) {
    }
    
    @Deprecated
    @Override
    public void setDefaultDataBinding(String dataBinding) {
    }    

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
