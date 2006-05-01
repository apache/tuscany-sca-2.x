package org.apache.tuscany.core.system.config.extensibility;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

import org.apache.tuscany.core.system.config.SystemExtensibilityElement;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.ContextObjectFactory;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.common.monitor.MonitorFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MonitorExtensibilityElement implements SystemExtensibilityElement {

    private Method method;
    private Field field;

    public MonitorExtensibilityElement(Method m) {
        assert(method.getParameterTypes().length == 1): "Illegal number of parameters";
        method = m;
    }

    public MonitorExtensibilityElement(Field f) {
        field = f;
    }

    public Injector<?> getInjector(MonitorFactory factory) {
        if (method != null) {
            Object monitor = factory.getMonitor(method.getParameterTypes()[0]);
            return new MethodInjector(method, new SingletonObjectFactory<Object>(monitor));
        } else {
            Object monitor = factory.getMonitor(field.getType());
            return new FieldInjector(field, new SingletonObjectFactory<Object>(monitor));
        }
    }

}
