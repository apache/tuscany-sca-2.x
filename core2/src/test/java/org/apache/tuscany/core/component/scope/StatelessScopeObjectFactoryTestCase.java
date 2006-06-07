package org.apache.tuscany.core.component.scope;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class StatelessScopeObjectFactoryTestCase extends TestCase {

    public void testCreation() {
        assertNotNull(new StatelessScopeObjectFactory().getInstance());
    }
}
