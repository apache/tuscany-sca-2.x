package org.apache.tuscany.databinding.sdo.injection;

import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.databinding.sdo.system.SDOService;

import commonj.sdo.helper.DataFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public class DataFactoryObjectFactory implements ObjectFactory<DataFactory> {


    private ContextResolver resolver;

    /**
     * @throws org.apache.tuscany.core.injection.FactoryInitException
     *
     */
    public DataFactoryObjectFactory(ContextResolver resolver) {
        this.resolver = resolver;
    }


    public DataFactory getInstance() throws ObjectCreationException {
        CompositeContext parent = resolver.getCurrentContext();
        if (parent == null) {
            return null;// FIXME semantic here means required is not followed
        }
        if (!(parent instanceof AutowireContext)) {
            ObjectCreationException e = new ObjectCreationException("Parent does not implement "
                    + AutowireContext.class.getName());
            e.setIdentifier(parent.getName());
            throw e;
        }
        AutowireContext ctx = (AutowireContext) parent;
        SDOService sdoService = ctx.resolveInstance(SDOService.class);
        return sdoService.getDataFactory();
    }

    public void setContextResolver(ContextResolver resolver) {
        this.resolver = resolver;
    }


}
