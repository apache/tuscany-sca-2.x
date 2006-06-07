package org.apache.tuscany.spi.extension;

import junit.framework.TestCase;
import org.apache.tuscany.spi.model.Scope;

/**
 * @version $Rev$ $Date$
 */
public class ServiceExtensionTestCase extends TestCase {

    public void testScope() throws Exception {
        ServiceExtension service = new ServiceExtension(null, null, null);
        assertEquals(Scope.COMPOSITE, service.getScope());

    }

    public void testSetGetInterface() throws Exception {
        ServiceExtension service = new ServiceExtension(null, null, null);
        try {
            service.getInterface();
            fail();
        } catch (AssertionError e) {
            //expected
        }
    }


    public void testPrepare() throws Exception {
        ServiceExtension service = new ServiceExtension(null, null, null);
        service.prepare();
    }

}
