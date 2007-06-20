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
package org.apache.tuscany.implementation.osgi.invocation;

import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.scope.InstanceWrapper;
import org.apache.tuscany.sca.scope.TargetDestructionException;
import org.apache.tuscany.sca.scope.TargetInitializationException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


/**
 * InstanceWrapper for creating instances for OSGi components. 
 * This class needs to implement InstanceWrapper since the wrapper is stored in
 * the scope container. But getInstance() is called on this wrapper only through the
 * OSGi target invoker. OSGiTargetInvoker always invokes getInstance for a specific
 * service since one OSGi SCA component can associate different objects with 
 * different services (this is different from Java SCA components which always associate 
 * a single component instance with multiple services).
 * 
 */
public class OSGiInstanceWrapper<T> implements InstanceWrapper<T> {
    
    private OSGiImplementationProvider provider;
    private Bundle bundle;
    private BundleContext bundleContext;
    private T osgiInstance;
    private ServiceReference osgiServiceReference;

    public OSGiInstanceWrapper(OSGiImplementationProvider provider, 
            BundleContext bundleContext, 
            Bundle bundle) {
        
        this.provider = provider;
        this.bundleContext = bundleContext;
        this.bundle = bundle;
    }
    
    public T getInstance(RuntimeComponentService service) {

        provider.startBundle(bundle);
        osgiServiceReference = provider.getOSGiServiceReference(bundle, service);
        
        osgiInstance = (T)bundleContext.getService(osgiServiceReference);
        
        return osgiInstance;
    }
    
    // This method is provided purely to implement InstanceWrapper interface, and is never called.
    public T getInstance() {

        return null;
    }
    
    public void start() throws TargetInitializationException {
        
    }

    public void stop() throws TargetDestructionException {
        if (osgiInstance != null && osgiServiceReference != null) {
            
            bundleContext.ungetService(osgiServiceReference);
            
            osgiInstance = null;
            osgiServiceReference = null;
        }
    }
    
}
