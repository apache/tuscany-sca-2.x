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
package org.apache.tuscany.container.groovy;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import junit.framework.TestCase;
import org.apache.tuscany.container.groovy.mock.Greeting;
import org.apache.tuscany.test.ArtifactFactory;
import static org.apache.tuscany.test.ArtifactFactory.createLocalInboundWire;
import static org.apache.tuscany.test.ArtifactFactory.createLocalOutboundWire;
import static org.apache.tuscany.test.ArtifactFactory.createWireService;
import static org.apache.tuscany.test.ArtifactFactory.terminateWire;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;
import org.easymock.IAnswer;
import org.easymock.IArgumentMatcher;
import org.easymock.EasyMock;

/**
 * @version $$Rev$$ $$Date$$
 */
public class WireTestCase extends TestCase {

    private static final String SCRIPT = "import org.apache.tuscany.container.groovy.mock.Greeting;"
        + "class Foo implements Greeting{"
        + "   Greeting wire;"
        + "   "
        + "   String setWire(Greeting ref){"
        + "       wire = ref;"
        + "       return null;"
        + "   };"
        + "   "
        + "   String greet(String name){"
        + "       return wire.greet(name);  "
        + "   };"
        + "}";

    private static final String SCRIPT2 = "import org.apache.tuscany.container.groovy.mock.Greeting;"
        + "class Foo implements Greeting{"
        + "   Greeting wire;"
        + "   "
        + "   String setWire(Greeting ref){"
        + "       wire = ref;"
        + "       return null;"
        + "   };"
        + "   "
        + "   public String greet(String name){"
        + "       return name;  "
        + "   }"
        + "}";

    private Class<? extends GroovyObject> implClass1;
    private Class<? extends GroovyObject> implClass2;
    private ScopeContainer scopeContainer;
    private WireService wireService;

    /**
     * Tests a basic invocation down a source wire
     */
    public void testReferenceWireInvocation() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyConfiguration configuration = new GroovyConfiguration();
        configuration.setName("source");
        configuration.setGroovyClass(implClass1);
        configuration.setServices(services);
        configuration.setWireService(createWireService());
        GroovyAtomicComponent component = new GroovyAtomicComponent(configuration);
        component.setScopeContainer(scopeContainer);
        OutboundWire wire = createLocalOutboundWire("wire", Greeting.class);
        terminateWire(wire);

        TargetInvoker invoker = createMock(TargetInvoker.class);
        expect(invoker.isCacheable()).andReturn(false);
        Message response = new MessageImpl();
        response.setBody("foo");
        expect(invoker.invoke(eqMessage())).andReturn(response);
        replay(invoker);

        for (OutboundInvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(invoker);
        }
        component.addOutboundWire(wire);
        Greeting greeting = (Greeting) component.getTargetInstance();
        assertEquals("foo", greeting.greet("foo"));
        verify(invoker);
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
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyConfiguration configuration = new GroovyConfiguration();
        configuration.setName("source");
        configuration.setGroovyClass(implClass2);
        configuration.setServices(services);
        configuration.setWireService(createWireService());
        GroovyAtomicComponent component = new GroovyAtomicComponent(configuration);
        component.setScopeContainer(scopeContainer);
        Operation<Type> operation = new Operation<Type>("greet", null, null, null, false, null, NO_CONVERSATION);
        TargetInvoker invoker = component.createTargetInvoker(null, operation, null);
        assertEquals("foo", invoker.invokeTarget(new String[]{"foo"}, TargetInvoker.NONE));
    }


    /**
     * Tests a basic invocation down a target wire
     */
    public void testTargetWireInvocation() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyConfiguration configuration = new GroovyConfiguration();
        configuration.setName("source");
        configuration.setGroovyClass(implClass2);
        configuration.setServices(services);
        configuration.setWireService(createWireService());
        GroovyAtomicComponent component = new GroovyAtomicComponent(configuration);
        component.setScopeContainer(scopeContainer);
        InboundWire wire = createLocalInboundWire("Greeting", Greeting.class);
        terminateWire(wire);
        for (InboundInvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(component.createTargetInvoker(null, chain.getOperation(), null));
        }
        component.addInboundWire(wire);
        Greeting greeting = wireService.createProxy(Greeting.class, component.getInboundWire("Greeting"));
        assertEquals("foo", greeting.greet("foo"));
    }

    @SuppressWarnings({"unchecked"})
    protected void setUp() throws Exception {
        super.setUp();
        GroovyClassLoader cl = new GroovyClassLoader(getClass().getClassLoader());
        implClass1 = cl.parseClass(SCRIPT);
        implClass2 = cl.parseClass(SCRIPT2);
        scopeContainer = createMock(ScopeContainer.class);
        expect(scopeContainer.getInstance(isA(AtomicComponent.class))).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return ((AtomicComponent) getCurrentArguments()[0]).createInstance();
            }
        });
        EasyMock.expect(scopeContainer.getScope()).andReturn(Scope.COMPOSITE).anyTimes();
        replay(scopeContainer);
        wireService = ArtifactFactory.createWireService();
    }
}
