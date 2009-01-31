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
package conversation.referenceclient;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

/*
 * OSGi bundle activator for conversation tests
 */
public class ConversationalReferenceClientActivator implements BundleActivator, ServiceListener {

   
    private BundleContext bundleContext;


    public void start(BundleContext bc) throws Exception {
        
        System.out.println("Started OSGiConversationReferenceClientActivator ");
        
        this.bundleContext = bc;
        
        bc.addServiceListener(this);
        
        Hashtable<String, Object> serviceProps;
        
        serviceProps = new Hashtable<String, Object>();
        serviceProps.put("component.name", "ConversationalReferenceClient");
        ConversationalReferenceClientImpl refClient = new ConversationalReferenceClientImpl();
        bundleContext.registerService(ConversationalReferenceClient.class.getName(), refClient, serviceProps);
        
    }
    
    
    
    public void stop(BundleContext bc)  {
    }

    public void serviceChanged(ServiceEvent event) {
        
        if (event.getType() == ServiceEvent.REGISTERED) {
            
        }
    }
    
}
