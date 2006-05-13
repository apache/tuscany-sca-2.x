package org.apache.tuscany.core.mock.context;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.ContextNotFoundException;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockCompositeContext<T> extends AbstractContext<T> implements CompositeContext<T> {

    public Object getInstance(QualifiedName qName) throws TargetException {
        throw new UnsupportedOperationException();
    }

    public void registerContext(Context context) {
        throw new UnsupportedOperationException();
    }

    public Context getContext(String name) {
        throw new UnsupportedOperationException();
    }

    public List<ServiceContext> getServiceContexts() {
        return Collections.emptyList();
    }

    public ServiceContext getServiceContext(String name) throws ContextNotFoundException {
        return null;
    }

    public List<ReferenceContext> getReferenceContexts() {
        return Collections.emptyList();
    }

    public void onEvent(Event event) {

    }

    public Object getService(String name) throws TargetException {
        throw new UnsupportedOperationException();
    }

    public List<Class<?>> getServiceInterfaces() {
        return null;
    }

    public void addTargetWire(TargetWire wire) {
        throw new UnsupportedOperationException();
    }

    public TargetWire getTargetWire(String serviceName) {
        return null;
    }

    public Map<String, TargetWire> getTargetWires() {
        return null;
    }

    public void addSourceWire(SourceWire wire) {
        throw new UnsupportedOperationException();
    }

    public void addSourceWires(Class<?> multiplicityClass, List<SourceWire> wires) {
        throw new UnsupportedOperationException();
    }

    public List<SourceWire> getSourceWires() {
        return null;
    }

    public void prepare() {

    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }

    public T getService() throws TargetException {
        return null;
    }
}
