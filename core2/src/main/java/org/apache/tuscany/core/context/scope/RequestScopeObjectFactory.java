package org.apache.tuscany.core.context.scope;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.ObjectCreationException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class RequestScopeObjectFactory implements ObjectFactory<RequestScopeContext> {

    public RequestScopeContext getInstance() throws ObjectCreationException {
        return new RequestScopeContext();
    }
}
