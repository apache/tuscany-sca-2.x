package org.apache.tuscany.core.mock.context;

import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.QualifiedName;

/**
 * @version $$Rev$$ $$Date$$
 */
public class MockCompositeContext extends AbstractContext implements CompositeContext {

    public Object getInstance(QualifiedName qName) throws TargetException {
        throw new UnsupportedOperationException();
    }

    public void registerContext(Context context) {
        throw new UnsupportedOperationException();
    }

    public Context getContext(String name) {
        throw new UnsupportedOperationException();
    }
}
