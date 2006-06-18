package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.component.WorkContext;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.mock.component.StatelessComponent;
import org.apache.tuscany.core.mock.component.StatelessComponentImpl;
import org.apache.tuscany.core.mock.factories.MockFactory;
import org.apache.tuscany.core.system.component.SystemAtomicComponent;

/**
 * Unit tests for the module scope container
 *
 * @version $Rev$ $Date$
 */
public class BasicStatelessScopeTestCase extends TestCase {

    /**
     * Tests instance identity is properly maintained
     */
    public void testInstanceManagement() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        StatelessScopeContainer scope = new StatelessScopeContainer(ctx);
        scope.start();
        SystemAtomicComponent context1 =
            MockFactory.createAtomicComponent("comp1", scope, StatelessComponentImpl.class);
        scope.register(context1);
        SystemAtomicComponent context2 =
            MockFactory.createAtomicComponent("comp2", scope, StatelessComponentImpl.class);
        scope.register(context2);
        StatelessComponentImpl comp1 = (StatelessComponentImpl) scope.getInstance(context1);
        Assert.assertNotNull(comp1);
        StatelessComponentImpl comp2 = (StatelessComponentImpl) scope.getInstance(context2);
        Assert.assertNotNull(comp2);
        Assert.assertNotSame(comp1, comp2);
        scope.stop();
    }

    public void testRegisterContextAfterRequest() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        StatelessScopeContainer scope = new StatelessScopeContainer(ctx);

        scope.start();
        SystemAtomicComponent context1 =
            MockFactory.createAtomicComponent("comp1", scope, StatelessComponentImpl.class);
        scope.register(context1);
        StatelessComponent comp1 = (StatelessComponentImpl) scope.getInstance(context1);
        Assert.assertNotNull(comp1);
        SystemAtomicComponent context2 =
            MockFactory.createAtomicComponent("comp2", scope, StatelessComponentImpl.class);
        scope.register(context2);
        StatelessComponentImpl comp2 = (StatelessComponentImpl) scope.getInstance(context2);
        Assert.assertNotNull(comp2);
        scope.stop();
    }


    /**
     * Tests setting no components in the scope
     */
    public void testSetNullComponents() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        StatelessScopeContainer scope = new StatelessScopeContainer(ctx);
        scope.start();
        scope.stop();
    }


}
