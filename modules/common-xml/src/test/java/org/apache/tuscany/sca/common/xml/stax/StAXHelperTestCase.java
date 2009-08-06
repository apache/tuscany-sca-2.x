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

package org.apache.tuscany.sca.common.xml.stax;

import static org.junit.Assert.assertNotNull;

import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import org.w3c.dom.Node;

/**
 * Test Case for StAXHelper
 *
 * @version $Rev$ $Date$
 */
public class StAXHelperTestCase {
    private static final String XML =
        "<a:foo xmlns:a='http://a' name='foo'><bar name='bar'>" + "<doo a:name='doo' xmlns:a='http://doo'/>"
            + "</bar></a:foo>";

    @Test
    public void testHelper() throws Exception {
        StAXHelper helper = new StAXHelper(new DefaultExtensionPointRegistry());
        XMLStreamReader reader = helper.createXMLStreamReader(XML);
        String xml = helper.saveAsString(reader);
        XMLAssert.assertXMLEqual(XML, xml);
        reader = helper.createXMLStreamReader(xml);
        assertNotNull(reader);

        Node node = helper.saveAsNode(reader);
        // reader = helper.createXMLStreamReader(node);
        assertNotNull(node.getFirstChild());

    }

}
