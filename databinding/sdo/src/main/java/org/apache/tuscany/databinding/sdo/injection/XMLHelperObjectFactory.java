package org.apache.tuscany.databinding.sdo.injection;

import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.databinding.sdo.system.SDOService;

import commonj.sdo.helper.XMLHelper;

/**
 * @version $$Rev$$ $$Date$$
 */
public class XMLHelperObjectFactory implements ObjectFactory<XMLHelper> {


    private ContextResolver resolver;

    /**
     * @throws org.apache.tuscany.core.injection.FactoryInitException
     *
     */
    public XMLHelperObjectFactory(ContextResolver resolver) {
        this.resolver = resolver;
    }


    public XMLHelper getInstance() throws ObjectCreationException {
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
        return sdoService.getXMLHelper();
    }

    public void setContextResolver(ContextResolver resolver) {
        this.resolver = resolver;
    }


}
