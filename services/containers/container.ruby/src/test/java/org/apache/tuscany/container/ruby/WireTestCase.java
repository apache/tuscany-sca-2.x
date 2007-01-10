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
package org.apache.tuscany.container.ruby;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.container.ruby.mock.Greeting;
import org.apache.tuscany.container.ruby.rubyscript.RubyScript;
import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.test.ArtifactFactory;
import static org.easymock.EasyMock.reportMatcher;
import org.easymock.IArgumentMatcher;

/**
 * Tests for JavaScript component wiring
 */
public class WireTestCase extends TestCase {

    private static final String SCRIPT = "   def setWire(ref)\n" + "       wire = ref\n"
        + "end   \n" + "   def greet(name)\n" + "       return wire.greet(name) \n"
        + "   end\n";

    private static final String SCRIPT2 = "   def greet(name)\n" + "       return name  \n"
        + "end \n";

    private RubyScript implClass1;

    private RubyScript implClass2;
    private WireService wireSerivce;

    /**
     * Tests a basic invocation down a source wire
     */
    public void testReferenceWireInvocation() throws Exception {
        // ModuleScopeContainer scope = new ModuleScopeContainer(null);
        // scope.start();
        //
        // List<Class<?>> serviceBindings = new ArrayList<Class<?>>();
        // serviceBindings.add(Greeting.class);
        // JavaScriptComponent<Greeting> context = new JavaScriptComponent<Greeting>("source", implClass1, serviceBindings, properties, null, scope,
        // ArtifactFactory.createWireService(), null);
        // OutboundWire<?> wire = ArtifactFactory.createOutboundWire("wire", Greeting.class);
        // ArtifactFactory.terminateWire(wire);
        //
        // TargetInvoker invoker = createMock(TargetInvoker.class);
        // expect(invoker.isCacheable()).andReturn(false);
        // Message response = new MessageImpl();
        // response.setBody("foo");
        // expect(invoker.invoke(eqMessage())).andReturn(response);
        // replay(invoker);
        //
        // for (OutboundInvocationChain chain : wire.getInvocationChains().values()) {
        // chain.setTargetInvoker(invoker);
        // }
        // scope.register(context);
        // context.addOutboundWire(wire);
        // Greeting greeting = context.getServiceInstance();
        // assertEquals("foo", greeting.greet("foo"));
        // verify(invoker);
        //
        // scope.stop();
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
        CompositeScopeContainer scope = new CompositeScopeContainer(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        Map<String, Object> properties = new Hashtable<String, Object>();
        properties.put("greeting", "HeyThere");

        RubyComponent context = new RubyComponent("source",
            implClass2,
            null,
            properties,
            null,
            ArtifactFactory.createWireService(),
            null,
            null);
        scope.register(context);
        DataType<String> returnDataType = new DataType<String>(String.class, String.class.getName());
//        Operation<String> operation = new Operation<String>("greet",
//                                                        returnDataType,
//                                                        null,
//                                                        null,
//                                                        false,
//                                                        null);
//        
//        TargetInvoker invoker = context.createTargetInvoker(null,
//                                                            operation);
//        assertEquals("foo",
//                     invoker.invokeTarget(new String[]{"foo"}));
        scope.stop();
    }

    /**
     * Tests a basic invocation down a target wire
     */
    public void testTargetWireInvocation() throws Exception {
        CompositeScopeContainer scope = new CompositeScopeContainer(null);
        scope.start();
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        Map<String, Object> properties = new Hashtable<String, Object>();
        properties.put("greeting", "HeyThere");
        RubyComponent context = new RubyComponent("source",
            implClass2,
            null,
            properties,
            null,
            ArtifactFactory.createWireService(),
            null,
            null);
        context.setScopeContainer(scope);
        scope.register(context);

        InboundWire wire = ArtifactFactory.createLocalInboundWire("Greeting",
            Greeting.class);
        ArtifactFactory.terminateWire(wire);
        for (InboundInvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(context.createTargetInvoker(null,
                chain.getOperation(), null));
        }
        context.addInboundWire(wire);
        Greeting greeting = wireSerivce.createProxy(Greeting.class, context.getInboundWire("Greeting"));
        assertEquals("foo",
            greeting.greet("foo"));
        scope.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        wireSerivce = ArtifactFactory.createWireService();
        implClass1 = new RubyScript("script1", SCRIPT);
        implClass2 = new RubyScript("script2", SCRIPT2);
    }
}
