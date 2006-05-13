/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.binding.axis2.util;

import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sdo.util.DataObjectUtil;
import org.apache.tuscany.sdo.util.SDOUtil;

import commonj.sdo.DataObject;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

public class SDODataBindingTestCase extends TestCase {
    public static final QName DOCLITWRAPPED_QN = new QName("http://www.example.org/creditscore/doclitwrapped/", "getCreditScore");

    public static final QName DOCLIT_QN = new QName("http://www.example.org/creditscore/doclit/", "getCreditScoreRequest");

    public static final QName GREETING_QN = new QName("http://helloworldaxis.samples.tuscany.apache.org", "getGreetings");

    private TypeHelper typeHelper;

    private SDODataBinding docLitWrappedDB;

    private SDODataBinding docLitDB;

    private SDODataBinding greetingDB;

    public void testToOMElement() {
        String s = "petra";

        OMElement omElement = greetingDB.toOMElement(new Object[] { s });
        assertNotNull(omElement);

        Object[] os = greetingDB.fromOMElement(omElement);
        assertNotNull(os);
        assertEquals(1, os.length);
        assertEquals(s, os[0]);
    }

    public void testDocLit() {
        DataFactory dataFactory = SDOUtil.createDataFactory(typeHelper);
        DataObject dataObject = dataFactory.create("http://www.example.org/creditscore/doclit/", "Customer");
        dataObject.setString(0, "111-22-3333");
        dataObject.setString(1, "John");
        dataObject.setString(2, "Smith");

        OMElement omElement = docLitDB.toOMElement(new Object[] { dataObject });
        assertNotNull(omElement);

        Object[] os = docLitDB.fromOMElement(omElement);
        assertNotNull(os);
        assertEquals(os.length, 1);
        assertTrue(os[0] instanceof DataObject);

        dataObject = (DataObject) os[0];
        assertEquals(dataObject.getString(0), "111-22-3333");
        assertEquals(dataObject.getString(1), "John");
        assertEquals(dataObject.getString(2), "Smith");
    }

    public void testDocLitWrapped() {
        Object[] args = new Object[] { "111-22-3333", "John", "Smith" };

        OMElement omElement = docLitWrappedDB.toOMElement(args);
        assertNotNull(omElement);

        Object[] os = docLitWrappedDB.fromOMElement(omElement);
        assertNotNull(os);
        assertEquals(os.length, 3);

        assertEquals(os[0], "111-22-3333");
        assertEquals(os[1], "John");
        assertEquals(os[2], "Smith");
    }

    protected void setUp() throws Exception {
        super.setUp();
        DataObjectUtil.initRuntime();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

            typeHelper = SDOUtil.createTypeHelper();
            XSDHelper xsdHelper = SDOUtil.createXSDHelper(typeHelper);
            URL url = getClass().getResource("helloworld.wsdl");
            xsdHelper.define(url.openStream(), null);
            url = getClass().getResource("CreditScoreDocLitWrapped.wsdl");
            xsdHelper.define(url.openStream(), null);
            url = getClass().getResource("CreditScoreDocLit.wsdl");
            xsdHelper.define(url.openStream(), null);

            this.greetingDB = new SDODataBinding(getClass().getClassLoader(),typeHelper, GREETING_QN, true);
            this.docLitWrappedDB = new SDODataBinding(getClass().getClassLoader(),typeHelper, DOCLITWRAPPED_QN, true);

            this.docLitDB = new SDODataBinding(getClass().getClassLoader(),typeHelper, DOCLIT_QN, false);

        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

}
