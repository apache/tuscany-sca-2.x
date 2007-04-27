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

package org.apache.tuscany.contribution.resolver;

import java.lang.ref.WeakReference;

/**
 * A weak reference to a class, which should be used to register classes
 * with an ArtifactResolver and resolve these classes later.
 *
 * @version $Rev$ $Date$
 */
public class ClassReference {
    
    private WeakReference<Class> clazz;
    private String className;

    /**
     * Constructs a new ClassReference.
     * 
     * @param clazz
     */
    public ClassReference(Class clazz) {
        this.clazz = new WeakReference<Class>(clazz);
        this.className = clazz.getName();
    }
    
    /**
     * Constructs a new ClassReference.
     * 
     * @param className
     */
    public ClassReference(String className) {
        this.className = className;
    }
    
    /**
     * Get the referenced class.
     * @return
     */
    public Class getJavaClass() {
        return clazz.get();
    }
    
    /**
     * Get the referenced class name.
     * @return
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * Returns true if the class reference is unresolved.
     * 
     * @return
     */
    boolean isUnresolved() {
        return clazz == null;
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            if (obj instanceof ClassReference) {
                return className.equals(((ClassReference)obj).className);
            } else {
                return false;
            }
        }
    }

}
