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

package interceptors;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import helloworld.HelloWorld;
import helloworld.StatusImpl;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.apache.tuscany.sca.policy.PolicySet;
import org.junit.Ignore;

public class HelloworldTestCase extends TestCase {

    private Node node;
    private HelloWorld helloWorld;

    @Override
    protected void setUp() throws Exception {
        StatusImpl.statusString = "";
        
        node = NodeFactory.newInstance().createNode("helloworld.composite", new Contribution("test", "target/classes"));
        node.start();
        helloWorld = node.getService(HelloWorld.class, "HelloWorldClient/HelloWorld");
    }
    
    public void testCalculator() throws Exception {
        // check response from application
        assertEquals("Hello fred", helloWorld.getGreetings("fred"));
        
        // check sequences of interceptors
        System.out.println(StatusImpl.statusString);
        assertEquals("TestBindingWSPolicyProviderService.configureBinding() - org.apache.tuscany.sca.binding.ws.axis2.provider.Axis2ServiceBindingProvider\n" +
                     "TestBindingWSPolicyProviderService.configureBinding() - org.apache.tuscany.sca.binding.ws.axis2.provider.Axis2ServiceBindingProvider\n" +
                     "TestPolicyInterceptor.processRequest() - HelloWorldClient#reference-binding($self$.HelloWorld/HelloWorld) @ reference.policy\n" +
                     "TestPolicyInterceptor.processRequest() - HelloWorldClient#service-binding(HelloWorld/HelloWorld) @ service.policy\n" +
                     "TestPolicyInterceptor.processRequest() - null @ implementation.policy\n" +
                     "TestBindingWSPolicyProviderReference.configureBinding() - org.apache.tuscany.sca.binding.ws.axis2.provider.Axis2ReferenceBindingProvider\n" +
                     "TestPolicyInterceptor.processRequest() - HelloWorldClient#reference-binding(helloWorldWS/BindingWS) @ reference.policy\n" +
                     "TestBindingWSPolicyInterceptor.processRequest() - HelloWorldClient#reference-binding(helloWorldWS/BindingWS) @ reference.binding.policy\n" +
                     "TestAxisHandler.invoke() - Reference OutFlow Handler\n" +
                     "TestAxisHandler.invoke() - Service InFlow Handler\n" +
                     "TestBindingWSPolicyInterceptor.processRequest() - HelloWorldService2#service-binding(HelloWorld/BindingWS) @ service.binding.policy\n" +
                     "TestPolicyInterceptor.processRequest() - HelloWorldService2#service-binding(HelloWorld/BindingWS) @ service.policy\n" +
                     "TestPolicyInterceptor.processRequest() - null @ implementation.policy\n" +
                     "At service - Hello fred\n" +
                     "TestPolicyInterceptor.processResponse() - null @ implementation.policy\n" +
                     "TestPolicyInterceptor.processResponse() - HelloWorldService2#service-binding(HelloWorld/BindingWS) @ service.policy\n" +
                     "TestBindingWSPolicyInterceptor.processResponse() - HelloWorldService2#service-binding(HelloWorld/BindingWS) @ service.binding.policy\n" +
                     "TestAxisHandler.invoke() - Service OutFlow Handler\n" +
                     "TestAxisHandler.invoke() - Reference InFlow Handler\n" +
                     "TestBindingWSPolicyInterceptor.processResponse() - HelloWorldClient#reference-binding(helloWorldWS/BindingWS) @ reference.binding.policy\n" +
                     "TestPolicyInterceptor.processResponse() - HelloWorldClient#reference-binding(helloWorldWS/BindingWS) @ reference.policy\n" +
                     "At client - Hello fred\n" +
                     "TestPolicyInterceptor.processResponse() - null @ implementation.policy\n" +
                     "TestPolicyInterceptor.processResponse() - HelloWorldClient#service-binding(HelloWorld/HelloWorld) @ service.policy\n" +
                     "TestPolicyInterceptor.processResponse() - HelloWorldClient#reference-binding($self$.HelloWorld/HelloWorld) @ reference.policy\n", 
                     StatusImpl.statusString);
        
        // check final intents on endpoint reference to see if the matching process
        // results on the right set
        Composite domainComposite = ((NodeImpl)node).getDomainComposite();
        List<PolicySet> policySets = domainComposite.getComponents().get(0).getReferences().get(0).getEndpointReferences().get(0).getPolicySets();
        
        assertEquals(2, policySets.size());
        assertEquals("TestInteractonPolicySet2", policySets.get(0).getName().getLocalPart());
        assertEquals("TestInteractonPolicySet1", policySets.get(1).getName().getLocalPart());
        
    }    
    
    @Override
    protected void tearDown() throws Exception {
        node.stop();
    }

}
