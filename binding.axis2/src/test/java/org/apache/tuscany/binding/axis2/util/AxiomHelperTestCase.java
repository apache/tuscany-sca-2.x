package org.apache.tuscany.binding.axis2.util;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.apache.axis2.om.OMElement;
import org.apache.tuscany.binding.axis2.util.AxiomHelper;

public class AxiomHelperTestCase extends TestCase{


    public void test1() throws SecurityException, NoSuchMethodException {        
        Method method = HelloWorld.class.getMethod("getGreetings", new Class[]{String.class});
        String s = "World";
        OMElement omElement = AxiomHelper.toOMElement(method,new Object[] {s}, null);
        Object[] os = AxiomHelper.toObjects(omElement);
        assertNotNull(os);
        assertEquals(os.length, 1);
        assertEquals(os[0], s);
    }
    
    
    
    protected void setUp() throws Exception {
        super.setUp();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    }
    
}

interface HelloWorld {

    public String getGreetings(String s);
}
