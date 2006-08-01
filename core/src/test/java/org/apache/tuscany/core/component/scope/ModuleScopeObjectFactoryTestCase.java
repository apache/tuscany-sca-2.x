package org.apache.tuscany.core.component.scope;

import org.jmock.MockObjectTestCase;
import org.jmock.Mock;
import org.apache.tuscany.spi.component.ScopeRegistry;

/**
 * @version $Rev$ $Date$
 */
public class ModuleScopeObjectFactoryTestCase extends MockObjectTestCase {

    public void testCreation() {
        Mock registry = mock(ScopeRegistry.class);
        registry.expects(once()).method("registerFactory").withAnyArguments();

        assertNotNull(new ModuleScopeObjectFactory((ScopeRegistry)registry.proxy()).getInstance());
    }
}
