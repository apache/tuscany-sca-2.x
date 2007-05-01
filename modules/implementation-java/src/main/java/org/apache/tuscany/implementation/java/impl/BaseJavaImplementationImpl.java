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
package org.apache.tuscany.implementation.java.impl;

import org.apache.tuscany.assembly.impl.ComponentTypeImpl;
import org.apache.tuscany.implementation.java.BaseJavaImplementation;

/**
 * Represents a Java implementation.
 * 
 * @version $Rev$ $Date$
 */
public abstract class BaseJavaImplementationImpl extends ComponentTypeImpl implements BaseJavaImplementation {

    private String className;
    private Class<?> javaClass;
    
    protected BaseJavaImplementationImpl() {
    }

    public String getName() {
        if (isUnresolved()) {
            return className;
        } else if (javaClass != null) {
            return javaClass.getName();
        } else {
            return null;
        }
    }

    public void setName(String className) {
        if (!isUnresolved()) {
            throw new IllegalStateException();
        }
        this.className = className;
    }

    public Class<?> getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(Class<?> javaClass) {
        this.javaClass = javaClass;
        if (this.className == null) {
            this.className = javaClass.getName();
        }
    }

    @Override
    public int hashCode() {
        return String.valueOf(getName()).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof BaseJavaImplementation) {
            if (getName() != null) {
                return getName().equals(((BaseJavaImplementation)obj).getName());
            } else {
                return ((BaseJavaImplementation)obj).getName() == null;
            }
        } else {
            return false;
        }
    }
}
