package org.apache.tuscany.core.mock.context;

import java.lang.reflect.Method;

import org.apache.tuscany.core.wire.jdk.PojoTargetInvoker;
import org.apache.tuscany.spi.context.TargetException;

/**
 *
 * @version $Rev: 408473 $ $Date: 2006-05-21 12:46:01 -0700 (Sun, 21 May 2006) $
 */
public class MockTargetInvoker extends PojoTargetInvoker {

    private MockAtomicContext context;
    private Object target;
    public boolean cacheable;


    /**
     * Creates a new invoker
     *
     * @param operation the operation the invoker is associated with
     * @param context   the scope context the component is resolved in
     */
    public MockTargetInvoker(Method operation, MockAtomicContext context) {
        super(operation);
        assert (context != null) : "No atomic context specified";
        this.context = context;
    }

    /**
     * Resolves the target service instance or returns a cached one
     */
    protected Object getInstance() throws TargetException {
        if (!cacheable) {
            return context.getTargetInstance();
        } else {
            if (target == null) {
                target = context.getTargetInstance();
            }
            return target;
        }
    }

    public MockTargetInvoker clone() throws CloneNotSupportedException {
        MockTargetInvoker invoker = (MockTargetInvoker) super.clone();
        invoker.target = null;
        invoker.cacheable = this.cacheable;
        invoker.context = this.context;
        return invoker;
    }
}
