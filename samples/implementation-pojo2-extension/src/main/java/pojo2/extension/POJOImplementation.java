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
import java.util.ArrayList;
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


/**
 * Represents a POJO implementation in an SCA assembly.
 *
 * @version $Rev$ $Date$
 */
public class POJOImplementation implements Implementation {
    
    private String pojoName;
    private Class<?> pojoClass;
    private String uri;
    private Map<String, Method> methods;
    private List<Service> services = new ArrayList<Service>();
    private List<Reference> references = new ArrayList<Reference>();
    private List<Property> properties = new ArrayList<Property>();
    private boolean unresolved;

    /**
     * Returns the POJO class name
     * @return
     */
    public String getPOJOName() {
        return pojoName;
    }

    /**
     * Sets the POJO class name
     * @param pojoName
     */
    public void setPOJOName(String pojoName) {
        this.pojoName = pojoName;
        setURI(pojoName.replace('.', '/'));
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
        setPOJOName(pojoClass.getName());
        
        // Index the POJO's methods
        methods = new HashMap<String, Method>();
        Method[] m = pojoClass.getMethods();
        for (int i = 0; i < m.length; i++) {
            methods.put(m[i].getName(), m[i]);
        }
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
        return properties;
    }

    public List<Service> getServices() {
        return services;
    }
    
    public List<Reference> getReferences() {
        return references;
    }

    public String getURI() {
        return uri;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        // The sample POJO implementation does not support constrainingTypes
    }

    public void setURI(String uri) {
        this.uri = uri;
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
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof POJOImplementation) {
            return ((POJOImplementation)obj).getURI().equals(uri);
        } else {
            return false;
        }
    }

}
