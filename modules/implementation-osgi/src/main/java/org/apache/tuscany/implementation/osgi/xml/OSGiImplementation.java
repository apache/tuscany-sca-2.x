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
package org.apache.tuscany.implementation.osgi.xml;


import java.util.Hashtable;
import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.impl.ComponentTypeImpl;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.implementation.osgi.OSGiImplementationInterface;
import org.apache.tuscany.sca.scope.Scope;


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
    private String[] allowsPassByRef;
    Hashtable<String, List<ComponentProperty>> referenceProperties;
    Hashtable<String, List<ComponentProperty>> serviceProperties;

    public OSGiImplementation(String bundleName, 
            String bundleLocation,
    		String[] imports, 
    		String scopeName,
            String[] allowsPassByRef,
            Hashtable<String, List<ComponentProperty>> refProperties,
            Hashtable<String, List<ComponentProperty>> serviceProperties) {
    	
    	super();
        this.bundleName = bundleName;
        this.bundleLocation = bundleLocation;
        this.imports = imports;
        this.scope = new Scope(scopeName == null?"COMPOSITE":scopeName);
        this.allowsPassByRef = allowsPassByRef;
        this.referenceProperties = refProperties;
        this.serviceProperties = serviceProperties;
        
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
    

    public boolean isAllowsPassByReference(Operation operation) {
        if (allowsPassByRef == null || allowsPassByRef.length == 0)
            return false;
        Interface interfaze = operation.getInterface();
        if (!(interfaze instanceof JavaInterface))
            return false;
        String className = ((JavaInterface)interfaze).getJavaClass().getName();
        String operationName = className + "." + operation.getName();
        
        for (String opName : allowsPassByRef) {
            if (className.equals(opName) || operationName.equals(opName))
                return true;
        }
        return false;
    }

    public boolean isEagerInit() {
        return false;
    }

    public long getMaxAge() {
        return Long.MAX_VALUE;
    }

    public long getMaxIdleTime() {
        return Long.MAX_VALUE;
    }
	
	
}
