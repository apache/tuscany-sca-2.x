package org.apache.tuscany.binding.axis2.util;

import java.net.URL;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axis2.om.OMElement;
import org.apache.tuscany.sdo.helper.TypeHelperImpl;
import org.apache.tuscany.sdo.helper.XSDHelperImpl;
import org.apache.tuscany.sdo.util.DataObjectUtil;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;

import commonj.sdo.DataObject;
import commonj.sdo.helper.TypeHelper;

public class AxiomHelperTestCase extends TestCase {

    public static final QName GREETING_QN = new QName("http://helloworldaxis.samples.tuscany.apache.org", "getGreetings");
    
    private TypeHelper typeHelper;

    public void testToObjects1() {
        String s = "petra";
        OMElement omElement = AxiomHelper.toOMElement(typeHelper, new Object[] { s }, GREETING_QN);
        assertNotNull(omElement);

        Object[] os = AxiomHelper.toObjects(typeHelper, omElement);
        assertNotNull(os);
        assertEquals(os.length, 1);
        assertEquals(os[0], s);
    }

    public void testToObjects2() {
        String s = "sue";
        DataObject dataObject = AxiomHelper.toDataObject(typeHelper, new Object[] { s }, GREETING_QN);
        assertNotNull(dataObject);

        Object[] os = AxiomHelper.toObjects(dataObject);
        assertNotNull(os);
        assertEquals(os.length, 1);
        assertEquals(os[0], s);
    }

    public void testToOMElement1() {
        String s = "beate";
        OMElement omElement = AxiomHelper.toOMElement(typeHelper, new Object[] { s }, GREETING_QN);
        assertNotNull(omElement);
    }

    public void testToOMElement2() {
        String s = "emma";
        DataObject dataObject = AxiomHelper.toDataObject(typeHelper, new Object[] { s }, GREETING_QN);
        assertNotNull(dataObject);

        OMElement omElement = AxiomHelper.toOMElement(typeHelper, dataObject, GREETING_QN);
        assertNotNull(omElement);
    }

    public void testToDataObject() {
        String s = "bersi";
        DataObject dataObject = AxiomHelper.toDataObject(typeHelper, new Object[] { s }, GREETING_QN);
        assertNotNull(dataObject);

        Object[] os = AxiomHelper.toObjects(dataObject);
        assertNotNull(os);
        assertEquals(os.length, 1);
        assertEquals(os[0], s);
    }

    protected void setUp() throws Exception {
        super.setUp();
        DataObjectUtil.initRuntime();
        ClassLoader cl=Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            typeHelper=SDOUtil.createTypeHelper();
            URL url = getClass().getResource("helloworld.wsdl");
            new XSDHelperImpl(typeHelper).define(url.openStream(), null);
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

}
