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
package org.apache.tuscany.container.script.helper;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.stream.XMLStreamException;

import junit.framework.TestCase;

import org.apache.tuscany.container.script.helper.ScriptHelperComponentTypeLoader;
import org.apache.tuscany.container.script.helper.ScriptHelperImplementation;
import org.apache.tuscany.container.script.helper.ScriptHelperInstanceFactory;
import org.apache.tuscany.container.script.helper.mock.MockInstanceFactory;
import org.apache.tuscany.core.loader.LoaderRegistryImpl;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Scope;
import org.easymock.IAnswer;

/**
 * 
 */
public class ScriptHelperComponentTypeLoaderTestCase extends TestCase {

    public void testGetSideFileName() {
        ScriptHelperComponentTypeLoader loader = new ScriptHelperComponentTypeLoader();
        assertEquals("BSFEasyTestCase.componentType", loader.getSideFileName("BSFEasyTestCase.mock"));
    }

    public void testGetSideFileNameNoDot() {
        ScriptHelperComponentTypeLoader loader = new ScriptHelperComponentTypeLoader();
      assertEquals("BSFEasyTestCase.componentType", loader.getSideFileName("BSFEasyTestCase"));
    }

    @SuppressWarnings("unchecked")
    public void testLoadFromSideFile() throws MalformedURLException, LoaderException, XMLStreamException {
        ScriptHelperComponentTypeLoader loader = new ScriptHelperComponentTypeLoader();
        LoaderRegistry loaderRegistry = new LoaderRegistryImpl() {
            public <MO extends ModelObject> MO load(CompositeComponent parent, ModelObject mo, URL url, Class<MO> type, DeploymentContext ctx) throws LoaderException {
                return (MO) new ComponentType();
            }
        };
        loader.setLoaderRegistry(loaderRegistry);
        loader.loadFromSidefile(null, null);
    }

    @SuppressWarnings("unchecked")
    public void testLoad() throws LoaderException {
        ScriptHelperInstanceFactory bsfEasy = new MockInstanceFactory("org/apache/tuscany/container/script/helper/foo.mock", getClass().getClassLoader());
        ScriptHelperComponentTypeLoader loader = new ScriptHelperComponentTypeLoader();
        LoaderRegistry loaderRegistry = new LoaderRegistryImpl() {
            public <MO extends ModelObject> MO load(CompositeComponent parent,
                    ModelObject mo,
                    URL url,
                    Class<MO> type,
                    DeploymentContext ctx) throws LoaderException {
                return (MO) new ComponentType();
            }
        };
        loader.setLoaderRegistry(loaderRegistry);
        ScriptHelperImplementation implementation = new ScriptHelperImplementation();
        implementation.setResourceName("org/apache/tuscany/container/script/helper/foo.mock");
        implementation.setScriptInstanceFactory(bsfEasy);
        DeploymentContext deploymentContext = createMock(DeploymentContext.class);
        final ScopeContainer scopeContainer = createMock(ScopeContainer.class);
        expect(scopeContainer.getScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return Scope.MODULE;
            }
        });
        expect(deploymentContext.getModuleScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return scopeContainer;
            }
        });
        replay(deploymentContext);
        loader.load(null, implementation, deploymentContext);
        assertNotNull(implementation.getComponentType());
    }

    @SuppressWarnings("unchecked")
    public void testLoadMissingSideFile() throws LoaderException {
        ScriptHelperInstanceFactory bsfEasy = new MockInstanceFactory("org/apche/tuscany/container/script/helper/foo.mock", getClass().getClassLoader());
        ScriptHelperComponentTypeLoader loader = new ScriptHelperComponentTypeLoader();
        ScriptHelperImplementation implementation = new ScriptHelperImplementation();
        implementation.setResourceName("org/apache/tuscany/container/script/helper/doesntExist");
        implementation.setScriptInstanceFactory(bsfEasy);
        DeploymentContext deploymentContext = createMock(DeploymentContext.class);
        final ScopeContainer scopeContainer = createMock(ScopeContainer.class);
        expect(scopeContainer.getScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return Scope.MODULE;
            }
        });
        expect(deploymentContext.getModuleScope()).andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return scopeContainer;
            }
        });
        replay(deploymentContext);
        try {
            loader.load(null, implementation, deploymentContext);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    public void testGetImplementationClass() {
        ScriptHelperComponentTypeLoader loader = new ScriptHelperComponentTypeLoader();
        assertEquals(ScriptHelperImplementation.class, loader.getImplementationClass());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }
}
