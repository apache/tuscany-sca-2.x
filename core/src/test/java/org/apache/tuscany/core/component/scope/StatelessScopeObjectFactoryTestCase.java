package org.apache.tuscany.core.component.scope;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.apache.tuscany.spi.component.ScopeRegistry;

/**
 * @version $Rev$ $Date$
 */
public class StatelessScopeObjectFactoryTestCase extends MockObjectTestCase {

    public void testCreation() {
        Mock registry = mock(ScopeRegistry.class);
        registry.expects(once()).method("registerFactory").withAnyArguments();

        assertNotNull(new StatelessScopeObjectFactory((ScopeRegistry)registry.proxy()).getInstance());
    }
}
