
package org.apache.tuscany.sca.itest;
import static junit.framework.Assert.assertEquals;
import mysca.test.myservice.impl.MyService;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MyTotalServiceTestCase {
    private static MyService service1;
    private static MyService service2;
    private static MyService service3;

    private static SCADomain domain;

    @Test
    public void testPropertyDefault() {
        assertEquals("RTP", service1.getLocation());
        assertEquals("2006", service1.getYear());
    }
    
    @Test
    public void testPropertyOverride() {
        assertEquals("Raleigh", service2.getLocation());
        assertEquals("2008", service2.getYear());
    }    

    @Test
    public void testPropertyNestedOverride() {
        assertEquals("Durham", service3.getLocation());
        assertEquals("2009", service3.getYear());
    }

    @BeforeClass
    /**
     * Sets up for the test case execution such as locating services.
     */
    public static void setUp() throws Exception {
        
        domain = SCADomain.newInstance("Outer.composite");
        service1 = domain.getService(MyService.class, "MyServiceComponent/MyService");
        service2 = domain.getService(MyService.class, "MyServiceComponentNew/MyService");
        service3 = domain.getService(MyService.class, "MySimpleServiceInRecursiveAnother");
    }
   
    @AfterClass
    public static void tearDown() {
        domain.close();
    }
}
