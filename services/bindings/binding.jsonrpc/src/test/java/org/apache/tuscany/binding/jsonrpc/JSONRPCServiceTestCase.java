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

import javax.servlet.Servlet;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.model.ServiceContract;

import static org.easymock.classextension.EasyMock.*;
import org.easymock.EasyMock;

import junit.framework.TestCase;

public class JSONRPCServiceTestCase extends TestCase {
    private static final String SERVICE_NAME = "test_service_name";    

    @SuppressWarnings({"unchecked"})
    public void testStart() {
        CompositeComponent mockParent = createMock(CompositeComponent.class);
        replay(mockParent);
        WireService mockWireService = createMock(WireService.class);
        expect(mockWireService.createProxy(EasyMock.isA(Class.class), EasyMock.isA(InboundWire.class))).andReturn(this);
        replay(mockWireService);
        ServletHost mockServletHost = createMock(ServletHost.class);
        expect(mockServletHost.isMappingRegistered(JSONRPCServiceBinding.SCRIPT_GETTER_SERVICE_MAPPING)).andReturn(false);
        mockServletHost.registerMapping(eq("/" + SERVICE_NAME), (Servlet) notNull());
        mockServletHost.registerMapping(eq(JSONRPCServiceBinding.SCRIPT_GETTER_SERVICE_MAPPING), (Servlet) notNull());
        replay(mockServletHost);

        ServiceContract contract = new ServiceContract(Object.class){

        };
        JSONRPCServiceBinding jsonRpcService = new JSONRPCServiceBinding(SERVICE_NAME, mockParent, mockWireService, mockServletHost );
        InboundWire wire = EasyMock.createNiceMock(InboundWire.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract);
        EasyMock.replay(wire);
        jsonRpcService.setInboundWire(wire);
        jsonRpcService.start();
    }

    @SuppressWarnings({"unchecked"})
    public void testStop() {
        CompositeComponent mockParent = createMock(CompositeComponent.class);
        replay(mockParent);
        WireService mockWireService = createMock(WireService.class);
        expect(mockWireService.createProxy(EasyMock.isA(Class.class), EasyMock.isA(InboundWire.class))).andReturn(this);
        replay(mockWireService);
        ServletHost mockServletHost = createMock(ServletHost.class);
        expect(mockServletHost.isMappingRegistered(JSONRPCServiceBinding.SCRIPT_GETTER_SERVICE_MAPPING)).andReturn(false);
        mockServletHost.registerMapping(eq("/" + SERVICE_NAME), (Servlet) notNull());
        mockServletHost.registerMapping(eq(JSONRPCServiceBinding.SCRIPT_GETTER_SERVICE_MAPPING), (Servlet) notNull());
        expect(mockServletHost.unregisterMapping(eq("/" + SERVICE_NAME))).andReturn(null);
        expect(mockServletHost.unregisterMapping(eq(JSONRPCServiceBinding.SCRIPT_GETTER_SERVICE_MAPPING))).andReturn(null);
        replay(mockServletHost);
        
        ServiceContract contract = new ServiceContract(Object.class){

        };
        JSONRPCServiceBinding jsonRpcService = new JSONRPCServiceBinding(SERVICE_NAME, mockParent, mockWireService, mockServletHost );
        InboundWire wire = EasyMock.createNiceMock(InboundWire.class);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract);
        EasyMock.replay(wire);
        jsonRpcService.setInboundWire(wire);
        jsonRpcService.start();
        jsonRpcService.stop();
    }

    public void testJSONRPCService() {
        CompositeComponent mockParent = createMock(CompositeComponent.class);    
        replay(mockParent);
        WireService mockWireService = createMock(WireService.class);        
        replay(mockWireService);
        ServletHost mockServletHost = createMock(ServletHost.class);        
        replay(mockServletHost);
        
        JSONRPCServiceBinding jsonRpcService = new JSONRPCServiceBinding(SERVICE_NAME, mockParent, mockWireService, mockServletHost );
        assertNotNull(jsonRpcService);
    }

}
