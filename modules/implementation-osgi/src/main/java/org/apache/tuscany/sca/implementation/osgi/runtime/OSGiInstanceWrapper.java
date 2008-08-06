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
package org.apache.tuscany.sca.implementation.osgi.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.core.context.InstanceWrapper;
import org.apache.tuscany.sca.core.scope.Scope;
import org.apache.tuscany.sca.core.scope.TargetDestructionException;
import org.apache.tuscany.sca.core.scope.TargetInitializationException;
import org.apache.tuscany.sca.implementation.osgi.context.OSGiAnnotations;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;


/**
 * InstanceWrapper for creating instances for OSGi components. 
 * This class needs to implement InstanceWrapper since the wrapper is stored in
 * the scope container. But getInstance() is called on this wrapper only through the
 * OSGi target invoker. OSGiTargetInvoker always invokes getInstance for a specific
 * service since one OSGi SCA component can associate different objects with 
 * different services (this is different from Java SCA components which always associate 
 * a single component instance with multiple services).
 *
 * @version $Rev$ $Date$
 */
public class OSGiInstanceWrapper<T> implements InstanceWrapper<T> {
    private static final Random RANDOM_NUMBER_GENERATOR = new Random();
    
    private OSGiAnnotations annotationProcessor;
    private OSGiImplementationProvider provider;
    private BundleContext bundleContext;
    private Hashtable<Object,InstanceInfo<T>> instanceInfoList = 
        new Hashtable<Object,InstanceInfo<T>>();
    
    // Dummy bundles are used to create a new service object for scopes other than COMPOSITE
    private Bundle dummyReferenceBundle;


    public OSGiInstanceWrapper(OSGiImplementationProvider provider, 
            OSGiAnnotations annotationProcessor,
            BundleContext bundleContext) {
        
        this.provider = provider;
        this.annotationProcessor = annotationProcessor;
        this.bundleContext = bundleContext;
    }
    
    public synchronized T getInstance(ComponentService service) throws TargetInitializationException {
        
    	// If an instance corresponding to this service has already been created, return the instance.
        if (instanceInfoList.get(service) != null)
            return instanceInfoList.get(service).osgiInstance;
        
        // There is no strict relation between service and callback instances. The instance semantics
        // actually applies to the component instance in SCA. But for OSGi services, the callback
        // is just another OSGi service, and could correspond to any of the service instances in 
        // the component. To implement the SCA scope semantics for callbacks, OSGi callbacks
        // should also be made on the service object which implements the callback. The following code
        // finds the first possible callback instance based on the interfaces implemented by the service 
        // objects in this component. Note that the interfaces are checked by name rather than using
        // instanceof since the class seen by Tuscany could be from a different classloader from that
        // used by the bundle.
        if (service.isCallback()) {
        	Iterator<InstanceInfo<T>> instances = instanceInfoList.values().iterator();
        	while (instances.hasNext()) {
        		InstanceInfo<T> instanceInfo = instances.next();
        		Interface interfaze = service.getInterfaceContract().getInterface();
        		if (interfaze instanceof JavaInterface && ((JavaInterface)interfaze).getJavaClass() != null) {
        			String interfaceName = ((JavaInterface)interfaze).getJavaClass().getName();
        			Class[] interfaces = instanceInfo.osgiInstance.getClass().getInterfaces();
        			for (Class clazz : interfaces) {
        				if (clazz.getName().equals(interfaceName)) {
                			return instanceInfo.osgiInstance;
        				}
        			}
        			
        		}
        		    
        	}
        }

        Bundle refBundle = provider.startBundle(true);
        
        // For scopes other than composite, the service object is obtained using a dummy reference
        // bundle to guarantee that a new instance is created each time. This combined with the Tuscany
        // scope container code guarantee SCA scope semantics for OSGi components as long as service
        // factories are used.
        if (!annotationProcessor.getScope().equals(Scope.COMPOSITE)) {
            refBundle = getDummyReferenceBundle();
        }
        
        InstanceInfo<T> instanceInfo = new InstanceInfo<T>();
               
        instanceInfo.refBundleContext = refBundle.getBundleContext();

        instanceInfo.osgiInstance = getInstanceObject(instanceInfo, service);
        
        try {

            if (!isInitialized(instanceInfo.osgiInstance)) {
                
                annotationProcessor.injectProperties(instanceInfo.osgiInstance);
                callLifecycleMethod(instanceInfo.osgiInstance, Init.class);
                
                instanceInfo.isFirstInstance = true;
            }

            instanceInfoList.put(service, instanceInfo);

        } catch (Exception e) {
            throw new TargetInitializationException(e);
        }
        
        return instanceInfo.osgiInstance;
    }
    
    
    
