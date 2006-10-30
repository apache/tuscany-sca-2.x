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

import static org.easymock.classextension.EasyMock.*;

import junit.framework.TestCase;

public class JSONRPCServiceTestCase extends TestCase {
    private static final String SERVICE_NAME = "test_service_name";    

    public void testStart() {
        CompositeComponent mockParent = createMock(CompositeComponent.class);
        replay(mockParent);
        WireService mockWireService = createMock(WireService.class);
        expect(mockWireService.createProxy(null)).andReturn(this);
        replay(mockWireService);
        ServletHost mockServletHost = createMock(ServletHost.class);
        expect(mockServletHost.isMappingRegistered(JSONRPCService.SCRIPT_GETTER_SERVICE_MAPPING)).andReturn(false);
        mockServletHost.registerMapping(eq("/" + SERVICE_NAME), (Servlet) notNull());
        mockServletHost.registerMapping(eq(JSONRPCService.SCRIPT_GETTER_SERVICE_MAPPING), (Servlet) notNull());
        replay(mockServletHost);
        
        JSONRPCService jsonRpcService = new JSONRPCService(SERVICE_NAME, this.getClass(), mockParent, mockWireService, mockServletHost );
        jsonRpcService.start();
    }

    public void testStop() {
        CompositeComponent mockParent = createMock(CompositeComponent.class);
        replay(mockParent);
        WireService mockWireService = createMock(WireService.class);
        expect(mockWireService.createProxy(null)).andReturn(this);
        replay(mockWireService);
        ServletHost mockServletHost = createMock(ServletHost.class);
        expect(mockServletHost.isMappingRegistered(JSONRPCService.SCRIPT_GETTER_SERVICE_MAPPING)).andReturn(false);
        mockServletHost.registerMapping(eq("/" + SERVICE_NAME), (Servlet) notNull());
        mockServletHost.registerMapping(eq(JSONRPCService.SCRIPT_GETTER_SERVICE_MAPPING), (Servlet) notNull());
        expect(mockServletHost.unregisterMapping(eq("/" + SERVICE_NAME))).andReturn(null);
        expect(mockServletHost.unregisterMapping(eq(JSONRPCService.SCRIPT_GETTER_SERVICE_MAPPING))).andReturn(null);
        replay(mockServletHost);
        
        JSONRPCService jsonRpcService = new JSONRPCService(SERVICE_NAME, this.getClass(), mockParent, mockWireService, mockServletHost );
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
        
        JSONRPCService jsonRpcService = new JSONRPCService(SERVICE_NAME, this.getClass(), mockParent, mockWireService, mockServletHost );
        assertNotNull(jsonRpcService);
    }

}
