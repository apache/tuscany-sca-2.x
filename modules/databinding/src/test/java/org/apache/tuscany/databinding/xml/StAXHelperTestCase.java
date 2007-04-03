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

package org.apache.tuscany.databinding.xml;

import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.databinding.xml.StAXHelper;

import junit.framework.TestCase;

/**
 * Test Case for StAXHelper
 */
public class StAXHelperTestCase extends TestCase {
    private static final String XML =
        "<a:foo xmlns:a='http://a' name='foo'><bar name='bar'>" + "<doo a:name='doo' xmlns:a='http://doo'/>"
            + "</bar></a:foo>";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testHelper() throws Exception {
        XMLStreamReader reader = StAXHelper.createXMLStreamReader(XML);
        String xml = StAXHelper.save(reader);
        reader = StAXHelper.createXMLStreamReader(xml);
    }

}
