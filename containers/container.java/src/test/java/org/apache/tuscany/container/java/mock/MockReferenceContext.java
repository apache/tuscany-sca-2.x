package org.apache.tuscany.container.java.mock;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.extension.ReferenceContextExtension;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockReferenceContext extends ReferenceContextExtension {

    public MockReferenceContext(String name, TargetWire<?> wire) {
        this.name = name;
        this.targetWire = wire;
        this.referenceInterface = wire.getBusinessInterface();
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        return new Invoker();
    }


    /**
     * An invoker which echoes back the first parameter
     */
    private class Invoker implements TargetInvoker {

        public Object invokeTarget(Object payload) throws InvocationTargetException {
            assert(payload != null && payload.getClass().isArray() && (Array.getLength(payload) == 1)): "unknown param type";
            return Array.get(payload,0);
        }

        public boolean isCacheable() {
            return false;
        }

        public void setCacheable(boolean cacheable) {

        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
