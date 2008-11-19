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

package org.apache.tuscany.sca.databinding.sdo;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.databinding.xml.Node2String;
import org.apache.tuscany.sca.databinding.xml.XMLStreamReader2Node;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

import com.example.ipo.sdo.PurchaseOrderType;
import commonj.sdo.DataObject;
import commonj.sdo.helper.EqualityHelper;

/**
 *
 * @version $Rev$ $Date$
 */
public class DataObject2XMLStreamReaderTestCase extends SDOTransformerTestCaseBase {

    @Override
    protected DataType<?> getSourceDataType() {
        return new DataTypeImpl<XMLType>(binding, PurchaseOrderType.class, new XMLType(ORDER_QNAME, null));
    }

    @Override
    protected DataType<?> getTargetDataType() {
        return new DataTypeImpl<Class<XMLStreamReader>>(XMLStreamReader.class, XMLStreamReader.class);
    }

    public final void testTransform() throws XMLStreamException {
        XMLStreamReader reader = new DataObject2XMLStreamReader().transform(dataObject, context);
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                break;
            }
        }
        DataObject d = new XMLStreamReader2DataObject().transform(reader, reversedContext);
        assertNotNull(d);
        assertTrue(EqualityHelper.INSTANCE.equal(dataObject, d));
    }
    
    public final void testTransform1() throws XMLStreamException {
        XMLStreamReader reader = new DataObject2XMLStreamReader().transform(dataObject, context);
        XMLStreamReader2Node t2 = new XMLStreamReader2Node();
        org.w3c.dom.Node node = t2.transform(reader, context);
        assertNotNull(node);
        Node2String t3 = new Node2String();
        String xml = t3.transform(node, context);
        assertTrue(xml.contains("xmlns:xsi"));
    }

}
