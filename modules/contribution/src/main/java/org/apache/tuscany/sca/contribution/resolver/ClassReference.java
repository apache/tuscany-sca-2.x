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

package org.apache.tuscany.sca.contribution.resolver;

import java.lang.ref.WeakReference;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.contribution.Contribution;

/**
 * A weak reference to a class, which should be used to register classes
 * with an ArtifactResolver and resolve these classes later.
 * 
 * FIXME The core contribution model should not have dependencies on classes
 * and ClassLoaders. This should move to the Java import support module.
 *
 * @version $Rev$ $Date$
 * @tuscany.spi.extension.asclient
 */
public class ClassReference implements Base {
    
    private WeakReference<Class<?>> clazz;
    private String className;
    private Contribution contributionContainingClass;

    /**
     * Constructs a new ClassReference.
     * 
     * @param clazz The class reference
     */
    public ClassReference(Class<?> clazz) {
        this.clazz = new WeakReference<Class<?>>(clazz);
        this.className = clazz.getName();
    }
    
    /**
     * Constructs a new ClassReference.
     * 
     * @param className The class name
     */
    public ClassReference(String className) {
        this.className = className;
    }
    
    /**
     * Get the referenced class.
     * 
     * @return The referenced class
     */
    public Class<?> getJavaClass() {
        if (clazz != null) {
            return clazz.get();
        } else {
            return null;
        }
    }
    
    /**
     * Get the referenced class name.
     * 
     * @return The class name
     */
    public String getClassName() {
        return className;
    }
    
    public boolean isUnresolved() {
        return clazz == null;
    }
    
    public void setUnresolved(boolean unresolved) {
        throw new IllegalStateException();
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
    
    /**
     * A Java class may reference a WSDL file via a JAXWS annotation. We need to resolve
     * the WSDL file location in the context of the same contribution that holds the 
     * Java file. In order to do this we need to pass back the actual contribution that
     * was used to resolve a Java class. It's possible that multiple contributions hold
     * the same class so just scanning the artifacts in all the contribution is not good 
     * enough
     * 
     * @return
     */
    public Contribution getContributionContainingClass() {
        return contributionContainingClass;
    }
    
    public void setContributionContainingClass(
            Contribution contributionContainingClass) {
        this.contributionContainingClass = contributionContainingClass;
    }

}
