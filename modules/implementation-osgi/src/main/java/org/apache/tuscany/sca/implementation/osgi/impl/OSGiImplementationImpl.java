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
package org.apache.tuscany.sca.implementation.osgi.impl;


import java.util.Hashtable;
import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.impl.ImplementationImpl;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.implementation.osgi.OSGiImplementation;


/**
 * OSGi implementation 
 *    All attributes from <implementation.osgi> have getters in this class
 * This class implements OSGiImplementationInterface which is associated with OSGiImplementationProvider.
 *
 * @version $Rev$ $Date$
 */
public class OSGiImplementationImpl extends ImplementationImpl implements OSGiImplementation {
    
    private String bundleSymbolicName;
    private String bundleVersion;
    
    private String[] imports;
    private Hashtable<String, List<ComponentProperty>> referenceProperties;
    private Hashtable<String, List<ComponentProperty>> serviceProperties;

    private Hashtable<String, List<ComponentProperty>> referenceCallbackProperties;
    private Hashtable<String, List<ComponentProperty>> serviceCallbackProperties;
    
    private String[] classList;
    
    private ModelFactoryExtensionPoint modelFactories;
    
    private Object osgiBundle;
    
    public OSGiImplementationImpl(
            ModelFactoryExtensionPoint modelFactories,
            String bundleSymbolicName,
            String bundleVersion,
            String[] imports,
            String[] classList,
            Hashtable<String, List<ComponentProperty>> refProperties,
            Hashtable<String, List<ComponentProperty>> serviceProperties) {
        
        super();
        this.bundleSymbolicName = bundleSymbolicName;
        this.bundleVersion = bundleVersion;
        this.imports = imports;
        this.referenceProperties = refProperties;
        this.serviceProperties = serviceProperties;
        this.classList = classList;
        this.modelFactories = modelFactories;
    }

    public void setCallbackProperties(Hashtable<String, List<ComponentProperty>> refCallbackProperties, 
            Hashtable<String, List<ComponentProperty>> serviceCallbackProperties) {
        
        this.referenceCallbackProperties = refCallbackProperties;
        this.serviceCallbackProperties = serviceCallbackProperties;
        
    }
    
   
    public String getBundleSymbolicName() {
        return bundleSymbolicName;
    }

    public String getBundleVersion() {
        return bundleVersion;
    }

    public String[] getImports() {
        return imports;
    }
    
    public String[] getClassList() {
        return classList;
    }
    
    public ModelFactoryExtensionPoint getModelFactories() {
        return modelFactories;
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

    /**
     * Since OSGi implementation annotations may not be processed until much later, leave it to
     * the OSGi invoker to decide whether pass-by-reference is allowed.
     * @return
     */
    public boolean isAllowsPassByReference() {
        return true;
    }
    
    public Object getOSGiBundle() {
        return osgiBundle;
    }

    public void setOSGiBundle(Object osgiBundle) {
        this.osgiBundle = osgiBundle;
    }

    private boolean areEqual(Object obj1, Object obj2) {
        if (obj1 == obj2)
            return true;
        if (obj1 == null || obj2 == null)
            return false;
        return obj1.equals(obj2);
    }

    @Override
    public boolean equals(Object obj) {
        
        if (!(obj instanceof OSGiImplementationImpl))
            return super.equals(obj);
        OSGiImplementationImpl impl = (OSGiImplementationImpl)obj;
        if (!areEqual(bundleSymbolicName, impl.bundleSymbolicName))
            return false;
        if (!areEqual(bundleVersion, impl.bundleVersion))
            return false;
        if (!areEqual(serviceProperties, impl.serviceProperties))
            return false;
        if (!areEqual(serviceCallbackProperties, impl.serviceCallbackProperties))
            return false;
        if (!areEqual(referenceProperties, impl.referenceProperties))            
            return false;
        if (!areEqual(referenceCallbackProperties, impl.referenceCallbackProperties))            
                return false;
        return super.equals(obj);
    }
    

    
}
