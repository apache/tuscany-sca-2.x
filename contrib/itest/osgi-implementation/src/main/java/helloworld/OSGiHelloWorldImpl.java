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


public class OSGiHelloWorldImpl implements HelloWorld, ServiceListener, BundleActivator {

    public helloworld.ws.HelloWorld helloWorldWS;
    
    private BundleContext bundleContext;
    
    public String getGreetings(String s) {
        return helloWorldWS.getGreetings(s);
    }
    

    public void start(BundleContext bc) {
    	
    	System.out.println("Started OsgiHelloWorldImpl bundle ");
    	
    	this.bundleContext = bc;
    	
        Hashtable<String, Object> serviceProps = new Hashtable<String, Object>();
        serviceProps.put("component.name", "HelloWorldComponent");
        bundleContext.registerService("helloworld.HelloWorld", this, serviceProps);
        
        ServiceReference ref = bundleContext.getServiceReference("helloworld.ws.HelloWorld");
        if (ref != null)
            helloWorldWS = (helloworld.ws.HelloWorld)bundleContext.getService(ref);
        else {
            try {
            	String filter = "(objectclass=helloworld.ws.HelloWorld)";
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
			    helloWorldWS =  (helloworld.ws.HelloWorld) bundleContext.getService(ref);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
