package org.apache.tuscany.sca.itest.admin;

import static junit.framework.Assert.assertEquals;

import org.apache.tuscany.sca.itest.admin.MyService;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;



public class MySimpleServiceInRecursiveTestCase
{
    private static MyService myServiceOrg;
    private static MyService myServiceAnother;
    private static MyService myServiceCary;

    private static SCADomain domain;

    
    @Test
    public void testPropertyDefault()
    {
        assertEquals("RTP",myServiceOrg.getLocation());
        assertEquals("2006",myServiceOrg.getYear());
    }

   
    @Test
    public void testPropertyOverrideValue()
    {
        assertEquals("CARY",myServiceCary.getLocation());
        assertEquals("2007",myServiceCary.getYear());
    }

   
    @Test
    public void testPropertyOverrideVariable()
    {
        assertEquals("Durham",myServiceAnother.getLocation());
        assertEquals("2009",myServiceAnother.getYear());
    }


    @BeforeClass
    public static void init() throws Exception {
        try {
        domain = SCADomain.newInstance("Iteration1Composite.composite");
        } catch ( Exception e ) { e.printStackTrace(); }
        
        myServiceOrg = domain.getService(MyService.class, "MySimpleServiceInRecursive/MyServiceOrig1");
        myServiceCary = domain.getService(MyService.class, "MySimpleServiceInRecursive/MyServiceCary1");
        myServiceAnother = domain.getService(MyService.class, "MySimpleServiceInRecursiveAnother/MyServiceNew1");
    }
	
	@AfterClass
    public static void destroy() throws Exception {
        domain.close();
    }
}
