package org.apache.tuscany.core.component;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class CompositeComponentTestCase extends TestCase {

    public void testSetUri() throws Exception {
        CompositeComponentImpl<?> component = new CompositeComponentImpl("foo", null, null, null);
        component.setURI("foo/bar");
        assertEquals("foo/bar", component.getURI());
    }
}
