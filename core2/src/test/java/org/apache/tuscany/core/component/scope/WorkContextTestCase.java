package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.WorkContext;

import org.apache.tuscany.core.component.WorkContextImpl;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class WorkContextTestCase extends MockObjectTestCase {

    public void testRemoteComponent() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        Mock mock = mock(CompositeComponent.class);
        CompositeComponent component = (CompositeComponent) mock.proxy();
        Mock mock2 = mock(CompositeComponent.class);
        CompositeComponent component2 = (CompositeComponent) mock2.proxy();
        ctx.setRemoteComponent(component);
        assertEquals(component, ctx.getRemoteComponent());
        ctx.setRemoteComponent(component2);
        assertEquals(component2, ctx.getRemoteComponent());
    }

    public void testNonSetRemoteComponent() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        assertNull(ctx.getRemoteComponent());
    }

    public void testIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        Object id = new Object();
        ctx.setIdentifier(this, id);
        assertEquals(id, ctx.getIdentifier(this));
    }

    public void testClearIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        Object id = new Object();
        ctx.setIdentifier(this, id);
        ctx.clearIdentifier(this);
        assertNull(ctx.getIdentifier(this));
    }

    public void testClearIndentifiers() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        Object id = new Object();
        Object id2 = new Object();
        ctx.setIdentifier(id, id);
        ctx.setIdentifier(id2, id2);
        ctx.clearIdentifiers();
        assertNull(ctx.getIdentifier(id));
        assertNull(ctx.getIdentifier(id2));
    }

    public void testClearNonExistentIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        ctx.clearIdentifier(this);
    }

    public void testNullIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        Object id = new Object();
        ctx.setIdentifier(this, id);
        ctx.clearIdentifier(null);
        assertEquals(id, ctx.getIdentifier(this));
    }

    public void testNoIndentifier() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        assertNull(ctx.getIdentifier(this));
    }


}
