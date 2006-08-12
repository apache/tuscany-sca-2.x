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
package org.apache.tuscany.container.javascript;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.tuscany.container.javascript.mock.Greeting;
import org.apache.tuscany.container.javascript.rhino.RhinoScript;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.test.ArtifactFactory;
import org.easymock.IArgumentMatcher;

/**
 * Tests for JavaScript component wiring
 */
public class WireTestCase extends TestCase {
    private static final Map<String, Object> properties = new HashMap<String, Object>();

    private static final String SCRIPT = 
        "   function setWire(ref){" + 
        "       wire = ref;" + "   }" +
        "   " +
        "   function greet(name){" +
        "       return wire.greet(name);  " +
        "   }";

    private static final String SCRIPT2 = 
        "   function greet(name){" +
        "       return name;  " +
        "   }";

    private RhinoScript implClass1;

    private RhinoScript implClass2;

    /**
     * Tests a basic invocation down a source wire
     */
    public void testReferenceWireInvocation() throws Exception {
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();

        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        JavaScriptComponent<Greeting> context = new JavaScriptComponent<Greeting>("source", implClass1, services, properties, null, scope,
                ArtifactFactory.createWireService(), null);
        OutboundWire<?> wire = ArtifactFactory.createOutboundWire("wire", Greeting.class);
        ArtifactFactory.terminateWire(wire);

        TargetInvoker invoker = createMock(TargetInvoker.class);
        expect(invoker.isCacheable()).andReturn(false);
        Message response = new MessageImpl();
        response.setBody("foo");
        expect(invoker.invoke(eqMessage())).andReturn(response);
        replay(invoker);

        for (OutboundInvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(invoker);
        }
        scope.register(context);
        context.addOutboundWire(wire);
        Greeting greeting = context.getServiceInstance();
        assertEquals("foo", greeting.greet("foo"));
        verify(invoker);

        scope.stop();
    }

    // todo this could be generalized and moved to test module
    public static Message eqMessage() {
        reportMatcher(new IArgumentMatcher() {
            public boolean matches(Object object) {
                if (!(object instanceof Message)) {
                    return false;
                }
                final Message msg = (Message) object;
                Object[] body = (Object[]) msg.getBody();
                return "foo".equals(body[0]);
            }

            public void appendTo(StringBuffer stringBuffer) {
            }
        });
        return null;
    }

    /**
     * Tests a basic invocation to a target
     */
    public void testTargetInvocation() throws Exception {
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        JavaScriptComponent<Greeting> context = new JavaScriptComponent<Greeting>("source", implClass2, services, properties, null, scope,
                ArtifactFactory.createWireService(), null);
        scope.register(context);
        TargetInvoker invoker = context.createTargetInvoker("greeting", Greeting.class.getMethod("greet", String.class)
        );
        assertEquals("foo", invoker.invokeTarget(new String[] { "foo" }));
        scope.stop();
    }

    /**
     * Tests a basic invocation down a target wire
     */
    public void testTargetWireInvocation() throws Exception {
        ModuleScopeContainer scope = new ModuleScopeContainer(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        JavaScriptComponent<Greeting> context = new JavaScriptComponent<Greeting>("source", implClass2, services, properties, null, scope,
                ArtifactFactory.createWireService(), null);
        scope.register(context);

        InboundWire<?> wire = ArtifactFactory.createInboundWire("Greeting", Greeting.class);
        ArtifactFactory.terminateWire(wire);
        for (InboundInvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(context.createTargetInvoker("Greeting", chain.getMethod()));
        }
        context.addInboundWire(wire);
        Greeting greeting = (Greeting) context.getServiceInstance("Greeting");
        assertEquals("foo", greeting.greet("foo"));
        scope.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        implClass1 = new RhinoScript("script1", SCRIPT);
        implClass2 = new RhinoScript("script2", SCRIPT2);
    }
}
