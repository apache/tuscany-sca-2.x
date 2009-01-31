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
import java.util.ArrayList;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;


/**
 * This class implements the Customer service component.
 */
public  class OSGiBundleImpl implements ServiceListener, BundleActivator {
    

    String name;
    
    String[] references;
    Class<?>[] referenceClasses;
    Field[] referenceFields;
    String[] referenceFilters;
    
    Class myClass;
    ArrayList<String> serviceNames = new ArrayList<String>();
    ArrayList<Object> serviceObjs = new ArrayList<Object>();
    ArrayList<Hashtable<String, Object>> serviceProperties = new ArrayList<Hashtable<String, Object>>();
    
    
	private BundleContext bundleContext;
    
    public OSGiBundleImpl() {}
    
    public OSGiBundleImpl(String[] references, String[] filters) {
        
        myClass = this.getClass();
        this.name = this.getClass().getSimpleName();
        this.references = references == null?new String[0] : references;
        
       
        try {
            referenceClasses = new Class[references.length];
            referenceFields = new Field[references.length];
            referenceFilters = new String[references.length];
            for (int i = 0; i < references.length; i++) {
                referenceFields[i] = this.getClass().getDeclaredField(references[i]);
                referenceFields[i].setAccessible(true);
                referenceClasses[i] = referenceFields[i].getType();
                
                if (filters != null && filters.length > i)
                    referenceFilters[i] = filters[i];
            }
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
    
    public void start(BundleContext bc) {
    	
    	System.out.println("Started bundle " + name);
    	
    	this.bundleContext = bc;
        
        boolean useSingleRegisterService = serviceNames.size() > 1;
        for (int i = 1; i < serviceNames.size(); i++) {
            if (serviceObjs.get(i) != serviceObjs.get(0) || 
                    serviceProperties.get(i) != serviceProperties.get(0)) {
                useSingleRegisterService = false;
                break;
            }
        }
        if (useSingleRegisterService) {
            bundleContext.registerService(serviceNames.toArray(new String[serviceNames.size()]), 
                    serviceObjs.get(0), serviceProperties.get(0));
        }
        else {
            for (int i = 0; i < serviceNames.size(); i++) {
                bundleContext.registerService(serviceNames.get(i), serviceObjs.get(i), serviceProperties.get(i));
            }
        }
        
        started(bc);
    	
        for (int i = 0; i < references.length; i++) {

            try {
                if (referenceFields[i].get(this) != null)
                    continue;
                
    	        ServiceReference[] refs = bundleContext.getServiceReferences(referenceClasses[i].getName(), referenceFilters[i]);
	            if (refs != null && refs.length > 0) {
                    Object obj = bundleContext.getService(refs[0]);
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
    
    protected void started(BundleContext bc)  {
        
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
    

    public void registerService(Object serviceObject, String serviceName, Hashtable<String, Object> props) {
        serviceObjs.add(serviceObject);
        serviceNames.add(serviceName);
        serviceProperties.add(props == null? new Hashtable<String, Object>() : props);
    }
}
 