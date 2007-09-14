package org.apache.tuscany.sca.itest.admin;

import static junit.framework.Assert.assertEquals;

import org.apache.tuscany.sca.itest.admin.MyTotalService;
import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MyTotalServiceTestCase
{
    private static MyTotalService myTotalServiceOrg;
    private static MyTotalService myTotalServiceNew;

   private static SCADomain domain;

	 @Test
    public void testPropertyDefault()
    {
        assertEquals("RTP",myTotalServiceOrg.getLocation());
        assertEquals("2006",myTotalServiceOrg.getYear());
    }

    
	 @Test
    public void testPropertyOverrideVariable()
    {
        assertEquals("Raleigh",myTotalServiceNew.getLocation());
        assertEquals("2008",myTotalServiceNew.getYear());
    }

  
    @BeforeClass
    public static void init() throws Exception {
        try {
        domain = SCADomain.newInstance("Iteration3Composite.composite");
        } catch ( Exception e ) { e.printStackTrace(); }
		
        myTotalServiceOrg =domain.getService(MyTotalService.class, "MyTotalServiceComponent");
        myTotalServiceNew=domain.getService(MyTotalService.class, "MyTotalServiceNewComponent");
    }
	
    @AfterClass
    public static void destroy() throws Exception {
        domain.close();
    }
}
