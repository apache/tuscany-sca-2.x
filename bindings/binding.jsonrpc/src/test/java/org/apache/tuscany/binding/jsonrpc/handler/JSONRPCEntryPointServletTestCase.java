/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.jsonrpc.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.tuscany.binding.jsonrpc.mocks.MockConfigUtils;
import org.apache.tuscany.binding.jsonrpc.mocks.servlet.MockHttpServletRequest;
import org.apache.tuscany.binding.jsonrpc.mocks.servlet.MockHttpServletResponse;

public class JSONRPCEntryPointServletTestCase extends TestCase {

    private static final String JSON_REQUEST = "{\"id\": 2, \"method\": \"MyEntryPoint.concat\", \"params\": [\" world\"]}";

    private static final String JSON_RESPONSE = "{\"result\":\"hello world\",\"id\":2}";

    public void testService() throws ServletException, ClassCastException, IOException {
//        JSONRPCEntryPointServlet servlet = new JSONRPCEntryPointServlet("MyEntryPoint", "hello");
//        ServletConfig servletConfig = MockConfigUtils.createMockServletConfig("MyEntryPoint", "hello");
//        servlet.init(servletConfig);
//
//        HttpServletRequest request = new MockHttpServletRequest(JSON_REQUEST.getBytes());
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        HttpServletResponse response = new MockHttpServletResponse(os);
//
//        servlet.service(request, response);
//
//        String responseString = new String(os.toByteArray());
//        assertEquals(JSON_RESPONSE, responseString);
    }

    public void testInit() throws ServletException {
//        JSONRPCEntryPointServlet servlet = new JSONRPCEntryPointServlet("MyEntryPoint", "hello");
//        ServletConfig servletConfig = MockConfigUtils.createMockServletConfig("MyEntryPoint", "hello");
//
//        servlet.init(servletConfig);
//
//        assertEquals("MyEntryPoint", servlet.getEntryPointName());
//        assertEquals("hello", servlet.getEntryPointProxy());
    }

//    public void testHasJSONRPCBinding() {
//        JSONRPCEntryPointServlet servlet = new JSONRPCEntryPointServlet("MyEntryPoint", "hello");
//
//        EntryPoint entryPoint = MockConfigUtils.createMockEntryPoint("MyEntryPoint");
//        assertFalse(servlet.hasJSONRPCBinding(entryPoint));
//
//        MockConfigUtils.addNonJSONRPCBinding(entryPoint);
//        assertFalse(servlet.hasJSONRPCBinding(entryPoint));
//
//        MockConfigUtils.addJSONRPCBinding(entryPoint);
//        assertTrue(servlet.hasJSONRPCBinding(entryPoint));
//
//        assertEquals(2, entryPoint.getBindings().size());
//
//    }

}
