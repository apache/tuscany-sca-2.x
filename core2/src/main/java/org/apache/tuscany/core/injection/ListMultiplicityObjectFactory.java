package org.apache.tuscany.core.injection;

import java.util.List;
import java.util.ArrayList;

import org.apache.tuscany.common.ObjectCreationException;
import org.apache.tuscany.common.ObjectFactory;

/**
 * Resolves targets configured in a multiplicity by delegating to object factories and returning an
 * <code>List</code> containing object instances
 *
 * @version $Rev$ $Date$
 */
public class ListMultiplicityObjectFactory implements ObjectFactory<List> {

    private ObjectFactory[] factories;

    public ListMultiplicityObjectFactory(List<ObjectFactory<?>> factories) {
        assert (factories != null) : "Object factories were null";
        this.factories = factories.toArray(new ObjectFactory[factories.size()]);
    }

    public List getInstance() throws ObjectCreationException {
        List<Object> list = new ArrayList<Object>();
        for (ObjectFactory factory : factories) {
            list.add(factory.getInstance());
        }
        return list;
    }

}
