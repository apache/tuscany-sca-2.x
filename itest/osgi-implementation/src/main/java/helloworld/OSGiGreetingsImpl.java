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

package helloworld;

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleActivator;


public class OSGiGreetingsImpl implements Greetings, ServiceListener, BundleActivator {

    private Greetings greetingsService;
    
    private BundleContext bundleContext;
    
    public String[] getGreetingsFromOSGi(String s[]) {
        for (int i = 0; i < s.length; i++) {
            s[i] = "Hello " + s[i] + "(From OSGi)";
        }
            
        return greetingsService.getGreetingsFromOSGi(s);
    }
    
    public String[] getGreetingsFromJava(String s[]) {
        for (int i = 0; i < s.length; i++) {
            s[i] = s[i] + "(From OSGi)";
        }
            
        return s;
    }
    
    public String[] getModifiedGreetingsFromOSGi(String s[]) {
        for (int i = 0; i < s.length; i++) {
            s[i] = "Hello " + s[i] + "(From OSGi)";
        }
            
        return greetingsService.getModifiedGreetingsFromOSGi(s);
    }
    
    public String[] getModifiedGreetingsFromJava(String s[]) {
        for (int i = 0; i < s.length; i++) {
            s[i] = s[i] + "(From OSGi)";
        }
            
        return s;
    }

    public void start(BundleContext bc) {
    	
    	System.out.println("Started OsgiGreetingsImpl bundle ");
    	
    	this.bundleContext = bc;
    	
        Hashtable<String, Object> serviceProps = new Hashtable<String, Object>();
        serviceProps.put("component.service.name", "OSGiGreetingsComponent/Greetings");
        bundleContext.registerService("helloworld.Greetings", this, serviceProps);
        
        
        ServiceReference ref = bundleContext.getServiceReference("helloworld.Greetings");
        if (ref != null)
            greetingsService = (helloworld.Greetings)bundleContext.getService(ref);
        else {
            try {
                String filter = "(objectclass=helloworld.Greetings)";
                this.bundleContext.addServiceListener(this, filter);                

            } catch (InvalidSyntaxException e) {
                e.printStackTrace();
            }
        }
      
    }
    
    public void stop(BundleContext bc)  {
    }
    
	public void serviceChanged(ServiceEvent event) {
		try {
			if (event.getType() == ServiceEvent.REGISTERED) {
			    ServiceReference ref = event.getServiceReference();
			    greetingsService =  (helloworld.Greetings) bundleContext.getService(ref);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
