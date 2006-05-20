/**
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
package org.apache.tuscany.launcher;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class MainLauncherTestCase extends TestCase {
    private MainLauncher launcher;

    public void testMissingClass() {
        launcher.setClassName("NoSuchClass");
        try {
            launcher.callApplication();
            fail();
        } catch (ClassNotFoundException e) {
            assertEquals("NoSuchClass", e.getMessage());
        } catch (Exception e) {
            fail();
        }
    }

    public void testMissingMain() {
        launcher.setClassName(NoMain.class.getName());
        try {
            launcher.callApplication();
            fail();
        } catch (InvalidMainException e) {
            assertEquals(NoMain.class.getName(), e.getMessage());
        } catch (Exception e) {
            fail();
        }
    }

    public void testNonStaticMain() {
        launcher.setClassName(NotStaticMain.class.getName());
        try {
            launcher.callApplication();
            fail();
        } catch (InvalidMainException e) {
            assertEquals("public void org.apache.tuscany.launcher.MainLauncherTestCase$NotStaticMain.main(java.lang.String[])", e.getMessage());
        } catch (Exception e) {
            fail();
        }
    }

    public void testApplicationReturns() {
        launcher.setClassName(AppClass.class.getName());
        launcher.setArgs(new String[]{"return"});
        try {
            launcher.callApplication();
        } catch (Exception e) {
            fail();
        }
    }

    public void testApplicationThrows() {
        launcher.setClassName(AppClass.class.getName());
        launcher.setArgs(new String[]{"throw"});
        try {
            launcher.callApplication();
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            assertTrue(cause instanceof UnsupportedOperationException);
            assertEquals("test exception", cause.getMessage());
        } catch (Exception e) {
            fail();
        }
    }

    public static class NoMain {
    }

    public static class NotStaticMain {
        public void main(String[] args) {
        }
    }

    public static class AppClass {
        public static void main(String[] args) {
            if (args == null || args.length != 1) {
                fail();
            }
            if ("return".equals(args[0])) {
                return;
            } else if ("throw".equals(args[0])) {
                throw new UnsupportedOperationException("test exception");
            } else {
                fail();
            }
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        launcher = new MainLauncher();
        launcher.setApplicationLoader(getClass().getClassLoader());
    }
}
