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
package org.apache.tuscany.sca.interfacedef.java.impl;

import java.lang.ref.WeakReference;

import javax.xml.namespace.QName;
import org.apache.tuscany.sca.interfacedef.impl.InterfaceImpl;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;

/**
 * Represents a Java interface.
 * 
 * @version $Rev$ $Date$
 */
public class JavaInterfaceImpl extends InterfaceImpl implements JavaInterface {

    private String className;
    private WeakReference<Class<?>> javaClass;
    private Class<?> callbackClass;
    private QName qname;
    
    protected JavaInterfaceImpl() {
    }

    public String getName() {
        if (isUnresolved()) {
            return className;
        } else if (javaClass != null) {
            return javaClass.get().getName();
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

    public QName getQName() {
        return qname;
    }

    public void setQName(QName interfacename) {
        qname = interfacename;
    }

    public Class<?> getJavaClass() {
        if (javaClass != null){
            return javaClass.get();
        } else {
            return null;
        }
    }

    public void setJavaClass(Class<?> javaClass) {
        this.javaClass = new WeakReference<Class<?>>(javaClass);
        if (javaClass != null) {
            this.className = javaClass.getName();
        }
    }
    
    public Class<?> getCallbackClass() {
        return callbackClass;
    }
    
    public void setCallbackClass(Class<?> callbackClass) {
        this.callbackClass = callbackClass;
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JavaInterfaceImpl other = (JavaInterfaceImpl)obj;
        if (isUnresolved() || other.isUnresolved()) {
            if (className == null) {
                if (other.className != null)
                    return false;
            } else if (!className.equals(other.className))
                return false;
        } else {
            if (javaClass == null) {
                if (other.javaClass != null)
                    return false;
            } else if (!javaClass.get().equals(other.javaClass.get()))
                return false;
            if (callbackClass == null) {
                if (other.callbackClass != null)
                    return false;
            } else if (!callbackClass.equals(other.callbackClass))
                return false;
        }

        return true;
    }

}
