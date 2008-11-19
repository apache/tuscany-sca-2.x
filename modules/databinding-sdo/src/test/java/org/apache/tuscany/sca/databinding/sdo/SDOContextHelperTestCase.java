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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.Assert;

import org.apache.tuscany.sdo.api.SDOUtil;
import org.junit.Test;

import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

/**
 * @version $Rev$ $Date$
 */
public class SDOContextHelperTestCase {
    @Test
    public void testGenerateSchema() throws IOException {
        HelperContext context = SDOUtil.createHelperContext();
        URL url = getClass().getResource("/ipo.xsd");
        Assert.assertNotNull(url);
        InputStream is = url.openStream();
        XSDHelper xsdHelper = context.getXSDHelper();
        xsdHelper.define(is, url.toExternalForm());
        TypeHelper typeHelper = context.getTypeHelper();
        Type type = typeHelper.getType("http://www.example.com/IPO", "PurchaseOrderType");
        Assert.assertNotNull(type);
        /*
        SDOContextHelper.generateSchema(context, Arrays.asList(type));
        */
    }
}
