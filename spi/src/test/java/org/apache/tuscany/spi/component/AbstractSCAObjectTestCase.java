package org.apache.tuscany.spi.component;

import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.event.TrueFilter;
import org.apache.tuscany.spi.model.Scope;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class AbstractSCAObjectTestCase extends MockObjectTestCase {

    public void testFireListener() {
        SCAObject object = new TestSCAObject("foo", null);
        Mock mock = mock(RuntimeEventListener.class);
        Event event = new TestEvent();
        mock.expects(once()).method("onEvent").with(eq(event));
        RuntimeEventListener listener = (RuntimeEventListener) mock.proxy();
        object.addListener(listener);
        object.publish(event);
    }

    public void testRemoveListener() {
        SCAObject object = new TestSCAObject("foo", null);
        Mock mock = mock(RuntimeEventListener.class);
        Event event = new TestEvent();
        mock.expects(never()).method("onEvent").with(eq(event));
        RuntimeEventListener listener = (RuntimeEventListener) mock.proxy();
        object.addListener(listener);
        object.removeListener(listener);
        object.publish(event);
    }

    public void testFalseFilterListener() {
        SCAObject object = new TestSCAObject("foo", null);
        Mock mock = mock(RuntimeEventListener.class);
        Event event = new TestEvent();
        mock.expects(never()).method("onEvent").with(eq(event));
        RuntimeEventListener listener = (RuntimeEventListener) mock.proxy();
        object.addListener(new FalseFilter(), listener);
        object.publish(event);
    }

    public void testTrueFilterListener() {
        SCAObject object = new TestSCAObject("foo", null);
        Mock mock = mock(RuntimeEventListener.class);
        Event event = new TestEvent();
        mock.expects(once()).method("onEvent").with(eq(event));
        RuntimeEventListener listener = (RuntimeEventListener) mock.proxy();
        object.addListener(new TrueFilter(), listener);
        object.publish(event);
    }

    public void testToString() {
        SCAObject object = new TestSCAObject("foo", null);
        assertNotNull(object.toString());
    }

    public void testGetName() {
        SCAObject object = new TestSCAObject("foo", null);
        assertEquals("foo", object.getName());
    }


    public void testToPrepare() {
        SCAObject object = new TestSCAObject("foo", null);
        object.prepare();
    }


    private class TestSCAObject extends AbstractSCAObject {
        public TestSCAObject(String name, CompositeComponent parent) {
            super(name, parent);
        }

        public Scope getScope() {
            return null;
        }

        public Object getServiceInstance() throws TargetException {
            return null;
        }
    }

    private class TestEvent implements Event {
        public Object getSource() {
            return null;
        }
    }

    private class FalseFilter implements EventFilter {

        public boolean match(Event event) {
            return false;
        }
    }


}