    // This method is provided purely to implement InstanceWrapper interface, and is never called.
    public T getInstance() {

        return null;
    }
    
    public void start() throws TargetInitializationException {

        if (provider.isEagerInit()) {
            List<ComponentService> services = provider.getRuntimeComponent().getServices();
            for (ComponentService service : services) {
                getInstance(service);
            }
        }
    }

    public synchronized void stop() throws TargetDestructionException {
        
        for (InstanceInfo<T> instanceInfo : instanceInfoList.values()) {
            if (instanceInfo.osgiInstance != null && instanceInfo.osgiServiceReference != null) {            
                
                try {
                    
                    if (instanceInfo.isFirstInstance)
                        callLifecycleMethod(instanceInfo.osgiInstance, Destroy.class);
            
                    instanceInfo.refBundleContext.ungetService(instanceInfo.osgiServiceReference);
            
                    instanceInfo.osgiInstance = null;
                    instanceInfo.osgiServiceReference = null;
            
                } catch (Exception e) {
                    throw new TargetDestructionException(e);
                }            
            }
        }
        instanceInfoList.clear();
        if (dummyReferenceBundle != null) {
            try {
                dummyReferenceBundle.uninstall();
            } catch (BundleException e) {
                throw new TargetDestructionException(e);
            }
            dummyReferenceBundle = null;
        }
    }
    
    @SuppressWarnings("unchecked")
    private T getInstanceObject(InstanceInfo<T> instanceInfo, ComponentService service) {

    	/** 
    	 * Since implementation.osgi is not well integrated with the OSGi lifecycle
    	 * it is possible that the service is deactivated before the service instance
    	 * is obtained when using declarative services. Retry in this case.  
    	 */
    	int maxRetries = 10;
    	for (int i = 0; i < maxRetries; i++) {
          instanceInfo.osgiServiceReference = provider.getOSGiServiceReference(service); 
          if (instanceInfo.osgiServiceReference == null)
        	  return null;
          T obj = (T)instanceInfo.refBundleContext.getService(instanceInfo.osgiServiceReference);
          if (obj != null)
        	  return obj;
    	}
    	return null;
    }
    
    private Bundle getDummyReferenceBundle() throws TargetInitializationException {
        
        if (dummyReferenceBundle != null)
            return dummyReferenceBundle;
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        String EOL = System.getProperty("line.separator");
        String bundleName = "dummy.sca." + RANDOM_NUMBER_GENERATOR.nextInt();
        
        
        String manifestStr = "Manifest-Version: 1.0" + EOL +
                        "Bundle-ManifestVersion: 2" + EOL +
                        "Bundle-Name: " + bundleName + EOL +
                        "Bundle-SymbolicName: " + bundleName + EOL +
                        "Bundle-Version: " + "1.0.0" + EOL +
                        "Bundle-Localization: plugin" + EOL;
        
                        
        StringBuilder manifestBuf = new StringBuilder();
        manifestBuf.append(manifestStr);
       
        try {
            ByteArrayInputStream manifestStream = new ByteArrayInputStream(manifestBuf.toString().getBytes());
            Manifest manifest = new Manifest();
            manifest.read(manifestStream);
            
      
            JarOutputStream jarOut = new JarOutputStream(out, manifest);

            jarOut.close();
            out.close();
            
            
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                   
            dummyReferenceBundle = bundleContext.installBundle("file://" + bundleName + ".jar", in);
            
            dummyReferenceBundle.start();
            
        } catch (Exception e) {
            throw new TargetInitializationException(e);
        }
        
        return dummyReferenceBundle;
        
    }
    
    private void callLifecycleMethod(Object instance,
            Class<? extends Annotation> annotationClass) throws Exception {

        Method method = null;
        if (annotationClass == Init.class) {
            method = annotationProcessor.getInitMethod(instance);
        } else if (annotationClass == Destroy.class) {
            method = annotationProcessor.getDestroyMethod(instance);
        }

        if (method != null) {
            method.setAccessible(true);
            method.invoke(instance);
        }
    }
    
    private boolean isInitialized(Object instance) {
        for (InstanceInfo<?> info : instanceInfoList.values()) {
            if (info.osgiInstance == instance)
                return true;
        }
        return false;
    }
    
    private static class InstanceInfo<T> {
        private T osgiInstance;
        private ServiceReference osgiServiceReference;
        private BundleContext refBundleContext;
        private boolean isFirstInstance;

    }
    
}
