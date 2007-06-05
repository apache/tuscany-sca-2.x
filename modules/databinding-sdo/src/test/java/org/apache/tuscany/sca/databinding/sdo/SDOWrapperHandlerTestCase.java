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

import java.util.List;

import junit.framework.TestCase;

import org.apache.tuscany.sdo.api.SDOUtil;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

/**
 * @version $Rev$ $Date$
 */
public class SDOWrapperHandlerTestCase extends TestCase {
    private HelperContext context;
    private SDOWrapperHandler handler;

    public void setUp() throws Exception {
        context = SDOUtil.createHelperContext();
        handler = new SDOWrapperHandler();
    }

    public void testWrapperAnyType() throws Exception {
        XMLHelper xmlHelper = context.getXMLHelper();
        XMLDocument document = xmlHelper.load(getClass().getResourceAsStream("/wrapper.xml"));
        List children = handler.getChildren(document);
        assertEquals(5, children.size());
    }

    public void testWrapper() throws Exception {
        XSDHelper xsdHelper = context.getXSDHelper();
        xsdHelper.define(getClass().getResourceAsStream("/wrapper.xsd"), null);
        XMLHelper xmlHelper = context.getXMLHelper();
        XMLDocument document = xmlHelper.load(getClass().getResourceAsStream("/wrapper.xml"));
        List children = handler.getChildren(document);
        assertEquals(5, children.size());
    }

}
