/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tuscany.container.js.rhino;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.mozilla.javascript.EcmaError;

/**
 * Tests for the RhinoInvoker
 */
public class RhinoInvokerTestCase extends TestCase {

    private static final String scriptName = "RhinoInvokerTestCase.js";
    private String script;
    
    protected void setUp() throws Exception {
        super.setUp();
        this.script = readResource(scriptName);
    }

    public void testSimpleInvocation() {
        RhinoScript ri = new RhinoScript(scriptName, script);
        Object x = ri.invoke("echo", "petra", null);
        assertEquals("petra", x);
    }

    public void testCopy() {
        RhinoScript ri = new RhinoScript(scriptName, script);
        Object x = ri.invoke("echo", "petra", null);
        assertEquals("petra", x);

        ri = ri.copy();
        x = ri.invoke("echo", "sue", null);
        assertEquals("sue", x);

    }

    public void testContexts1() {
        RhinoScript ri = new RhinoScript(scriptName, script);
        Map<String, Object> contexts = new HashMap<String, Object>();
        contexts.put("a", "petra");
        Object x = ri.invoke("getA", null, contexts);
        assertEquals("petra", x);
    }

    /**
     * Tests context not accessable across invocations
     */
    public void testContexts2() {
        RhinoScript ri = new RhinoScript(scriptName, script);
        Map<String, Object> contexts = new HashMap<String, Object>();
        contexts.put("a", "petra");
        Object x = ri.invoke("getA", null, contexts);
        assertEquals("petra", x);

        try {
            x = ri.invoke("getA", null, null);
            assertTrue("expected ReferenceError", false);
        } catch (EcmaError e) {
            assertEquals("ReferenceError", e.getName());
        }
    }

    /**
     * Tests shared scope is accessable across invocations
     */
    public void testScopes1() {
        RhinoScript ri = new RhinoScript(scriptName, script);
        ri.invoke("setGlobalVarY", "petra", null);

        Object x = ri.invoke("getGlobalVarY", null, null);
        assertEquals("petra", x);
    }

    /**
     * Tests local vars are NOT accessable across invocations
     */
    public void testScopes2() {
        RhinoScript ri = new RhinoScript(scriptName, script);
        ri.invoke("setLocalVarY", "petra", null);

        try {
            ri.invoke("getGlobalVarY", null, null);
            assertTrue("expected ReferenceError", false);
        } catch (EcmaError e) {
            assertEquals("ReferenceError", e.getName());
        }
    }

    /**
     * Tests shared scope is accessable when using contexts (ie an invocation scope)
     */
    public void testScopes3() {
        RhinoScript ri = new RhinoScript(scriptName, script);
        ri.invoke("setGlobalVarY", "petra", null);

        Map<String, Object> contexts = new HashMap<String, Object>();
        contexts.put("a", "sue");
        Object x = ri.invoke("getGlobalVarY", null, contexts);
        assertEquals("petra", x);

        x = ri.invoke("getA", null, contexts);
        assertEquals("sue", x);

    }

    /**
     * Tests a copy only retains the script scope not the shared scope
     */
    public void testScopes4() {
        RhinoScript ri = new RhinoScript(scriptName, script);
        ri.invoke("setGlobalVarY", "petra", null);

        ri = ri.copy();
        try {
            ri.invoke("getGlobalVarY", null, null);
            assertTrue("expected ReferenceError", false);
        } catch (EcmaError e) {
            assertEquals("ReferenceError", e.getName());
        }
        try {
            ri.invoke("getA", null, null);
            assertTrue("expected ReferenceError", false);
        } catch (EcmaError e) {
            assertEquals("ReferenceError", e.getName());
        }

    }

    public void testGetInt() {
        RhinoScript ri = new RhinoScript(scriptName, script);
        Object x = ri.invoke("getInt", null, Integer.TYPE, null);
        assertEquals(Integer.class, x.getClass());
    }

    /**
     * Read a resource into a String
     */
    private String readResource(String name) {
        try {
        URL url = getClass().getResource(name);
        if (url == null) {
            throw new RuntimeException("resource not found: " + name);
        }
        InputStream inputStream = url.openStream();

        StringBuffer resource = new StringBuffer();
        int n = 0;

            while ((n = inputStream.read()) != -1) {
                resource.append((char) n);
            }

            inputStream.close();

        String s = resource.toString();
        return s;

        } catch (IOException e) {
            throw new RuntimeException("IOException reading resource " + name, e);
        }
    }

}