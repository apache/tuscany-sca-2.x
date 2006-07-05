package org.apache.tuscany.core.injection;

import org.apache.tuscany.spi.ObjectCreationException;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class MonitorObjectFactoryTestCase extends MockObjectTestCase {

    public void testFactory() {
        NullMonitorFactory monitorFactory = new NullMonitorFactory();
        Mock mock = mock(AutowireComponent.class);
        mock.expects(once()).method("resolveInstance").will(returnValue(monitorFactory));
        AutowireComponent parent = (AutowireComponent) mock.proxy();
        MonitorObjectFactory<Foo> factory = new MonitorObjectFactory<Foo>(parent, Foo.class);
        factory.getInstance().log("foo");
    }

    public void testNoFactory() {
        Mock mock = mock(AutowireComponent.class);
        mock.expects(once()).method("resolveInstance");
        AutowireComponent parent = (AutowireComponent) mock.proxy();
        try {
            MonitorObjectFactory<Foo> factory = new MonitorObjectFactory<Foo>(parent, Foo.class);
            factory.getInstance();
            fail();
        } catch (ObjectCreationException e) {
            //expected
        }
    }

    private interface Foo {
        void log(String foo);
    }
}
