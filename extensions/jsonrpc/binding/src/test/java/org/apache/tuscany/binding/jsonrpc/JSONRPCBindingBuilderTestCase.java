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
package org.apache.tuscany.binding.jsonrpc;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import junit.framework.TestCase;

import org.apache.tuscany.spi.host.ServletHost;

public class JSONRPCBindingBuilderTestCase extends TestCase {

    public void testSetServletHost() {
        JSONRPCBindingBuilder bindingBuilder = new JSONRPCBindingBuilder();
        ServletHost mockServletHost = createMock(ServletHost.class);
        replay(mockServletHost);
        bindingBuilder.setServletHost(mockServletHost);
        assertEquals(mockServletHost, bindingBuilder.getServletHost());
    }

    public void testGetBindingType() {
        JSONRPCBindingBuilder bindingBuilder = new JSONRPCBindingBuilder();
        assertEquals(JSONRPCBindingDefinition.class, bindingBuilder.getBindingType());
    }

    @SuppressWarnings("unchecked")
    public void testBuildCompositeComponentBoundServiceDefinitionOfJSONRPCBindingDeploymentContext() {
//        JSONRPCBindingBuilder bindingBuilder = new JSONRPCBindingBuilder();
//        CompositeComponent mockParent = createMock(CompositeComponent.class);
//        replay(mockParent);
//        BoundServiceDefinition mockServiceDefinition = createMock((new BoundServiceDefinition()).getClass());
//        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
//        try {
//            ServiceContract<?> contract = registry.introspect(JSONRPCServiceBinding.class);
//
//            expect(mockServiceDefinition.getServiceContract()).andStubReturn(contract);
//            expect(mockServiceDefinition.getName()).andReturn("test_service");
//            replay(mockServiceDefinition);
//            DeploymentContext mockDeploymentContext = createMock(DeploymentContext.class);
//            replay(mockDeploymentContext);
//
//            JSONRPCServiceBinding jsonService =
//                (JSONRPCServiceBinding) bindingBuilder.build(mockParent, mockServiceDefinition, null,
//                    mockDeploymentContext);
//            assertEquals(JSONRPCServiceBinding.class, jsonService.getClass());
//
//        } catch (InvalidServiceContractException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            fail(e.toString());
//        }
    }

}
