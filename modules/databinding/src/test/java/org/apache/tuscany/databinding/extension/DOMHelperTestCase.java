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

package org.apache.tuscany.databinding.extension;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;

import org.apache.tuscany.sca.databinding.impl.DOMHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

/**
 * 
 */
public class DOMHelperTestCase extends TestCase {
    private static final QName FOO_NAME = new QName("http://foo", "foo");

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testDOM() throws Exception {
        DocumentBuilder builder = DOMHelper.newDocumentBuilder();
        assertNotNull(builder);
        Document document = DOMHelper.newDocument();
        assertNotNull(document);
        Element element = DOMHelper.createElement(document, FOO_NAME);
        document.appendChild(element);
        QName name = DOMHelper.getQName(element);
        assertEquals(FOO_NAME, name);

    }

}
