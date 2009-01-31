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

import junit.framework.Assert;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.example.ipo.sdo.PurchaseOrderType;
import commonj.sdo.DataObject;

/**
 *
 * @version $Rev$ $Date$
 */
public class DataObject2NodeTestCase extends SDOTransformerTestCaseBase {
    @Override
    protected DataType<?> getSourceDataType() {
        return new DataTypeImpl<XMLType>(binding, PurchaseOrderType.class, new XMLType(ORDER_QNAME, null));
    }

    @Override
    protected DataType<?> getTargetDataType() {
        return new DataTypeImpl<Class<String>>(String.class, String.class);
    }

    public final void testTransform() {
        Node node = new DataObject2Node().transform(dataObject, context);
        Assert.assertNotNull(node);
        Element element = (Element) node;
        Assert.assertEquals(ORDER_QNAME.getNamespaceURI(), element.getNamespaceURI());
        Assert.assertEquals(ORDER_QNAME.getLocalPart(), element.getLocalName());
        DataObject po = new Node2DataObject().transform(node, reversedContext);
        Assert.assertTrue(po instanceof PurchaseOrderType);
        PurchaseOrderType orderType = (PurchaseOrderType)po;
        Assert.assertEquals("San Jose", orderType.getBillTo().getCity());
    }

}
