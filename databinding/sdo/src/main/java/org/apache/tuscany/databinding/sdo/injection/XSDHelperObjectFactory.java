package org.apache.tuscany.databinding.sdo.injection;

import commonj.sdo.helper.XSDHelper;
import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.databinding.sdo.system.SDOService;

/**
 * @version $$Rev$$ $$Date$$
 */
public class XSDHelperObjectFactory implements ObjectFactory<XSDHelper> {


    private ContextResolver resolver;

    /**
     * @throws org.apache.tuscany.core.injection.FactoryInitException
     *
     */
    public XSDHelperObjectFactory(ContextResolver resolver) {
        this.resolver = resolver;
    }


    public XSDHelper getInstance() throws ObjectCreationException {
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
        SDOService assemblyCtx = ctx.resolveInstance(SDOService.class);
        return assemblyCtx.getHelper();
    }

    public void setContextResolver(ContextResolver resolver) {
        this.resolver = resolver;
    }


}
