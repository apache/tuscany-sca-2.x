package org.apache.tuscany.core.injection;

import java.lang.reflect.Field;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Injects a value created by an {@link org.apache.tuscany.spi.ObjectFactory} on a given field
 *
 * @version $Rev$ $Date$
 */
public class FieldInjector<T> implements Injector<T> {

    private final Field field;

    private final ObjectFactory<?> objectFactory;

    /**
     * Create an injector and have it use the given <code>ObjectFactory</code> to inject a value on the instance using
     * the reflected <code>Field</code>
     */
    public FieldInjector(Field field, ObjectFactory<?> objectFactory) {
        this.field = field;
        this.objectFactory = objectFactory;
    }

    /**
     * Inject a new value on the given isntance
     */
    public void inject(T instance) throws ObjectCreationException {
        try {
            field.set(instance, objectFactory.getInstance());
        } catch (IllegalAccessException e) {
            throw new AssertionError("Field is not accessible [" + field + "]");
        }
    }
}
