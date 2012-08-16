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
package test;

import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.impl.NodeImpl;
import org.apache.tuscany.sca.runtime.DOMInvoker;
import org.junit.Test;
import org.xml.sax.SAXException;

import sample.Helloworld;

/**
 * Shows programatically creating a contribution, composite and component, starting the composite and
 * then test invoking the components service.  
 */
public class DomInvokerTestCase extends TestCase {

    private ComponentTestCase service;
    private DOMHelper domHelper;

    @Override
    protected void setUp() throws Exception {

        // start the ComponentTestCase service
        service = new ComponentTestCase();
        service.setUp();
        service.testInvoke();

        this.domHelper = DOMHelper.getInstance(service.extensionPoints);
    }

    @Test
    public void testInvoke() throws Exception {

        DOMInvoker domInvoker = service.node.getDOMInvoker("testComponent");
        org.w3c.dom.Node arg = getRequestDOM("petra");
        org.w3c.dom.Node response = domInvoker.invoke("sayHello", arg);
        Assert.assertEquals("Hello petra", getResponseString(response));
    }

    private String getResponseString(org.w3c.dom.Node responseDOM) {
        String xml = domHelper.saveAsString(responseDOM); 
        int x = xml.indexOf("<return>") + "<return>".length();
        int y = xml.indexOf("</return>");
        return xml.substring(x, y);
    }

    private org.w3c.dom.Node getRequestDOM(String name) {
        try {

            String xml = "<ns2:sayHello xmlns:ns2=\"http://sample/\"><arg0>"+ name + "</arg0></ns2:sayHello>";
            return domHelper.load(xml);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        service.tearDown();
    }

}
