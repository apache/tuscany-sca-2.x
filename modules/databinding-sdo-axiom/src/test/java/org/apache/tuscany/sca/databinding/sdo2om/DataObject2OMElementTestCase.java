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

package org.apache.tuscany.sca.databinding.sdo2om;

import java.io.StringWriter;

import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

import commonj.sdo.DataObject;

/**
 * 
 */
public class DataObject2OMElementTestCase extends SDOTransformerTestCaseBase {

    @Override
    protected DataType<?> getSourceDataType() {
        return new DataTypeImpl<XMLType>(DataObject.class.getName(), DataObject.class, new XMLType(ORDER_QNAME, null));
    }

    @Override
    protected DataType<?> getTargetDataType() {
        return new DataTypeImpl<XMLType>(OMElement.class.getName(), OMElement.class, new XMLType(ORDER_QNAME, null));
    }

    public final void testTransform() throws XMLStreamException {
        OMElement element = new DataObject2OMElement().transform(dataObject, context);
        Assert.assertEquals(ORDER_QNAME.getNamespaceURI(), element.getNamespace().getNamespaceURI());
        Assert.assertEquals(ORDER_QNAME.getLocalPart(), element.getLocalName());
        // TODO: See https://issues.apache.org/jira/browse/WSCOMMONS-226
        // element.getBuilder().setCache(false);
        StringWriter writer = new StringWriter();
        element.serialize(writer);
    }

    public final void testTransformWrapper() throws XMLStreamException {
        OMElement element = new DataObject2OMElement().transform(dataObject, context);
        Assert.assertEquals(ORDER_QNAME.getNamespaceURI(), element.getNamespace().getNamespaceURI());
        Assert.assertEquals(ORDER_QNAME.getLocalPart(), element.getLocalName());

        OMNamespace ns = OMAbstractFactory.getOMFactory().createOMNamespace("http://ns1", "ns1");
        element.setNamespace(ns);
        element.setLocalName("dummy");
        // TODO: See https://issues.apache.org/jira/browse/WSCOMMONS-226
        // element.getBuilder().setCache(true);
        StringWriter writer = new StringWriter();
        element.serializeAndConsume(writer);
        // System.out.println(writer);
    }

}
