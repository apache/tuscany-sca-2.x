/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.tuscany.container.java.integration;

import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;
import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ModuleContext;

import org.apache.tuscany.core.client.TuscanyRuntime;

/**
 * @version $Rev$ $Date$
 */
public class HelloWorldMCTestCase extends TestCase {
    private ClassLoader oldCL;

    public void testHelloWorld() throws Exception {
        TuscanyRuntime tuscany = new TuscanyRuntime("test", null);
        tuscany.start();
        ModuleContext moduleContext = CurrentModuleContext.getContext();
        assertNotNull(moduleContext);

        HelloWorldService helloworldService = (HelloWorldService) moduleContext.locateService("HelloWorld");
        assertNotNull(helloworldService);

        String value = helloworldService .getGreetings("World");
        assertEquals("Hello World", value);

        tuscany.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        URL url = getClass().getResource("/helloworldmc/");
        ClassLoader cl = new URLClassLoader(new URL[]{url}, getClass().getClassLoader());
        oldCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
    }

    protected void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(oldCL);
        super.tearDown();
    }
}
