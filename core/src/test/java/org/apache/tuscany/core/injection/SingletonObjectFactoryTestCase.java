package org.apache.tuscany.core.injection;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class SingletonObjectFactoryTestCase extends TestCase {

    public void testSingleton() throws Exception {
        Object o = new Object();
        SingletonObjectFactory<Object> factory = new SingletonObjectFactory<Object>(o);
        assertEquals(o, factory.getInstance());
    }
}
