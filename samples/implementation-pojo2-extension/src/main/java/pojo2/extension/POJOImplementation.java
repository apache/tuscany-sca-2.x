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

package pojo2.extension;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

import pojo2.helper.TemporaryExtensionHelper;


/**
 * Represents a POJO implementation in an SCA assembly.
 *
 * @version $Rev$ $Date$
 */
public class POJOImplementation implements Implementation {
    
    private String pojoName;
    private Class<?> pojoClass;
    private Map<String, Method> methods;
    private Service service;

    /**
     * Returns the POJO class name
     * @return
     */
    public String getClass_() {
        return pojoName;
    }

    /**
     * Sets the POJO class name
     * @param class_
     */
    public void setClass_(String class_) {
        this.pojoName = class_;
        
        try {
            setPOJOClass(Class.forName(pojoName));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Returns the POJO class.
     * @return
     */
    public Class<?> getPOJOClass() {
        return pojoClass;
    }
    
    /**
     * Sets the POJO class.
     * @param pojoClass
     */
    public void setPOJOClass(Class<?> pojoClass) {
        this.pojoClass = pojoClass;
        
        // Index the POJO's methods
        methods = new HashMap<String, Method>();
        Method[] m = pojoClass.getMethods();
        for (int i = 0; i < m.length; i++) {
            methods.put(m[i].getName(), m[i]);
        }
        
        // Create a service for the POJO class
        service = TemporaryExtensionHelper.createJavaService(pojoClass.getSimpleName(), pojoClass);
    }
  
    /**
     * Returns the POJO's methods.
     * @return
     */
    public Map<String, Method> getMethods() {
        return methods;
    }

    public ConstrainingType getConstrainingType() {
        // The sample POJO implementation does not support constrainingTypes
        return null;
    }

    public List<Property> getProperties() {
        // The sample POJO implementation does not support properties
        return Collections.emptyList();
    }

    public List<Service> getServices() {
        // The sample POJO implementation provides a single fixed POJO service
        return Collections.singletonList(service);
    }
    
    public List<Reference> getReferences() {
        // The sample POJO implementation does not support properties
        return Collections.emptyList();
    }

    public String getURI() {
        // The sample POJO implementation does not have a URI
        return null;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        // The sample POJO implementation does not support constrainingTypes
    }

    public void setURI(String uri) {
        // The sample POJO implementation does not have a URI
    }

    public List<PolicySet> getPolicySets() {
        // The sample POJO implementation does not support policy sets
        return Collections.emptyList();
    }

    public List<Intent> getRequiredIntents() {
        // The sample POJO implementation does not support intents
        return Collections.emptyList();
    }

    public List<Object> getExtensions() {
        // The sample POJO implementation does not support extensions
        return Collections.emptyList();
    }

    public boolean isUnresolved() {
        // The sample POJO implementation is always resolved
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The sample POJO implementation is always resolved
    }

}
