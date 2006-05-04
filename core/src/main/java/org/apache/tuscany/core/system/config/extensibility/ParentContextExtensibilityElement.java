package org.apache.tuscany.core.system.config.extensibility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.injection.ContextObjectFactory;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.system.config.SystemInjectorExtensibilityElement;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ParentContextExtensibilityElement implements SystemInjectorExtensibilityElement {

    private Method method;
    private Field field;

    public ParentContextExtensibilityElement(Method m) {
        assert(m.getParameterTypes().length == 1): "Illegal number of parameters";
        method = m;
    }

    public ParentContextExtensibilityElement(Field f) {
        field = f;
    }

    public Injector<?> getInjector(ContextResolver resolver) {
        if (method != null) {
            return new MethodInjector(method, new ContextObjectFactory(resolver));
        } else {
            return new FieldInjector(field, new ContextObjectFactory(resolver));
        }
    }

}
