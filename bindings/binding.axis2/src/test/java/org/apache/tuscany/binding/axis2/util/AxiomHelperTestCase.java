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

public class AxiomHelperTestCase extends TestCase {

    public static final QName GREETING_QN = new QName("http://helloworldaxis.samples.tuscany.apache.org", "getGreetings");

    public static final QName DOCLIT_QN = new QName("http://www.example.org/creditscore/doclit/", "getCreditScoreRequest");

    private TypeHelper typeHelper;

    public void testToObjects1() {
        String s = "petra";
        OMElement omElement = AxiomHelper.toOMElement(typeHelper, new Object[] { s }, GREETING_QN, true);
        assertNotNull(omElement);

        Object[] os = AxiomHelper.toObjects(typeHelper, omElement, true);
        assertNotNull(os);
        assertEquals(os.length, 1);
        assertEquals(os[0], s);
    }

    public void testToObjects2() {
        String s = "sue";
        DataObject dataObject = AxiomHelper.toDataObject(typeHelper, new Object[] { s }, GREETING_QN, true);
        assertNotNull(dataObject);

        Object[] os = AxiomHelper.toObjects(dataObject, true);
        assertNotNull(os);
        assertEquals(os.length, 1);
        assertEquals(os[0], s);
    }

    public void testToOMElement1() {
        String s = "beate";
        OMElement omElement = AxiomHelper.toOMElement(typeHelper, new Object[] { s }, GREETING_QN, true);
        assertNotNull(omElement);
    }

    public void testToOMElement2() {
        String s = "emma";
        DataObject dataObject = AxiomHelper.toDataObject(typeHelper, new Object[] { s }, GREETING_QN, true);
        assertNotNull(dataObject);

        OMElement omElement = AxiomHelper.toOMElement(typeHelper, dataObject, GREETING_QN);
        assertNotNull(omElement);
    }

    public void testToOMElement3() {
        DataFactory dataFactory = SDOUtil.createDataFactory(typeHelper);
        DataObject dataObject = dataFactory.create("http://www.example.org/creditscore/doclit/", "Customer");
        dataObject.setString(0, "111-22-3333");
        dataObject.setString(1, "John");
        dataObject.setString(2, "Smith");

        OMElement omElement = AxiomHelper.toOMElement(typeHelper, new Object[] { dataObject }, DOCLIT_QN, false);
        assertNotNull(omElement);

        Object[] os = AxiomHelper.toObjects(typeHelper, omElement, false);
        assertNotNull(os);
        assertEquals(os.length, 1);
        assertTrue(os[0] instanceof DataObject);

        dataObject = (DataObject) os[0];
        assertEquals(dataObject.getString(0), "111-22-3333");
        assertEquals(dataObject.getString(1), "John");
        assertEquals(dataObject.getString(2), "Smith");

        dataObject = AxiomHelper.toDataObject(typeHelper, omElement);
        assertEquals(dataObject.getString(0), "111-22-3333");
        assertEquals(dataObject.getString(1), "John");
        assertEquals(dataObject.getString(2), "Smith");

    }

    public void testToDataObject() {
        String s = "bersi";
        DataObject dataObject = AxiomHelper.toDataObject(typeHelper, new Object[] { s }, GREETING_QN, true);
        assertNotNull(dataObject);

        Object[] os = AxiomHelper.toObjects(dataObject, true);
        assertNotNull(os);
        assertEquals(os.length, 1);
        assertEquals(os[0], s);
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
            url = getClass().getResource("CreditScoreDocLit.wsdl");
            xsdHelper.define(url.openStream(), null);
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

}
