package org.apache.tuscany.core.context.scope;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.ObjectCreationException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class HttpSessionScopeObjectFactory implements ObjectFactory<HttpSessionScopeContext> {

    public HttpSessionScopeContext getInstance() throws ObjectCreationException {
        return new HttpSessionScopeContext();
    }
}
