package org.apache.tuscany.core.injection;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.tuscany.spi.ObjectCreationException;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Invocation;
import org.jmock.core.Stub;

/**
 * @version $Rev$ $Date$
 */
public class JNDIObjectFactoryTestCase extends MockObjectTestCase {

    public void testGetInstance() throws Exception {
        Mock mock = mock(Context.class);
        mock.expects(once()).method("lookup").with(eq("foo")).will(returnValue(new Foo()));
        Context ctx = (Context) mock.proxy();
        JNDIObjectFactory<Foo> factory = new JNDIObjectFactory<Foo>(ctx, "foo");
        assertTrue(factory.getInstance() instanceof Foo); // must do an instanceof b/c of type erasure
    }

    public void testGetInstanceError() throws Exception {
        Mock mock = mock(Context.class);
        mock.expects(once()).method("lookup").with(eq("foo")).will(throwException(new NamingException()));
        Context ctx = (Context) mock.proxy();
        JNDIObjectFactory<Foo> factory = new JNDIObjectFactory<Foo>(ctx, "foo");
        try {
            factory.getInstance();
            fail();
        } catch (ObjectCreationException e) {
            //expected
        }
    }


    private class Foo {

    }
}
