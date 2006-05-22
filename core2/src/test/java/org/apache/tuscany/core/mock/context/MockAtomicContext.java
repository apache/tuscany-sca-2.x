package org.apache.tuscany.core.mock.context;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.context.PojoAtomicContext;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Provides a runtime context for Java component implementations
 *
 * @version $Rev: 408473 $ $Date: 2006-05-21 12:46:01 -0700 (Sun, 21 May 2006) $
 */
public class MockAtomicContext<T> extends PojoAtomicContext<T> {

    public MockAtomicContext(String name, List<Class<?>> serviceInterfaces, ObjectFactory<?> objectFactory, Scope scope, Map<String, Member> members) {
        super(name, null, serviceInterfaces, objectFactory, false, null, null, null, members);
        this.scope = scope;
    }

    public Object getService(String name) throws TargetException {
        // FIXME implement
        return getTargetInstance();
    }

    public T getService() throws TargetException {
        if (serviceInterfaces.size() == 1) {
            return getTargetInstance();
        } else {
            throw new TargetException("Context must contain exactly one service");
        }
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        return new MockTargetInvoker(operation, this);
    }


}
