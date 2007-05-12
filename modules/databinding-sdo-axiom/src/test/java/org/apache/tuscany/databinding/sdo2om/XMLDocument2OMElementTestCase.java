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

package org.apache.tuscany.databinding.sdo2om;

import java.io.StringWriter;

import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

import commonj.sdo.helper.XMLDocument;

/**
 * 
 */
public class XMLDocument2OMElementTestCase extends SDOTransformerTestCaseBase {

    @Override
    protected DataType<?> getSourceDataType() {
        return new DataTypeImpl<XMLType>(XMLDocument.class.getName(), XMLDocument.class, new XMLType(ORDER_QNAME, null));
    }

    @Override
    protected DataType<?> getTargetDataType() {
        return new DataTypeImpl<XMLType>(OMElement.class.getName(), OMElement.class, new XMLType(ORDER_QNAME, null));
    }

    public final void testTransform() throws XMLStreamException {
        XMLDocument document =
            helperContext.getXMLHelper().createDocument(dataObject,
                                                        ORDER_QNAME.getNamespaceURI(),
                                                        ORDER_QNAME.getLocalPart());
        OMElement element = new XMLDocument2OMElement().transform(document, context);
        Assert.assertEquals(ORDER_QNAME.getNamespaceURI(), element.getNamespace().getNamespaceURI());
        Assert.assertEquals(ORDER_QNAME.getLocalPart(), element.getLocalName());
        StringWriter writer = new StringWriter();
        element.serialize(writer);
    }

}
