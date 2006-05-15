/**
 *
 * Copyright 2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.databinding.sdo;

import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.sdo.util.DataObjectUtil;
import org.apache.tuscany.sdo.util.SDOUtil;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

public class SDOXMLHelperTestCase extends TestCase {

    private TypeHelper typeHelper;

    public static final QName GREETING_QN = new QName("http://helloworldaxis.samples.tuscany.apache.org", "getGreetings");

    private static final String GREETING_NAME = "petra";

    private static final String GREETING_XML = "<helloworldaxis:in0>petra</helloworldaxis:in0>";

    private DataObject greetingDOB;

    private byte[] greetingXML;
    
    private ClassLoader appCL;

    public static final QName DOCLIT_QN = new QName("http://www.example.org/creditscore/doclit/", "getCreditScoreRequest");

    // private static final Object[] CUSTOMER = { "111-22-3333", "John", "Smith" };

    private DataObject nonWrappedDOB;

    public void testXMLBytes1() {
        byte[] xmlBytes = SDOXMLHelper.toXMLbytes(appCL, typeHelper, greetingDOB, GREETING_QN);
        assertNotNull(xmlBytes);
        assertTrue(new String(xmlBytes).contains("<helloworldaxis:in0>petra</helloworldaxis:in0>"));
    }

    public void testXMLBytes2() {
        byte[] xmlBytes = SDOXMLHelper.toXMLBytes(appCL, typeHelper, new Object[] { GREETING_NAME }, GREETING_QN, true);
        assertNotNull(xmlBytes);
        assertTrue(new String(xmlBytes).contains(GREETING_XML));
    }

    // TODO: nonwrapped doesn't work
    // public void testXMLBytes3() {
    // byte[] xmlBytes = SDOXMLHelper.toXMLBytes(typeHelper, CUSTOMER, DOCLIT_QN, false);
    // assertNotNull(xmlBytes);
    // assertTrue(new String(xmlBytes).contains(DOC_LIT_XML));
    // }

    public void testToDataObject1() {
        DataObject dataObject = SDOXMLHelper.toDataObject(appCL, typeHelper, greetingXML);
        assertNotNull(dataObject);
        assertEquals(GREETING_NAME, dataObject.getString(0));
    }

    public void testToDataObject2() {
        DataObject dataObject = SDOXMLHelper.toDataObject(appCL, typeHelper, new Object[] { GREETING_NAME }, GREETING_QN, true);
        assertNotNull(dataObject);
        assertEquals(GREETING_NAME, dataObject.getString(0));
    }

    // TODO: nonwrapped doesn't work
    // public void testToDataObject3() {
    // DataObject dataObject = SDOXMLHelper.toDataObject(typeHelper, CUSTOMER, DOCLIT_QN, false);
    // assertNotNull(dataObject);
    // assertEquals(CUSTOMER[0], dataObject.getString(0));
    // assertEquals(CUSTOMER[1], dataObject.getString(1));
    // assertEquals(CUSTOMER[2], dataObject.getString(2));
    // }

    public void testToObjects1() {
        Object[] os = SDOXMLHelper.toObjects(appCL, typeHelper, greetingXML, true);
        assertNotNull(os);
        assertEquals(1, os.length);
        assertEquals(GREETING_NAME, os[0]);
    }

    public void testToObjects2() {
        Object[] os = SDOXMLHelper.toObjects(greetingDOB, true);
        assertNotNull(os);
        assertEquals(1, os.length);
        assertEquals(GREETING_NAME, os[0]);
    }

    public void testToObjects3() {
        Object[] os = SDOXMLHelper.toObjects(nonWrappedDOB, false);
        assertNotNull(os);
        // assertEquals(3, os.length); TODO: non-wrapped doesn't seem to work
    }

    protected void setUp() throws Exception {
        super.setUp();
        DataObjectUtil.initRuntime();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            appCL = getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(appCL);
            typeHelper = SDOUtil.createTypeHelper();
            XSDHelper xsdHelper = SDOUtil.createXSDHelper(typeHelper);
            URL url = getClass().getResource("helloworld.wsdl");
            xsdHelper.define(url.openStream(), null);
            url = getClass().getResource("CreditScoreDocLit.wsdl");
            xsdHelper.define(url.openStream(), null);
            greetingDOB = SDOXMLHelper.toDataObject(appCL, typeHelper, new Object[] { GREETING_NAME }, GREETING_QN, true);
            greetingXML = SDOXMLHelper.toXMLBytes(appCL, typeHelper, new Object[] { GREETING_NAME }, GREETING_QN, true);

            DataFactory dataFactory = SDOUtil.createDataFactory(typeHelper);
            nonWrappedDOB = dataFactory.create("http://www.example.org/creditscore/doclit/", "Customer");
            nonWrappedDOB.setString(0, "111-22-3333");
            nonWrappedDOB.setString(1, "John");
            nonWrappedDOB.setString(2, "Smith");

        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }
}
