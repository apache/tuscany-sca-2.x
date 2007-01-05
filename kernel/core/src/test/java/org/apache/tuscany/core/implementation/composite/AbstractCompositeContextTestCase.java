package org.apache.tuscany.core.implementation.composite;

import org.osoa.sca.CompositeContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ReferenceBinding;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.launcher.CompositeContextImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class AbstractCompositeContextTestCase extends TestCase {

    public void testGetName() throws Exception {
        CompositeComponent composite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(composite.getName()).andReturn("foo");
        EasyMock.replay(composite);
        WireService wireService = EasyMock.createNiceMock(WireService.class);
        CompositeContext context = new TestCompositeContext(composite, wireService);
        assertEquals("foo", context.getCompositeName());
    }

    public void testAtomicLocate() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.isOptimizable()).andReturn(false);
        EasyMock.replay(wire);
        AtomicComponent child = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(child.getInboundWire(AbstractCompositeContextTestCase.FooService.class.getName()))
            .andReturn(wire);
        EasyMock.replay(child);
        CompositeComponent composite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(composite.getChild("Foo")).andReturn(child);
        EasyMock.replay(composite);

        WireService wireService = EasyMock.createMock(WireService.class);
        EasyMock.expect(
            wireService.createProxy(EasyMock.eq(AbstractCompositeContextTestCase.FooService.class), EasyMock.eq(wire)))
            .andReturn(new AbstractCompositeContextTestCase.FooService() {
            });
        EasyMock.replay(wireService);
        CompositeContext context = new TestCompositeContext(composite, wireService);
        context.locateService(FooService.class, "Foo");
        EasyMock.verify(wireService);
        EasyMock.verify(composite);
        EasyMock.verify(wire);
        EasyMock.verify(child);
    }

    /**
     * Verifies the locateService checks for wire optimizations and if possible, avoids proxying the target instance
     */
    public void testOptimizedAtomicLocate() throws Exception {
        ServiceContract<?> contract = new JavaServiceContract(AbstractCompositeContextTestCase.FooService.class);
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.isOptimizable()).andReturn(true);
        EasyMock.expect(wire.getTargetService()).andReturn(new AbstractCompositeContextTestCase.FooService());
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.replay(wire);
        AtomicComponent child = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(child.getInboundWire(FooService.class.getName()))
            .andReturn(wire);
        EasyMock.replay(child);
        CompositeComponent composite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(composite.getChild("Foo")).andReturn(child);
        EasyMock.replay(composite);

        WireService wireService = EasyMock.createMock(WireService.class);
        EasyMock.replay(wireService);
        CompositeContextImpl context = new CompositeContextImpl(composite, wireService);
        context.locateService(FooService.class, "Foo");
        EasyMock.verify(wireService);
        EasyMock.verify(composite);
        EasyMock.verify(wire);
        EasyMock.verify(child);
    }

    public void testCannotOptimizeAtomicLocate() throws Exception {
        ServiceContract<?> contract = new JavaServiceContract(Object.class);
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.isOptimizable()).andReturn(true);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract).atLeastOnce();
        EasyMock.replay(wire);
        AtomicComponent child = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(child.getInboundWire(FooService.class.getName()))
            .andReturn(wire);
        EasyMock.replay(child);
        CompositeComponent composite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(composite.getChild("Foo")).andReturn(child);
        EasyMock.replay(composite);

        WireService service = EasyMock.createMock(WireService.class);
        EasyMock.expect(
            service.createProxy(EasyMock.eq(FooService.class), EasyMock.eq(wire)))
            .andReturn(new AbstractCompositeContextTestCase.FooService() {
            });
        EasyMock.replay(service);

        CompositeContextImpl context = new CompositeContextImpl(composite, service);
        context.locateService(AbstractCompositeContextTestCase.FooService.class, "Foo");
        EasyMock.verify(service);
        EasyMock.verify(composite);
        EasyMock.verify(wire);
        EasyMock.verify(child);
    }

    public void testNoWireJavaInterfaceAtomicLocate() throws Exception {
        ServiceContract<?> contract = new JavaServiceContract();
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.isOptimizable()).andReturn(true);
        EasyMock.expect(wire.getServiceContract()).andReturn(contract);
        EasyMock.replay(wire);
        AtomicComponent child = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(child.getInboundWire(FooService.class.getName()))
            .andReturn(wire);
        EasyMock.replay(child);
        CompositeComponent composite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(composite.getChild("Foo")).andReturn(child);
        EasyMock.replay(composite);

        WireService service = EasyMock.createMock(WireService.class);
        EasyMock.expect(
            service.createProxy(EasyMock.eq(AbstractCompositeContextTestCase.FooService.class), EasyMock.eq(wire)))
            .andReturn(new AbstractCompositeContextTestCase.FooService() {
            });
        EasyMock.replay(service);

        CompositeContextImpl context = new CompositeContextImpl(composite, service);
        context.locateService(FooService.class, "Foo");
        EasyMock.verify(service);
        EasyMock.verify(composite);
        EasyMock.verify(wire);
        EasyMock.verify(child);
    }

    public void testServiceLocate() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getBindingType()).andReturn(InboundWire.LOCAL_BINDING);
        EasyMock.expect(wire.isOptimizable()).andReturn(false);
        EasyMock.replay(wire);
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        binding.setService(EasyMock.isA(Service.class));
        EasyMock.expect(binding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(binding);
        Service child = new ServiceImpl("Foo", null, null);
        child.addServiceBinding(binding);
        CompositeComponent composite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(composite.getChild("Foo")).andReturn(child);
        EasyMock.replay(composite);

        WireService service = EasyMock.createMock(WireService.class);
        EasyMock.expect(
            service.createProxy(EasyMock.eq(AbstractCompositeContextTestCase.FooService.class), EasyMock.eq(wire)))
            .andReturn(new AbstractCompositeContextTestCase.FooService() {
            });
        EasyMock.replay(service);
        CompositeContextImpl context = new CompositeContextImpl(composite, service);
        context.locateService(AbstractCompositeContextTestCase.FooService.class, "Foo");
        EasyMock.verify(service);
        EasyMock.verify(composite);
        EasyMock.verify(wire);
        EasyMock.verify(binding);
    }

    public void testReferenceLocate() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.isOptimizable()).andReturn(false);
        EasyMock.replay(wire);
        ReferenceBinding child = EasyMock.createMock(ReferenceBinding.class);
        EasyMock.expect(child.getInboundWire()).andReturn(wire);
        EasyMock.replay(child);
        CompositeComponent composite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(composite.getChild("Foo")).andReturn(child);
        EasyMock.replay(composite);

        WireService service = EasyMock.createMock(WireService.class);
        EasyMock.expect(
            service.createProxy(EasyMock.eq(AbstractCompositeContextTestCase.FooService.class), EasyMock.eq(wire)))
            .andReturn(new AbstractCompositeContextTestCase.FooService() {
            });
        EasyMock.replay(service);
        CompositeContextImpl context = new CompositeContextImpl(composite, service);
        context.locateService(AbstractCompositeContextTestCase.FooService.class, "Foo");
        EasyMock.verify(service);
        EasyMock.verify(composite);
        EasyMock.verify(wire);
        EasyMock.verify(child);
    }

    public void testCompositeLocate() throws Exception {
        InboundWire wire = EasyMock.createMock(InboundWire.class);
        EasyMock.expect(wire.getBindingType()).andReturn(InboundWire.LOCAL_BINDING);
        EasyMock.expect(wire.isOptimizable()).andReturn(false);
        EasyMock.replay(wire);
        ServiceBinding serviceBinding = EasyMock.createMock(ServiceBinding.class);
        serviceBinding.setService(EasyMock.isA(Service.class));
        EasyMock.expect(serviceBinding.getInboundWire()).andReturn(wire).atLeastOnce();
        EasyMock.replay(serviceBinding);
        Service service = new ServiceImpl("Foo", null, null);
        service.addServiceBinding(serviceBinding);
        CompositeComponent child = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(child.getService("Bar")).andReturn(service);
        EasyMock.replay(child);
        CompositeComponent composite = EasyMock.createMock(CompositeComponent.class);
        EasyMock.expect(composite.getChild("Foo")).andReturn(child);
        EasyMock.replay(composite);

        WireService wireService = EasyMock.createMock(WireService.class);
        EasyMock.expect(
            wireService.createProxy(EasyMock.eq(AbstractCompositeContextTestCase.FooService.class), EasyMock.eq(wire)))
            .andReturn(new AbstractCompositeContextTestCase.FooService() {
            });
        EasyMock.replay(wireService);
        CompositeContextImpl context = new CompositeContextImpl(composite, wireService);
        context.locateService(AbstractCompositeContextTestCase.FooService.class, "Foo/Bar");
        EasyMock.verify(wireService);
        EasyMock.verify(composite);
        EasyMock.verify(wire);
        EasyMock.verify(child);
        EasyMock.verify(serviceBinding);
    }

    private class FooService {

    }

    private class TestCompositeContext extends AbstractCompositeContext {

        public TestCompositeContext(final CompositeComponent composite, final WireService wireService) {
            super(composite, wireService);
        }

        public void start() {

        }

        public void stop() {

        }

        public RequestContext getRequestContext() {
            return null;
        }

        public ServiceReference createServiceReferenceForSession(Object self) {
            return null;
        }

        public ServiceReference createServiceReferenceForSession(Object self, String serviceName) {
            return null;
        }

        public ServiceReference newSession(String serviceName) {
            return null;
        }

        public ServiceReference newSession(String serviceName, Object sessionId) {
            return null;
        }
    }

}
