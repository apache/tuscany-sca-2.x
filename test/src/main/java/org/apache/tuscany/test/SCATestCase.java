/*
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.test;

import junit.framework.TestCase;

import org.apache.tuscany.core.launcher.Launcher;
import org.apache.tuscany.core.launcher.CompositeContextImpl;
import org.apache.tuscany.spi.component.CompositeComponent;

/**
 * Base class for JUnit tests that want to run in an SCA client environment.
 *
 * @version $Rev$ $Date$
 */
public class SCATestCase extends TestCase {
    private Launcher launcher;
    private CompositeComponent<?> component;
    private CompositeContextImpl context;

    protected void setUp() throws Exception {
        super.setUp();
        launcher = new Launcher();
        launcher.setApplicationLoader(getClass().getClassLoader());
        launcher.bootRuntime();
        component = launcher.bootApplication();
        component.start();
        context = new CompositeContextImpl(component);
        context.start();
    }

    protected void tearDown() throws Exception {
        context.stop();
        component.stop();
        super.tearDown();
    }
}
