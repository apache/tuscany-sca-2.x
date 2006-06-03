package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.core.component.scope.RequestScopeContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class RequestScopeObjectFactory implements ObjectFactory<RequestScopeContext> {

    public RequestScopeContext getInstance() throws ObjectCreationException {
        return new RequestScopeContext();
    }
}
