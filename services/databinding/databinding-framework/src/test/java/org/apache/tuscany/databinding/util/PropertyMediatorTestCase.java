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

package org.apache.tuscany.databinding.util;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.databinding.xml.XMLStreamReader2Node;
import org.w3c.dom.Node;

/**
 * 
 */
public class PropertyMediatorTestCase extends TestCase {
    private static final String XML_STR =
            "<?xml version=\"1.0\" encoding=\"ASCII\"?>"
                    + "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" xmlns:foo=\"http://foo.com\" "
                    + "name=\"AccountServices\">"
                    + "<property name=\"complexFoo\" type=\"foo:MyComplexType\"><foo:a>AValue</foo:a>"
                    + "<foo:b>InterestingURI</foo:b>" + "</property></composite>";

    private QName name = new QName("http://www.osoa.org/xmlns/sca/1.0", "property");

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testProperty() throws FactoryConfigurationError, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(XML_STR));

        boolean fragmentOnly = false;

        while (fragmentOnly) {
            int event = reader.getEventType();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (reader.getName().equals(name)) {
                    break;
                }
            }
            if (reader.hasNext()) {
                reader.next();
            } else {
                return;
            }
        }

        XMLStreamReader2Node t1 = new XMLStreamReader2Node();
        Node node = t1.transform(reader, null);
        Assert.assertNotNull(node);
    }

}
