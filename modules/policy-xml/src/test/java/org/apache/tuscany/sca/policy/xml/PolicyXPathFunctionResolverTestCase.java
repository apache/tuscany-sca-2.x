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

package org.apache.tuscany.sca.policy.xml;

import java.util.Collections;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 
 */
public class PolicyXPathFunctionResolverTestCase {
    private static XPathFactory factory;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        factory = XPathFactory.newInstance();
    }

    @Test
    public void testXPath() throws Exception {
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(new NamespaceContextImpl());
        xpath.setXPathFunctionResolver(new PolicyXPathFunctionResolver(xpath.getNamespaceContext()));
        InputSource xml = new InputSource(getClass().getResourceAsStream("Calculator.composite"));
        XPathExpression exp = xpath.compile("//sca:composite//sca:component[sca:IntentRefs('sca:confidentiality')]");
        Object result = exp.evaluate(xml, XPathConstants.NODESET);
        Assert.assertTrue(result instanceof NodeList);
        NodeList nodes = (NodeList)result;
        // Assert.assertEquals(1, nodes.getLength());
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    private static class NamespaceContextImpl implements NamespaceContext {

        private static final String SCA11_NS = "http://docs.oasis-open.org/ns/opencsa/sca/200712";

        public String getNamespaceURI(String prefix) {
            if ("sca".equals(prefix)) {
                return SCA11_NS;
            } else {
                return null;
            }
        }

        public String getPrefix(String namespaceURI) {
            if (SCA11_NS.equals(namespaceURI)) {
                return "sca";
            }
            return null;
        }

        public Iterator getPrefixes(String namespaceURI) {
            if (SCA11_NS.equals(namespaceURI)) {
                return Collections.singleton("sca").iterator();
            }
            return null;
        }

    }

}
