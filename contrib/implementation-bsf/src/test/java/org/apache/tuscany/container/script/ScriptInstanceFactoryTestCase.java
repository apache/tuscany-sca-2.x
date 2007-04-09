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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

import junit.framework.TestCase;
import org.apache.bsf.BSFManager;
import org.apache.tuscany.container.script.mock.MockBSFEngine;

public class ScriptInstanceFactoryTestCase extends TestCase {

    public void testCreateInstance() throws InvocationTargetException {
        BSFManager.registerScriptingEngine("mock", MockBSFEngine.class.getName(), new String[]{"mock"});
        ScriptInstanceFactory factory =
            new ScriptInstanceFactory("foo.mock", "bar", "baz", getClass().getClassLoader());
        factory.addContextObjectFactory("foo", new SingletonObjectFactory("bar"));
        ScriptInstanceImpl instance = (ScriptInstanceImpl) factory.getInstance();
        assertNotNull(instance);
        assertNotNull(instance.bsfEngine);
    }

    public void testCreateInstanceNoClass() throws InvocationTargetException {
        BSFManager.registerScriptingEngine("mock", MockBSFEngine.class.getName(), new String[]{"mock"});
        ScriptInstanceFactory factory =
            new ScriptInstanceFactory("foo.mock", null, "baz", getClass().getClassLoader());
        factory.addContextObjectFactory("foo", new SingletonObjectFactory("bar"));
        ScriptInstanceImpl instance = (ScriptInstanceImpl) factory.getInstance();
        assertNotNull(instance);
        assertNotNull(instance.bsfEngine);
    }

    public void testCreateInstanceRuby() throws InvocationTargetException {
        BSFManager.registerScriptingEngine("ruby", MockBSFEngine.class.getName(), new String[]{"mock"});
        ScriptInstanceFactory factory =
            new ScriptInstanceFactory("foo.mock", "bar", "baz", getClass().getClassLoader());
        factory.addContextObjectFactory("foo", new SingletonObjectFactory("bar"));
        ScriptInstanceImpl instance = (ScriptInstanceImpl) factory.getInstance();
        assertNotNull(instance);
        assertNotNull(instance.bsfEngine);
    }

    public void testBadCreateInstance() throws InvocationTargetException {
        ScriptInstanceFactory factory =
            new ScriptInstanceFactory("foo", "bar", "baz", getClass().getClassLoader());
        try {
            factory.getInstance();
            fail();
        } catch (ObjectCreationException e) {
            // expected
        }
    }

    public void testGetters() throws InvocationTargetException {
        ScriptInstanceFactory factory =
            new ScriptInstanceFactory("foo", "bar", "baz", getClass().getClassLoader());
        assertEquals(getClass().getClassLoader(), factory.getClassLoader());
    }


    public void testGetResponseClasses() {
        ScriptInstanceFactory factory =
            new ScriptInstanceFactory("foo.mock", "bar", "baz", getClass().getClassLoader());
        Map<String, Class> classes = factory.getResponseClasses(Arrays.asList(new Class[]{Runnable.class}));
        assertEquals(1, classes.size());
        assertEquals("run", classes.keySet().iterator().next());
        assertEquals(void.class, classes.get("run"));
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    private class SingletonObjectFactory implements ObjectFactory<Object> {
        private Object instance;

        public SingletonObjectFactory(Object instance) {
            this.instance = instance;
        }

        public Object getInstance() throws ObjectCreationException {
            return instance;
        }
    }
}
