package org.apache.tuscany.spi.loader;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class LoaderExceptionTestCase extends TestCase {

    public void testResourceURI() throws Exception{
        LoaderException e = new LoaderException();
        e.setResourceURI("test");
        assertEquals("test",e.getResourceURI());
    }
}
