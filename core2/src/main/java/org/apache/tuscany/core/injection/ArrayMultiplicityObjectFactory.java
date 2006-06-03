package org.apache.tuscany.core.injection;

import java.lang.reflect.Array;
import java.util.List;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;

/**
 * Resolves targets configured in a multiplicity by delegating to object factories and returning an
 * <code>Array</code> containing object instances
 *
 * @version $Rev$ $Date$
 */
public class ArrayMultiplicityObjectFactory implements ObjectFactory<Object> {

    private ObjectFactory[] factories;

    private Class interfaceType;

    public ArrayMultiplicityObjectFactory(Class interfaceType, List<ObjectFactory<?>> factories) {
        assert (interfaceType != null) : "Interface type was null";
        assert (factories != null) : "Object factories were null";
        this.interfaceType = interfaceType;
        this.factories = factories.toArray(new ObjectFactory[factories.size()]);
    }

    public Object getInstance() throws ObjectCreationException {
        Object array = Array.newInstance(interfaceType, factories.length);
        for (int i = 0; i < factories.length; i++) {
            Array.set(array, i, factories[i].getInstance());
        }
        return array;
    }

}
