package org.apache.tuscany.spi.extension;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ComponentNotFoundException;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.component.IllegalTargetException;
import org.apache.tuscany.spi.component.InvalidComponentTypeException;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

/**
 * @version $Rev$ $Date$
 */
public class CompositeComponentExtensionTestCase extends TestCase {

    public void testGetScope() {
        assertEquals(Scope.COMPOSITE, new Composite().getScope());
    }

    public void testInvalidType() {
        Composite composite = new Composite();
        try {
            composite.register(getAtomic("foo"));
            fail();
        } catch (InvalidComponentTypeException e) {
            // expected
        }
    }

    public void testDuplicateName() {
        Composite composite = new Composite();
        composite.register(new ServiceExtension("foo", null, null));
        try {
            composite.register(new ServiceExtension("foo", null, null));
            fail();
        } catch (DuplicateNameException e) {
            // expected
        }
    }

    public void testGetChildren() {
        Composite composite = new Composite();
        composite.register(new ServiceExtension("foo", null, null));
        assertEquals(1, composite.getChildren().size());
    }

    public void testGetServices() {
        Composite composite = new Composite();
        composite.register(new ServiceExtension("foo", null, null));
        composite.register(getReference("bar"));
        assertEquals(1, composite.getServices().size());
    }

    public void testGetService() {
        Composite composite = new Composite();
        composite.register(new ServiceExtension("foo", null, null));
        composite.start();
        assertNotNull(composite.getService("foo"));
    }

    public void testServiceNotFound() {
        Composite composite = new Composite();
        composite.register(new ServiceExtension("foo", null, null));
        composite.start();
        try {
            composite.getService("bar");
            fail();
        } catch (ComponentNotFoundException e) {
            // expected
        }
    }

    public void testNotService() {
        Composite composite = new Composite();
        composite.register(getReference("foo"));
        composite.start();
        try {
            composite.getService("foo");
            fail();
        } catch (ComponentNotFoundException e) {
            // expected
        }
    }

    public void testReferencesServices() {
        Composite composite = new Composite();
        composite.register(new ServiceExtension("foo", null, null));
        composite.register(getReference("bar"));
        assertEquals(1, composite.getReferences().size());
    }

    public void testServiceInterfaces() {
        Composite<?> composite = new Composite();
        Service service1 = getService("foo", Foo.class);
        composite.register(service1);
        Service service2 = getService("bar", Bar.class);
        composite.register(service2);

        List<Class<?>> interfaces = composite.getServiceInterfaces();
        assertEquals(2, interfaces.size());
        for (Class o : interfaces) {
            if (!(Foo.class.isAssignableFrom(o)) && !(Bar.class.isAssignableFrom(o))) {
                fail();
            }
        }
    }

    public void testGetServiceInstanceByName() {
        Composite<?> composite = new Composite();
        Service service = createMock(Service.class);
        service.getName();
        expectLastCall().andReturn("foo").anyTimes();
        service.getInterface();
        expectLastCall().andReturn(Foo.class);
        service.getServiceInstance();
        expectLastCall().andReturn(new Foo() {
        });
        replay(service);
        composite.register(service);
        assertNotNull(composite.getServiceInstance("foo"));
    }

    public void testGetServiceInstanceNotFound() {
        Composite<?> composite = new Composite();
        Service service = getService("foo", Foo.class);
        composite.register(service);
        try {
            composite.getServiceInstance("bar");
            fail();
        } catch (TargetNotFoundException e) {
            //expected
        }
    }

    public void testGetServiceInstanceNotService() {
        Composite<?> composite = new Composite();
        Reference reference = getReference("foo");
        composite.register(reference);
        try {
            composite.getServiceInstance("foo");
            fail();
        } catch (IllegalTargetException e) {
            //expected
        }
    }

    public void testOnEvent() {
        Composite<?> composite = new Composite();
        Event event = new Event() {
            public Object getSource() {
                return null;
            }
        };
        RuntimeEventListener listener = createMock(RuntimeEventListener.class);
        listener.onEvent(eq(event));
        expectLastCall();
        replay(listener);
        composite.addListener(listener);
        composite.onEvent(event);
    }

    public void testPrepare() {
        Composite<?> composite = new Composite();
        composite.prepare();
    }

// TODO method not implemented
//    public void testSingleGetServiceInstance(){
//        Composite<?> composite = new Composite();
//        Mock mock = mock(Service.class);
//        mock.stubs().method("getName").will(returnValue("foo"));
//        mock.stubs().method("getInterface").will(returnValue(Foo.class));
//        mock.expects(once()).method("getServiceInstance").will(returnValue(new Foo(){}));
//        Service service = (Service) mock.proxy();
//        composite.register(service);
//        assertNotNull(composite.getServiceInstance());
//    }

    private class Composite<T> extends CompositeComponentExtension<T> {
        public Composite() {
            super(null, null, null);
        }

        public void setScopeContainer(ScopeContainer scopeContainer) {

        }

        public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
            return null;
        }
    }

    private AtomicComponent getAtomic(String name) {
        AtomicComponent component = createMock(AtomicComponent.class);
        component.getName();
        expectLastCall().andReturn(name).anyTimes();
        replay(component);
        return component;
    }

    private Reference getReference(String name) {
        Reference reference = createMock(Reference.class);
        reference.getName();
        expectLastCall().andReturn(name).anyTimes();
        replay(reference);
        return reference;
    }

    private Service getService(String name, Class<?> interfaze) {
        Service service = createMock(Service.class);
        service.getName();
        expectLastCall().andReturn(name).anyTimes();
        service.getInterface();
        expectLastCall().andReturn(interfaze);
        replay(service);
        return service;
    }

    private interface Foo {
    }

    private interface Bar {
    }

}
