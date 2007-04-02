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

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.stream.XMLStreamException;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

public class ScriptComponentTypeLoaderTestCase extends TestCase {

    public void testGetSideFileName() {
        ScriptComponentTypeLoader loader = new ScriptComponentTypeLoader();
        assertEquals("BSFEasyTestCase.componentType", loader.getSideFileName("BSFEasyTestCase.mock"));
    }

    public void testGetSideFileNameNoDot() {
        ScriptComponentTypeLoader loader = new ScriptComponentTypeLoader();
        assertEquals("BSFEasyTestCase.componentType", loader.getSideFileName("BSFEasyTestCase"));
    }

    @SuppressWarnings("unchecked")
    public void testLoad() throws MalformedURLException, LoaderException, XMLStreamException {
        CompositeComponent parent = EasyMock.createNiceMock(CompositeComponent.class);
        DeploymentContext context = EasyMock.createNiceMock(DeploymentContext.class);
        LoaderRegistry registry = EasyMock.createMock(LoaderRegistry.class);
        registry.load(EasyMock.eq(parent),
            EasyMock.isA(ScriptComponentType.class),
            EasyMock.isA(URL.class),
            EasyMock.isA(Class.class),
            EasyMock.eq(context));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return EasyMock.getCurrentArguments()[1];
            }
        });
        EasyMock.replay(registry);

        ScriptImplementation implementation = new ScriptImplementation();
        implementation.setResourceName("org/apache/tuscany/container/script/helper/foo.componentType");
        implementation.setClassLoader(getClass().getClassLoader());
        ScriptComponentTypeLoader loader = new ScriptComponentTypeLoader();
        loader.setLoaderRegistry(registry);
        loader.load(parent, implementation, context);
        assertNotNull(implementation.getComponentType());
    }

    @SuppressWarnings("unchecked")
    public void testLoadMissingSideFile() throws MalformedURLException, LoaderException, XMLStreamException {
        CompositeComponent parent = EasyMock.createNiceMock(CompositeComponent.class);
        DeploymentContext context = EasyMock.createNiceMock(DeploymentContext.class);
        LoaderRegistry registry = EasyMock.createMock(LoaderRegistry.class);
        registry.load(EasyMock.eq(parent),
            EasyMock.isA(ScriptComponentType.class),
            EasyMock.isA(URL.class),
            EasyMock.isA(Class.class),
            EasyMock.eq(context));
        EasyMock.expectLastCall().andStubAnswer(new IAnswer() {
            public Object answer() throws Throwable {
                return EasyMock.getCurrentArguments()[1];
            }
        });
        EasyMock.replay(registry);

        ScriptImplementation implementation = new ScriptImplementation();
        implementation.setResourceName("notthere");
        implementation.setClassLoader(getClass().getClassLoader());
        ScriptComponentTypeLoader loader = new ScriptComponentTypeLoader();
        loader.setLoaderRegistry(registry);
        try {
            loader.load(parent, implementation, context);
            fail();
        } catch (MissingSideFileException e) {
            //expected
        }
    }

    public void testGetImplementationClass() {
        ScriptComponentTypeLoader loader = new ScriptComponentTypeLoader();
        assertEquals(ScriptImplementation.class, loader.getImplementationClass());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }
}
