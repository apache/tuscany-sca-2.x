package org.apache.tuscany.core.extension.config.extensibility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.MethodInjector;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.apache.tuscany.core.injection.FieldInjector;
import org.apache.tuscany.core.extension.config.JavaExtensibilityElement;

/**
 * @version $$Rev$$ $$Date$$
 */
public class ComponentNameExtensibilityElement implements JavaExtensibilityElement {

    private Method method;
    private Field field;

    public ComponentNameExtensibilityElement(Method m) {
        method = m;
    }

    public ComponentNameExtensibilityElement(Field f) {
        field = f;
    }

    public Injector<?> getEventInvoker(String name) {
        if (method != null) {
            return new MethodInjector(method, new SingletonObjectFactory<Object>(name));
        }else{
            return new FieldInjector(field, new SingletonObjectFactory<Object>(name));
        }
    }

}
