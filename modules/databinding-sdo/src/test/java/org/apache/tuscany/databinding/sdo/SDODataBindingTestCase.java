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

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

import com.example.ipo.sdo.PurchaseOrderType;
import com.example.ipo.sdo.SdoFactory;
import com.example.ipo.sdo.USAddress;
import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.impl.HelperProvider;

/**
 * 
 */
public class SDODataBindingTestCase extends TestCase {
    protected static final QName ORDER_QNAME = new QName("http://www.example.com/IPO", "purchaseOrder");
    private SDODataBinding binding;
    private HelperContext context;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        binding = new SDODataBinding();
        context = HelperProvider.getDefaultContext();
        SdoFactory.INSTANCE.register(context);
    }

    public final void testIntrospect() {
        DataType dataType = new DataTypeImpl<Class>(DataObject.class, null);
        boolean yes = binding.introspect(dataType, null);
        assertTrue(yes);
        assertTrue(dataType.getDataBinding().equals(binding.getName()));
        assertTrue(dataType.getPhysical() == DataObject.class && dataType.getLogical() == XMLType.UNKNOWN);
        dataType = new DataTypeImpl<Class>(PurchaseOrderType.class, null);
        yes = binding.introspect(dataType, null);
        assertTrue(yes);
        assertEquals(PurchaseOrderType.class, dataType.getPhysical());
        assertEquals(new QName("http://www.example.com/IPO", "PurchaseOrderType"), ((XMLType)dataType.getLogical())
            .getTypeName());
        dataType = new DataTypeImpl<Class>(USAddress.class, null);
        yes = binding.introspect(dataType, null);
        assertTrue(yes);
        assertEquals(USAddress.class, dataType.getPhysical());
        assertEquals(new QName("http://www.example.com/IPO", "USAddress"), ((XMLType)dataType.getLogical())
            .getTypeName());
    }

    public final void testCopyRoot() {
        PurchaseOrderType po = SdoFactory.INSTANCE.createPurchaseOrderType();
        po.setComment("Comment");
        Object copy = binding.copy(po);
        assertTrue(copy instanceof PurchaseOrderType);
        assertTrue(po != copy);
        assertTrue(context.getEqualityHelper().equal((DataObject)po, (DataObject)copy));
        assertEquals("Comment", ((PurchaseOrderType)copy).getComment());
    }

    public final void testCopyNonRoot() {
        USAddress address = SdoFactory.INSTANCE.createUSAddress();
        address.setCity("San Jose");
        Object copy = binding.copy(address);
        assertTrue(copy instanceof USAddress);
        assertTrue(address != copy);
        assertTrue(context.getEqualityHelper().equal((DataObject)address, (DataObject)copy));
        assertEquals("San Jose", ((USAddress)copy).getCity());
    }

    public final void testCopyXMLDocument() {
        PurchaseOrderType po = SdoFactory.INSTANCE.createPurchaseOrderType();
        po.setComment("Comment");
        XMLDocument doc =
            context.getXMLHelper().createDocument((DataObject)po,
                                                  ORDER_QNAME.getNamespaceURI(),
                                                  ORDER_QNAME.getLocalPart());
        Object copy = binding.copy(doc);
        assertTrue(copy instanceof XMLDocument);
        XMLDocument docCopy = (XMLDocument)copy;
        assertTrue(doc != copy);
        assertTrue(context.getEqualityHelper().equal((DataObject)po, docCopy.getRootObject()));
        assertEquals("Comment", ((PurchaseOrderType)docCopy.getRootObject()).getComment());
    }
}
