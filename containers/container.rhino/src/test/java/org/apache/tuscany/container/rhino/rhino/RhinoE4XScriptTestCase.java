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
package org.apache.tuscany.container.rhino.rhino;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.sdo.helper.XSDHelperImpl;
import org.apache.tuscany.sdo.util.SDOUtil;

import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

/**
 * Tests for the RhinoE4XScript
 */
public class RhinoE4XScriptTestCase extends TestCase {

    private static final String scriptName = "RhinoE4XScriptTestCase.js";

    private String script;

    private E4XDataBinding dataBinding;

    protected void setUp() throws Exception {
        super.setUp();
        this.script = readResource(scriptName);
        TypeHelper th = SDOUtil.createTypeHelper();
        XSDHelper xsdHelper = new XSDHelperImpl(th);
        URL url = getClass().getResource("helloworld.wsdl");
        xsdHelper.define(url.openStream(), null);

        dataBinding = new E4XDataBinding(th);
        dataBinding.addElementQName("getGreetings", new QName("http://helloworld.samples.tuscany.apache.org", "getGreetings"));
    }

    public void testSimpleInvocation() throws IOException {
        RhinoE4XScript ri = new RhinoE4XScript(scriptName, script, null, null, dataBinding);
        Object x = ri.invoke("getGreetings", new Object[] { "petra" }, null);
        assertEquals(x, "hello petra");
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