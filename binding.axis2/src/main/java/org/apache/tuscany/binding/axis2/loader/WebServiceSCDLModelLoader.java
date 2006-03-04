package org.apache.tuscany.binding.axis2.loader;

import org.apache.tuscany.binding.axis2.assembly.WebServiceAssemblyFactory;
import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis2.assembly.impl.WebServiceAssemblyFactoryImpl;
import org.apache.tuscany.binding.axis2.assembly.impl.WebServiceBindingImpl;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.loader.SCDLModelLoaderRegistry;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Destroy;

/**
 * Populates the assembly model from an SCDL model
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class WebServiceSCDLModelLoader implements SCDLModelLoader {

    private RuntimeContext runtimeContext;
    private SCDLModelLoaderRegistry loaderRegistry;
    private WebServiceAssemblyFactory wsFactory;

    /**
     * Constructs a new WebServiceSCDLModelLoader.
     */
    public WebServiceSCDLModelLoader() {
        this.wsFactory=new WebServiceAssemblyFactoryImpl();
    }

    /**
     * @param runtimeContext The runtimeContext to set.
     */
    @Autowire
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    // @Autowire
    public void setLoaderRegistry(SCDLModelLoaderRegistry registry) {
        this.loaderRegistry = registry;
    }

    @Init(eager=true)
    public void init() {
        runtimeContext.addLoader(this);
//        loaderRegistry.registerLoader(this);
    }

    @Destroy
    public void destroy() {
        loaderRegistry.unregisterLoader(this);
    }

    /**
     * @see org.apache.tuscany.model.scdl.loader.SCDLModelLoader#load(org.apache.tuscany.model.assembly.AssemblyModelContext, java.lang.Object)
     */
    public AssemblyModelObject load(AssemblyModelContext modelContext, Object object) {
        if (object instanceof org.apache.tuscany.model.scdl.WebServiceBinding) {
            org.apache.tuscany.model.scdl.WebServiceBinding scdlBinding=(org.apache.tuscany.model.scdl.WebServiceBinding)object;
            WebServiceBinding binding=wsFactory.createWebServiceBinding();
            binding.setURI(scdlBinding.getUri());

            // Set the port URI into the assembly binding, it'll be resolved in the initialize method
            ((WebServiceBindingImpl)binding).setPortURI(scdlBinding.getPort());

            return binding;

        } else
            return null;
    }
}
