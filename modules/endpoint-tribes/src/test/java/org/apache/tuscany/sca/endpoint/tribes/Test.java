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

package org.apache.tuscany.sca.endpoint.tribes;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
        
        ReplicatedEndpointRegistry ep1 = new ReplicatedEndpointRegistry(extensionPoints, null, "foo", "bar");
        System.out.println("ep1 is: " + ep1);
        ep1.start();
        
        Endpoint e1 = assemblyFactory.createEndpoint();
        e1.setURI("e1uri");
        e1.setExtensionPointRegistry(null);
        ep1.addEndpoint(e1);
        
        Endpoint e1p = ep1.getEndpoint("e1uri");
        System.out.println(e1p);
        
        ReplicatedEndpointRegistry ep2 = new ReplicatedEndpointRegistry(extensionPoints, null, "foo", "bar");
        System.out.println("ep2 is: " + ep2);
        ep2.start();
        Thread.sleep(10000);
        
        Endpoint e1p2 = ep2.getEndpoint("e1uri");
        System.out.println(e1p2);
        
        
        ReplicatedEndpointRegistry ep3 = new ReplicatedEndpointRegistry(extensionPoints, null, "foo", "bar");
        System.out.println("ep3 is: " + ep3);
        ep3.start();
        Thread.sleep(10000);
        
        Endpoint e1p3 = ep3.getEndpoint("e1uri");
        System.out.println(e1p3);
        
    }
}
