package org.apache.tuscany.core.injection;

import org.osoa.sca.RequestContext;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.component.WorkContext;

import org.apache.tuscany.core.implementation.composite.ManagedRequestContext;

/**
 * Creates instances of {@link org.apache.tuscany.core.implementation.composite.ManagedRequestContext} for injection on
 * component implementation instances
 *
 * @version $Rev$ $Date$
 */
public class RequestContextObjectFactory implements ObjectFactory<RequestContext> {
    private WorkContext workContext;

    public RequestContextObjectFactory(WorkContext workContext) {
        assert workContext != null;
        this.workContext = workContext;
    }

    public ManagedRequestContext getInstance() throws ObjectCreationException {
        return new ManagedRequestContext(workContext);
    }
}
