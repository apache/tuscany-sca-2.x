package org.apache.tuscany.core.extension.config.extensibility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.core.extension.config.JavaExtensibilityElement;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.injection.ContextObjectFactory;
import org.apache.tuscany.core.builder.ContextResolver;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ContextExtensibilityElement implements JavaExtensibilityElement {

    private Method method;
    private Field field;

    public ContextExtensibilityElement(Method m) {
        method = m;
    }

    public ContextExtensibilityElement(Field f) {
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
