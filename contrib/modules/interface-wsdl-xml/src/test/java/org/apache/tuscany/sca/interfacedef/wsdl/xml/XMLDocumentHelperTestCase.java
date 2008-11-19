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

package org.apache.tuscany.sca.interfacedef.wsdl.xml;

import java.net.URL;

import javax.xml.stream.XMLInputFactory;

import junit.framework.Assert;

import org.apache.tuscany.sca.xsd.xml.XMLDocumentHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class XMLDocumentHelperTestCase {
    private URL wsdl;
    private URL xsd;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        wsdl = getClass().getResource("/wsdl/helloworld-interface.wsdl");
    }

    @Test
    public void testReadTNS() throws Exception {
        String tns = XMLDocumentHelper.readTargetNamespace(wsdl, XMLDocumentHelper.WSDL11, true, "targetNamespace", XMLInputFactory.newInstance());
        Assert.assertEquals("http://helloworld", tns);
    }

}
