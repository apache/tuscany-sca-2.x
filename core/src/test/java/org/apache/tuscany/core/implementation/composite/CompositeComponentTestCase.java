package org.apache.tuscany.core.implementation.composite;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class CompositeComponentTestCase extends TestCase {

    public void testSetUri() throws Exception {
        CompositeComponentImpl<?> component = new CompositeComponentImpl("foo", null, null);
        component.setURI("foo/bar");
        assertEquals("foo/bar", component.getURI());
    }
}
