package org.apache.tuscany.core.sdo;

import commonj.sdo.helper.TypeHelper;
import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.builder.ObjectFactory;
import org.apache.tuscany.core.context.AutowireContext;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.injection.ObjectCreationException;
import org.apache.tuscany.model.assembly.AssemblyContext;

/**
 * @version $$Rev$$ $$Date$$
 */
public class TypeHelperObjectFactory implements ObjectFactory<TypeHelper> {


    private ContextResolver resolver;

    /**
     * @throws org.apache.tuscany.core.injection.FactoryInitException
     *
     */
    public TypeHelperObjectFactory(ContextResolver resolver) {
        this.resolver = resolver;
    }


    public TypeHelper getInstance() throws ObjectCreationException {
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
        return assemblyContext.getTypeHelper();
    }


}
