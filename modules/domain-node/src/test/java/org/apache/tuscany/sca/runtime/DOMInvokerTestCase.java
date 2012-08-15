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
package org.apache.tuscany.sca.runtime;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.impl.NodeImpl;
import org.junit.Test;
import org.xml.sax.SAXException;

public class DOMInvokerTestCase {

    private DOMHelper domHelper;

    @Test
    public void testInstallDeployable() throws Throwable {
        Node node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("helloworld", "src/test/resources/sample-helloworld.jar", null, null);
        node.startComposite("helloworld", "helloworld.composite");

        ExtensionPointRegistry registry = ((NodeImpl)node).getExtensionPointRegistry();
        this.domHelper = DOMHelper.getInstance(registry);

        DOMInvoker domInvoker = node.getDOMInvoker("HelloworldComponent");
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

}
