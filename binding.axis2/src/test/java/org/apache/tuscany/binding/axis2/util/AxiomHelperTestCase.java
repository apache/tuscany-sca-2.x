package org.apache.tuscany.binding.axis2.util;

import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axis2.om.OMElement;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XSDHelper;

public class AxiomHelperTestCase extends TestCase {

    public static final QName GREETING_QN = new QName("http://helloworldaxis.samples.tuscany.apache.org", "getGreetings");

    public void testToObjects1() {
        String s = "petra";
        OMElement omElement = AxiomHelper.toOMElement(new Object[] { s }, GREETING_QN);
        assertNotNull(omElement);

        Object[] os = AxiomHelper.toObjects(omElement);
        assertNotNull(os);
        assertEquals(os.length, 1);
        assertEquals(os[0], s);
    }

    public void testToObjects2() {
        String s = "sue";
        DataObject dataObject = AxiomHelper.toDataObject(new Object[] { s }, GREETING_QN);
        assertNotNull(dataObject);

        Object[] os = AxiomHelper.toObjects(dataObject);
        assertNotNull(os);
        assertEquals(os.length, 1);
        assertEquals(os[0], s);
    }

    public void testToOMElement1() {
        String s = "beate";
        OMElement omElement = AxiomHelper.toOMElement(new Object[] { s }, GREETING_QN);
        assertNotNull(omElement);
    }

    public void testToOMElement2() {
        String s = "emma";
        DataObject dataObject = AxiomHelper.toDataObject(new Object[] { s }, GREETING_QN);
        assertNotNull(dataObject);

        OMElement omElement = AxiomHelper.toOMElement(dataObject, GREETING_QN);
        assertNotNull(omElement);
    }

    public void testToDataObject() {
        String s = "bersi";
        DataObject dataObject = AxiomHelper.toDataObject(new Object[] { s }, GREETING_QN);
        assertNotNull(dataObject);

        Object[] os = AxiomHelper.toObjects(dataObject);
        assertNotNull(os);
        assertEquals(os.length, 1);
        assertEquals(os[0], s);
    }

    protected void setUp() throws Exception {
        super.setUp();
        URL url = getClass().getResource("helloworld.wsdl");
        XSDHelper.INSTANCE.define(url.openStream(), null);
    }

}
