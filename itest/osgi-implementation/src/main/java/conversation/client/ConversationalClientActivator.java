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
package conversation.client;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import conversation.client.ConversationalCallback;
import conversation.client.ConversationalClient;
import conversation.client.ConversationalClientStatefulImpl;
import conversation.client.ConversationalClientStatelessImpl;

/*
 * OSGi bundle activator for conversation tests
 */
public class ConversationalClientActivator implements BundleActivator, ServiceListener {

   
    private BundleContext bundleContext;


    public void start(BundleContext bc) throws Exception {
        
        System.out.println("Started OSGiConversationClientActivator ");
        
        this.bundleContext = bc;
        
        bc.addServiceListener(this);
        
        Hashtable<String, Object> serviceProps;
        
        serviceProps = new Hashtable<String, Object>();
         
        serviceProps.put("component.name", "ConversationalStatelessClientStatelessService");
        Object statelessClientFactory1 = 
            new ConversationalClientServiceFactory(ConversationalClientStatelessImpl.class, bundleContext, 1);
        bundleContext.registerService(
                new String[] {ConversationalClient.class.getName(), ConversationalCallback.class.getName()},
                statelessClientFactory1, 
                serviceProps);
      
        serviceProps = new Hashtable<String, Object>();
        serviceProps.put("component.name", "ConversationalStatelessClientStatefulService");       
        Object statelessClientFactory2 = 
            new ConversationalClientServiceFactory(ConversationalClientStatelessImpl.class, bundleContext, 2);
        bundleContext.registerService(
                new String[] {ConversationalClient.class.getName(), ConversationalCallback.class.getName()},
                statelessClientFactory2,
                serviceProps);
        
            
        serviceProps = new Hashtable<String, Object>();
        serviceProps.put("component.name", "ConversationalStatefulClientStatelessService");
        Object statefulClientFactory1 = 
            new ConversationalClientServiceFactory(ConversationalClientStatefulImpl.class, bundleContext, 3);
        bundleContext.registerService(
                new String[] {ConversationalClient.class.getName(), ConversationalCallback.class.getName()},
                statefulClientFactory1, 
                serviceProps);
        
        serviceProps = new Hashtable<String, Object>();
        serviceProps.put("component.name", "ConversationalStatefulClientStatefulService");
        Object statefulClientFactory2 = 
            new ConversationalClientServiceFactory(ConversationalClientStatefulImpl.class, bundleContext, 4);
        bundleContext.registerService(
                new String[] {ConversationalClient.class.getName(), ConversationalCallback.class.getName()},
                statefulClientFactory2, 
                serviceProps);
      
        
    }
    
    
    
    public void stop(BundleContext bc)  {
    }

    public void serviceChanged(ServiceEvent event) {
        
        if (event.getType() == ServiceEvent.REGISTERED) {
            
        }
    }
    
}
