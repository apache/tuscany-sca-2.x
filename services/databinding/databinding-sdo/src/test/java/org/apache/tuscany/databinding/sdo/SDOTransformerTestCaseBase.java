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

import org.apache.tuscany.core.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.model.DataType;

import com.example.ipo.sdo.PurchaseOrderType;
import com.example.ipo.sdo.SdoFactory;
import com.example.ipo.sdo.USAddress;
import commonj.sdo.DataObject;

/**
 * The base class for SDO-related test cases
 */
public abstract class SDOTransformerTestCaseBase extends TestCase {
    protected String binding = DataObject.class.getName();

    protected static final QName orderQName = new QName("http://www.example.com/IPO", "purchaseOrder");

    protected TransformationContext context;
    protected TransformationContext reversedContext; 
    protected DataObject dataObject;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        SDOUtil.registerStaticTypes(SdoFactory.class);
        context = new TransformationContextImpl();
        context.setSourceDataType(getSourceDataType());
        context.setTargetDataType(getTargetDataType());

        reversedContext = new TransformationContextImpl();
        reversedContext.setSourceDataType(getTargetDataType());
        reversedContext.setTargetDataType(getSourceDataType());
        
        PurchaseOrderType po = SdoFactory.INSTANCE.createPurchaseOrderType();
        USAddress address = SdoFactory.INSTANCE.createUSAddress();
        address.setCity("San Jose");
        address.setStreet("123 ABC St");
        address.setState("CA");
        address.setStreet("95131");
        po.setBillTo(address);
        dataObject = (DataObject) po;
    }

    protected abstract DataType<?> getSourceDataType();

    protected abstract DataType<?> getTargetDataType();

}
