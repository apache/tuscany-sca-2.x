package org.apache.tuscany.spi;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class TuscanyRuntimeExceptionTestCase extends TestCase {

    public void testIdentifier() throws Exception{
        TuscanyRuntimeException e = new TestException();
        e.setIdentifier("foo");
        assertEquals("foo",e.getIdentifier());
    }

    public void testAddContext() throws Exception{
        TuscanyRuntimeException e = new TestException();
        e.addContextName("foo");
        e.addContextName("bar");
        assertEquals("foo",e.returnContextNames().get(0));
        assertEquals("bar",e.returnContextNames().get(1));
    }

    public void testEmptyContext() throws Exception{
        TuscanyRuntimeException e = new TestException();
        assertEquals(0,e.returnContextNames().size());
    }

    public void testEmptyGetMessage() throws Exception{
        TuscanyRuntimeException e = new TestException();
        e.getMessage();
    }

    public void testFullMessage() throws Exception{
        TuscanyRuntimeException e = new TestException();
        e.setIdentifier("foo");
        e.addContextName("foo");
        e.getMessage();
    }


    private class TestException extends TuscanyRuntimeException{

    }
}
