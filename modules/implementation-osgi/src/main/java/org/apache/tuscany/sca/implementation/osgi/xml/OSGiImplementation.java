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
package org.apache.tuscany.sca.implementation.osgi.xml;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.impl.ComponentTypeImpl;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementationInterface;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.scope.Scope;
import org.osoa.sca.annotations.AllowsPassByReference;


/**
 * OSGi implementation 
 *    All attributes from <implementation.osgi> have getters in this class
 * This class implements OSGiImplementationInterface which is associated with OSGiImplementationProvider.
 */
public class OSGiImplementation extends ComponentTypeImpl implements OSGiImplementationInterface {
    
    private String bundleName;
    private String bundleLocation;
    private String[] imports;
    private Scope scope;
    private boolean isEagerInit;
    private String[] allowsPassByRef;
    private boolean needsPropertyInjection;
    private Hashtable<String, List<ComponentProperty>> referenceProperties;
    private Hashtable<String, List<ComponentProperty>> serviceProperties;

    private Hashtable<String, List<ComponentProperty>> referenceCallbackProperties;
    private Hashtable<String, List<ComponentProperty>> serviceCallbackProperties;
    

    public OSGiImplementation(String bundleName, 
            String bundleLocation,
            String[] imports, 
            String scopeName,
            boolean isEagerInit,
            String[] allowsPassByRef,
            Hashtable<String, List<ComponentProperty>> refProperties,
            Hashtable<String, List<ComponentProperty>> serviceProperties,
            boolean needsPropertyInjection) {
        
        super();
        this.bundleName = bundleName;
        this.bundleLocation = bundleLocation;
        setURI(bundleName);
        this.imports = imports;
        this.scope = new Scope(scopeName == null?"COMPOSITE":scopeName);
        this.isEagerInit = isEagerInit;
        this.allowsPassByRef = allowsPassByRef;
        this.referenceProperties = refProperties;
        this.serviceProperties = serviceProperties;
        this.needsPropertyInjection = needsPropertyInjection;
        
    }

    
    public String getBundleName() {
        return bundleName;
    }
    
    public String getBundleLocation() {
        return bundleLocation;
    }

    
    public String[] getImports() {
        return imports;
    }
    
    
    public Scope getScope() {
        return scope;
    }
    
    public List<ComponentProperty> getReferenceProperties(String referenceName) {
        return referenceProperties.get(referenceName);
    }
    
    public List<ComponentProperty> getServiceProperties(String serviceName) {
        return serviceProperties.get(serviceName);
    }
    
    public List<ComponentProperty> getReferenceCallbackProperties(String referenceName) {
        return referenceCallbackProperties.get(referenceName);
    }
    
    public List<ComponentProperty> getServiceCallbackProperties(String serviceName) {
        return serviceCallbackProperties.get(serviceName);
    }
    

    public boolean isAllowsPassByReference(Method method) {
        
        if (allowsPassByRef == null || allowsPassByRef.length == 0)
            return false;
        
        String className = method.getDeclaringClass().getName();
        String methodName = className + "." + method.getName();
        
        for (String opName : allowsPassByRef) {
            if (className.equals(opName) || methodName.equals(opName))
                return true;
        }
        return false;
    }
    
    public boolean isAllowsPassByReferenceAnnotation(Method method) {
        
        if (method.getAnnotation(AllowsPassByReference.class) != null) {
            return true;
        }
        else if (method.getClass().getAnnotation(AllowsPassByReference.class) != null) {
            return true;
        }
        else            
            return false;
    }

    
    public boolean needsPropertyInjection() {
        return needsPropertyInjection;
    }


    public boolean isEagerInit() {
        return isEagerInit;
    }

    public long getMaxAge() {
        return Long.MAX_VALUE;
    }

    public long getMaxIdleTime() {
        return Long.MAX_VALUE;
    }
    
    
    protected void setCallbackProperties(Hashtable<String, List<ComponentProperty>> refCallbackProperties, 
            Hashtable<String, List<ComponentProperty>> serviceCallbackProperties) {
        
        this.referenceCallbackProperties = refCallbackProperties;
        this.serviceCallbackProperties = serviceCallbackProperties;
        
    }

}
