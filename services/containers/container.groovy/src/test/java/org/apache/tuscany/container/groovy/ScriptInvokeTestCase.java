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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.model.Scope;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import junit.framework.TestCase;
import org.apache.tuscany.container.groovy.mock.Greeting;
import static org.apache.tuscany.test.ArtifactFactory.createWireService;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import org.easymock.IAnswer;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ScriptInvokeTestCase extends TestCase {

    private static final String SCRIPT = "def greet(name) { return name }";

    private Class<? extends GroovyObject> implClass;
    private ScopeContainer scopeContainer;

    /**
     * Tests the invocation of a Groovy "script" as opposed to a class
     */
    public void testBasicScriptInvocation() throws Exception {
        List<Class<?>> services = new ArrayList<Class<?>>();
        services.add(Greeting.class);
        GroovyConfiguration configuration = new GroovyConfiguration();
        configuration.setName("source");
        configuration.setGroovyClass(implClass);
        configuration.setServices(services);
        configuration.setWireService(createWireService());
        GroovyAtomicComponent component = new GroovyAtomicComponent(configuration);
        component.setScopeContainer(scopeContainer);
        GroovyObject object = component.getTargetInstance();
        assertEquals("foo", object.invokeMethod("greet", "foo"));
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        GroovyClassLoader cl = new GroovyClassLoader(getClass().getClassLoader());
        implClass = cl.parseClass(SCRIPT);
        scopeContainer = createMock(ScopeContainer.class);
        expect(scopeContainer.getInstance(isA(AtomicComponent.class))).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return ((AtomicComponent) getCurrentArguments()[0]).createInstance();
            }
        });
        EasyMock.expect(scopeContainer.getScope()).andReturn(Scope.COMPOSITE).anyTimes();
        replay(scopeContainer);
    }
}
