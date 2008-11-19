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

package org.apache.tuscany.sca.implementation.web.runtime;

import java.util.Collection;

import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;

/**
 * Proxy ComponentContext wrappering a RuntimeComponent as the
 * RuntimeComponent ComponentContext has not been created till later 
 */
public class ComponentContextProxy implements ComponentContext {

    protected RuntimeComponent runtimeComponent;
    
    public ComponentContextProxy(RuntimeComponent runtimeComponent) {
        this.runtimeComponent = runtimeComponent;
    }
    
    protected ComponentContext getComponentContext() {
        return runtimeComponent.getComponentContext();
    }
    
    @SuppressWarnings("unchecked")
    public <B, R extends CallableReference<B>> R cast(B arg0) throws IllegalArgumentException {
        return (R) getComponentContext().cast(arg0);
    }

    public <B> ServiceReference<B> createSelfReference(Class<B> arg0) {
        return getComponentContext().createSelfReference(arg0);
    }

    public <B> ServiceReference<B> createSelfReference(Class<B> arg0, String arg1) {
        return getComponentContext().createSelfReference(arg0, arg1);
    }

    public <B> B getProperty(Class<B> arg0, String arg1) {
        return getComponentContext().getProperty(arg0, arg1);
    }

    public RequestContext getRequestContext() {
        return getComponentContext().getRequestContext();
    }

    public <B> B getService(Class<B> arg0, String arg1) {
        return getComponentContext().getService(arg0, arg1);
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> arg0, String arg1) {
        return getComponentContext().getServiceReference(arg0, arg1);
    }

    public String getURI() {
        return getComponentContext().getURI();
    }

    public <B> Collection<ServiceReference<B>> getServiceReferences(Class<B> businessInterface, String referenceName) {
        return getComponentContext().getServiceReferences(businessInterface, referenceName);
    }

    public <B> Collection<B> getServices(Class<B> businessInterface, String referenceName) {
        return getComponentContext().getServices(businessInterface, referenceName);
    }

}
