package org.apache.tuscany.core.component.event;

import org.apache.tuscany.spi.component.CompositeComponent;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class EventTestCase extends MockObjectTestCase {

    private CompositeComponent component;

    public void testCompositeStart() {
        CompositeStart event = new CompositeStart(this, component);
        assertEquals(component, event.getComposite());
    }

    public void testCompositeStop() {
        CompositeStop event = new CompositeStop(this, component);
        assertEquals(component, event.getComposite());
    }

    public void testHttpSessionStart() {
        Object id = new Object();
        HttpSessionEvent event = new HttpSessionStart(this, id);
        assertEquals(this, event.getSource());
        assertEquals(id, event.getId());
    }

    public void testHttpSessionEnd() {
        Object id = new Object();
        HttpSessionEvent event = new HttpSessionEnd(this, id);
        assertEquals(this, event.getSource());
        assertEquals(id, event.getId());
    }

    public void testRequestStart() {
        RequestStart event = new RequestStart(this);
        assertEquals(this, event.getSource());
    }

    public void testReequestEnd() {
        RequestEnd event = new RequestEnd(this);
        assertEquals(this, event.getSource());
    }


    protected void setUp() throws Exception {
        super.setUp();
        Mock mock = mock(CompositeComponent.class);
        component = (CompositeComponent) mock.proxy();
    }
}
