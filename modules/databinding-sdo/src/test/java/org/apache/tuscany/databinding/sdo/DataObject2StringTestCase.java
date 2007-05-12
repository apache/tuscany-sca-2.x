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

package org.apache.tuscany.databinding.sdo;

import junit.framework.Assert;

import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

import com.example.ipo.sdo.PurchaseOrderType;
import commonj.sdo.DataObject;

/**
 * 
 */
public class DataObject2StringTestCase extends SDOTransformerTestCaseBase {
    @Override
    protected DataType<?> getSourceDataType() {
        return new DataTypeImpl<XMLType>(binding, PurchaseOrderType.class, new XMLType(ORDER_QNAME, null));
    }

    @Override
    protected DataType<?> getTargetDataType() {
        return new DataTypeImpl<Class<String>>(String.class, String.class);
    }

    public final void testTransform() {
        String xml = new DataObject2String().transform(dataObject, context);
        Assert.assertTrue(xml.indexOf("<city>San Jose</city>") != -1);
        DataObject po = new String2DataObject().transform(xml, reversedContext);
        Assert.assertTrue(po instanceof PurchaseOrderType);
        PurchaseOrderType orderType = (PurchaseOrderType)po;
        Assert.assertEquals("San Jose", orderType.getBillTo().getCity());
    }

    public final void testXML() {
        String xml =
            "<foo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " 
            + "xmlns:ipo=\"http://www.example.com/IPO\" xsi:type=\"ipo:USAddress\"/>";
        DataObject dataObject = new String2DataObject().transform(xml, reversedContext);
        context.setSourceDataType(new DataTypeImpl<XMLType>(DataObject.class.getName(), DataObject.class, null));
        xml = new DataObject2String().transform(dataObject, context);
        Assert.assertTrue(xml.contains("xsi:type=\"ipo:USAddress\""));
    }

}
