package org.apache.tuscany.core.component.scope;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class ModuleScopeObjectFactoryTestCase extends TestCase {

    public void testCreation() {
        assertNotNull(new ModuleScopeObjectFactory().getInstance());
    }
}
