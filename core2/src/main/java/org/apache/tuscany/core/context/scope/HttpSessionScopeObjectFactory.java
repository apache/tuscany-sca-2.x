package org.apache.tuscany.core.context.scope;

import org.apache.tuscany.common.ObjectFactory;
import org.apache.tuscany.common.ObjectCreationException;

/**
 * @version $$Rev$$ $$Date$$
 */
public class HttpSessionScopeObjectFactory implements ObjectFactory<HttpSessionScopeContext> {

    public HttpSessionScopeContext getInstance() throws ObjectCreationException {
        return new HttpSessionScopeContext();
    }
}
