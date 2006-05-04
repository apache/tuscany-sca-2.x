package org.apache.tuscany.core.injection;

import java.lang.reflect.Field;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.common.ObjectCreationException;

/**
 * Injects a value created by an {@link org.apache.tuscany.common.ObjectFactory} on a given field
 *
 * @version $Rev: 399488 $ $Date: 2006-05-03 16:20:27 -0700 (Wed, 03 May 2006) $
 */
public class FieldInjector<T> implements Injector<T> {

    private final Field field;

    private final ObjectFactory<?> objectFactory;

    /**
     * Create an injector and have it use the given <code>ObjectFactory</code>
     * to inject a value on the instance using the reflected <code>Field</code>
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
