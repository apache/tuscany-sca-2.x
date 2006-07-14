package org.apache.tuscany.core.implementation.system.component;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.tuscany.core.implementation.PojoAtomicComponent;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWire;

/**
 * Default implementation of a system atomic context
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemAtomicComponentImpl<T> extends PojoAtomicComponent<T> implements SystemAtomicComponent<T> {

    public SystemAtomicComponentImpl(String name, PojoConfiguration configuration) {
        super(name, configuration);
        scope = Scope.MODULE;
    }

    @SuppressWarnings("unchecked")
    public T getTargetInstance() throws TargetException {
        return (T) scopeContainer.getInstance(this);
    }

    public Object getServiceInstance(String name) throws TargetException {
        return getTargetInstance();
    }

    public T getServiceInstance() throws TargetException {
        return getTargetInstance();
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }

    protected ObjectFactory<?> createWireFactory(OutboundWire wire) {
        assert wire instanceof SystemOutboundWire : "wire must be an instance of " + SystemOutboundWire.class.getName();
        SystemOutboundWire systemWire = (SystemOutboundWire) wire;
        return new SystemWireObjectFactory(systemWire);
    }

    public void prepare() {
        // override and do nothing since system services do not proxy
    }


}
