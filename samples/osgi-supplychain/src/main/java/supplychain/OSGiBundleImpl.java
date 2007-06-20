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
package supplychain;


import java.lang.reflect.Field;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;


/**
 * Common code for all OSGi bundles which dont use declarative services.
 * Registers services and sets references.
 */
public class OSGiBundleImpl implements ServiceListener, BundleActivator {
    

    String name;
    String serviceName;
    String[] references;
    Class<?>[] referenceClasses;
    Field[] referenceFields;
    
    Class myClass;
    
	private BundleContext bundleContext;
    
    public OSGiBundleImpl(String serviceName, String... references) {
        
        System.out.println("Created " + this.getClass().getSimpleName());
        
        myClass = this.getClass();
        this.name = this.getClass().getSimpleName();
        this.serviceName = serviceName;
        this.references = references;
        
        try {
            referenceClasses = new Class[references.length];
            referenceFields = new Field[references.length];
            for (int i = 0; i < references.length; i++) {
                referenceFields[i] = this.getClass().getDeclaredField(references[i]);
                referenceFields[i].setAccessible(true);
                referenceClasses[i] = referenceFields[i].getType();
            }
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
    
    public void start(BundleContext bc) {
    	
    	System.out.println("Started bundle " + name);
    	
    	this.bundleContext = bc;
    	
        bundleContext.registerService(serviceName, this, new Hashtable());
        
        for (int i = 0; i < references.length; i++) {

            try {
                
    	        ServiceReference ref = bundleContext.getServiceReference(referenceClasses[i].getName());
	            if (ref != null) {
                    Object obj = bundleContext.getService(ref);
                    referenceFields[i].set(this, referenceClasses[i].cast(obj));
                } else {
				    String filter = "(objectclass=" + referenceClasses[i].getName() + ")";
				    this.bundleContext.addServiceListener(this, filter);				
	            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void stop(BundleContext bc)  {
        System.out.println("Stop bundle " + name);

    }
    

	public void serviceChanged(ServiceEvent event) {
		try {
			if (event.getType() == ServiceEvent.REGISTERED) {
                
                ServiceReference ref = event.getServiceReference();
                Object obj = bundleContext.getService(ref);
                for (int i = 0; i < references.length; i++) {
                    if (referenceClasses[i].isAssignableFrom(obj.getClass())) {
                        referenceFields[i].set(this, referenceClasses[i].cast(obj));
                    }
                }
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
    

}
