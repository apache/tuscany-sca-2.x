package org.apache.tuscany.core.component.scope;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class StatelessScopeContainerTestCase extends TestCase {

    public void testBadStopWithoutStart() throws Exception {
        StatelessScopeContainer container = new StatelessScopeContainer();
        try {
            container.stop();
            fail();
        } catch (IllegalStateException e) {
            //expected
        }
    }

    public void testBadDoubleStart() throws Exception {
        StatelessScopeContainer container = new StatelessScopeContainer();
        try {
            container.start();
            container.start();
            fail();
        } catch (IllegalStateException e) {
            //expected
        }
    }
}
