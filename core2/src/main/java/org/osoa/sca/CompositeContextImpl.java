package org.osoa.sca;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.spi.component.CompositeComponent;


public class CompositeContextImpl implements CompositeContext {
    protected final CompositeComponent<?> composite;

    public CompositeContextImpl(final CompositeComponent<?> composite, final ClassLoader appclass) {
        this.composite = composite;
        Class<?> compClass;
        try {
            compClass = appclass.loadClass("org.osoa.sca.LauncherCurrentCompositeContext");
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }

        try {
            Method method = compClass.getDeclaredMethod("setContext", new Class[]{CompositeContext.class});
            method.invoke(null, new Object[]{this});
        } catch (NoSuchMethodException e) {
            throw new AssertionError();
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException();
        } catch (InvocationTargetException e) {
            // rethrow unchecked exceptions
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                // assertion because setContext() does not declare any checked exceptions
                throw new AssertionError(cause);
            }
        }
    }

    public ServiceReference createServiceReferenceForSession(Object arg0) {
        return null;
    }

    public ServiceReference createServiceReferenceForSession(Object arg0, String arg1) {
        return null;
    }

    public String getCompositeName() {
        return null;
    }

    public String getCompositeURI() {
        return null;
    }

    public RequestContext getRequestContext() {
        return null;
    }

    public <T> T locateService(Class<T> arg0, String arg1) {
        return arg0.cast(composite.getChild(arg1).getServiceInstance());
    }

    public ServiceReference newSession(String arg0) {
        return null;
    }

    public ServiceReference newSession(String arg0, Object arg1) {
        return null;
    }

}
