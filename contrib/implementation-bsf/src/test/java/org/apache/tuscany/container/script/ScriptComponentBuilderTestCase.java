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
package org.apache.tuscany.container.script;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import org.easymock.IAnswer;

public class ScriptComponentBuilderTestCase extends TestCase {

    public void testGetImplementationType() {
        ScriptComponentBuilder builder = new ScriptComponentBuilder();
        assertEquals(ScriptImplementation.class, builder.getImplementationType());
    }

    @SuppressWarnings("unchecked")
    public void testBuild() throws Exception {
        ScriptComponentBuilder builder = new ScriptComponentBuilder();
        DeploymentContext deploymentContext = createMock(DeploymentContext.class);
        final ScopeContainer scopeContainer = createMock(ScopeContainer.class);
        expect(scopeContainer.getScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return Scope.COMPOSITE;
            }
        });
        expect(deploymentContext.getCompositeScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return scopeContainer;
            }
        });
        replay(deploymentContext);
        ComponentDefinition<ScriptImplementation> impl =
            new ComponentDefinition<ScriptImplementation>(new ScriptImplementation());
        ScriptComponentType componentType = new ScriptComponentType();
        componentType.setLifecycleScope(Scope.COMPOSITE);
        ServiceDefinition service = new ServiceDefinition();
        ServiceContract serviceContract = new JavaServiceContract();
        service.setServiceContract(serviceContract);
        componentType.add(service);
        impl.getImplementation().setComponentType(componentType);

        PropertyValue<String> pv = new PropertyValue<String>("foo", "", "");
        ObjectFactory<String> pvFactory = (ObjectFactory<String>) createMock(ObjectFactory.class);
        expect(pvFactory.getInstance()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return null;
            }
        });
        replay(pvFactory);
        pv.setValueFactory(pvFactory);
        impl.add(pv);

        Component component = builder.build(null, impl, deploymentContext);
        assertNotNull(component);
    }

    @SuppressWarnings("unchecked")
    public void testBuildCompositeScope() throws Exception {
        ScriptComponentBuilder builder = new ScriptComponentBuilder();
        DeploymentContext deploymentContext = createMock(DeploymentContext.class);
        final ScopeContainer scopeContainer = createMock(ScopeContainer.class);
        expect(scopeContainer.getScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return Scope.COMPOSITE;
            }
        });
        expect(deploymentContext.getCompositeScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return scopeContainer;
            }
        });
        replay(deploymentContext);
        ComponentDefinition<ScriptImplementation> impl =
            new ComponentDefinition<ScriptImplementation>(new ScriptImplementation());
        ScriptComponentType componentType = new ScriptComponentType();
        ServiceDefinition service = new ServiceDefinition();
        ServiceContract serviceContract = new JavaServiceContract();
        service.setServiceContract(serviceContract);
        componentType.add(service);
        impl.getImplementation().setComponentType(componentType);
        Component component = builder.build(null, impl, deploymentContext);
        assertNotNull(component);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
}
