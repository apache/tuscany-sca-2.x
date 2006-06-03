package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.core.component.scope.HttpSessionScopeContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class HttpSessionScopeObjectFactory implements ObjectFactory<HttpSessionScopeContext> {

    public HttpSessionScopeContext getInstance() throws ObjectCreationException {
        return new HttpSessionScopeContext();
    }
}
