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

package org.apache.tuscany.sca.common.xml.dom;

import static org.junit.Assert.assertNotNull;

import javax.xml.parsers.DocumentBuilder;

import org.apache.tuscany.sca.common.xml.sax.SAXHelper;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;

/**
 * Test Case for StAXHelper
 *
 * @version $Rev$ $Date$
 */
public class DOMHelperTestCase {
    private static final String XML =
        "<a:foo xmlns:a='http://a' name='foo'><bar name='bar'>" + "<doo a:name='doo' xmlns:a='http://doo'/>"
            + "</bar></a:foo>";

    private static ExtensionPointRegistry registry;

    @BeforeClass
    public static void init() {
        registry = new DefaultExtensionPointRegistry();
        registry.start();
    }

    @AfterClass
    public static void destroy() {
        if (registry != null) {
            registry.stop();
        }
    }

    @Test
    public void testHelper() throws Exception {
        DOMHelper helper = DOMHelper.getInstance(registry);
        Document document = helper.load(XML);
        String xml = helper.saveAsString(document);
        XMLAssert.assertXMLEqual(XML, xml);

        Document root = helper.newDocument();
        ContentHandler handler = helper.createContentHandler(root);

        DOMHelper helper2 = DOMHelper.getInstance(registry);
        Assert.assertSame(helper, helper2);

        SAXHelper saxHelper = new SAXHelper(registry);
        saxHelper.parse(XML, handler);

        assertNotNull(root.getFirstChild());
        xml = helper.saveAsString(root);
        XMLAssert.assertXMLEqual(XML, xml);
    }

    @Test
    public void testPool() {
        DOMHelper helper = DOMHelper.getInstance(registry);

        DocumentBuilder buidler1 = helper.newDocumentBuilder();
        Assert.assertTrue(helper.builderPool.getObjects().get(buidler1));

        Assert.assertEquals(1, helper.builderPool.inUse());

        DocumentBuilder buidler2 = helper.newDocumentBuilder();
        Assert.assertTrue(helper.builderPool.getObjects().get(buidler2));
        Assert.assertEquals(2, helper.builderPool.inUse());

        helper.returnDocumentBuilder(buidler2);
        Assert.assertFalse(helper.builderPool.getObjects().get(buidler2));
        Assert.assertEquals(1, helper.builderPool.inUse());

        helper.returnDocumentBuilder(buidler1);
        Assert.assertFalse(helper.builderPool.getObjects().get(buidler1));
        Assert.assertEquals(0, helper.builderPool.inUse());

    }

}
