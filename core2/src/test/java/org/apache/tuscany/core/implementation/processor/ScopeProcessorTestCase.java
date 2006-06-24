package org.apache.tuscany.core.implementation.processor;

import org.apache.tuscany.spi.model.Scope;

import junit.framework.TestCase;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;

/**
 * @version $Rev$ $Date$
 */
public class ScopeProcessorTestCase extends TestCase {

    public void testModuleScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(Module.class, type, null);
        assertEquals(Scope.MODULE, type.getLifecycleScope());
    }

    public void testSessionScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(Session.class, type, null);
        assertEquals(Scope.SESSION, type.getLifecycleScope());
    }

    public void testRequestScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(Request.class, type, null);
        assertEquals(Scope.REQUEST, type.getLifecycleScope());
    }

    public void testCompositeScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(Composite.class, type, null);
        assertEquals(Scope.COMPOSITE, type.getLifecycleScope());
    }

    public void testStatelessScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(Stateless.class, type, null);
        assertEquals(Scope.STATELESS, type.getLifecycleScope());
    }

    public void testNoScope() throws ProcessingException {
        ScopeProcessor processor = new ScopeProcessor();
        PojoComponentType type = new PojoComponentType();
        processor.visitClass(None.class, type, null);
        assertEquals(Scope.STATELESS, type.getLifecycleScope());
    }

    @org.osoa.sca.annotations.Scope("MODULE")
    private class Module {
    }

    @org.osoa.sca.annotations.Scope("SESSION")
    private class Session {
    }

    @org.osoa.sca.annotations.Scope("REQUEST")
    private class Request {
    }

    @org.osoa.sca.annotations.Scope("COMPOSITE")
    private class Composite {
    }

    @org.osoa.sca.annotations.Scope("STATELESS")
    private class Stateless {
    }

    private class None {
    }

}
