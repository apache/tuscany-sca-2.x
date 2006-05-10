package org.apache.tuscany.core.sdo;

import commonj.sdo.helper.XSDHelper;
import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.sdo.util.SDOUtil;

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
        AssemblyContext assemblyContext = ctx.resolveInstance(AssemblyContext.class);
        return SDOUtil.createXSDHelper(assemblyContext.getTypeHelper());
    }


}
