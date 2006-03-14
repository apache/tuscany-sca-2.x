package org.apache.tuscany.binding.jsonrpc.loader;

import org.apache.tuscany.binding.jsonrpc.assembly.JSONRPCAssemblyFactory;
import org.apache.tuscany.binding.jsonrpc.assembly.JSONRPCBinding;
import org.apache.tuscany.binding.jsonrpc.assembly.impl.JSONRPCAssemblyFactoryImpl;
import org.apache.tuscany.binding.jsonrpc.scdl.ScdlFactory;
import org.apache.tuscany.core.loader.SCDLModelLoaderRegistry;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

/**
 * Populates the assembly model from an SCDL model
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class JSONRPCSCDLModelLoader implements SCDLModelLoader {

    private RuntimeContext runtimeContext;

    private SCDLModelLoaderRegistry loaderRegistry;

    private JSONRPCAssemblyFactory jsonrpcFactory;

    static {
        // Register the SCDL model
        SDOUtil.registerStaticTypes(ScdlFactory.class);
    }

    /**
     * Constructs a new WebServiceSCDLModelLoader.
     */
    public JSONRPCSCDLModelLoader() {
        this.jsonrpcFactory = new JSONRPCAssemblyFactoryImpl();
    }

    /**
     * @param runtimeContext
     *            The runtimeContext to set.
     */
    @Autowire
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    // @Autowire
    public void setLoaderRegistry(SCDLModelLoaderRegistry registry) {
        this.loaderRegistry = registry;
    }

    @Init(eager = true)
    public void init() {
        runtimeContext.addLoader(this);
        // loaderRegistry.registerLoader(this);
    }

    @Destroy
    public void destroy() {
        loaderRegistry.unregisterLoader(this);
    }

    /**
     * @see org.apache.tuscany.model.scdl.loader.SCDLModelLoader#load(org.apache.tuscany.model.assembly.AssemblyModelContext, java.lang.Object)
     */
    public AssemblyModelObject load(AssemblyModelContext modelContext, Object object) {
        if (object instanceof org.apache.tuscany.binding.jsonrpc.scdl.JSONRPCBinding) {
            JSONRPCBinding binding = jsonrpcFactory.createJSONRPCBinding();
            return binding;

        } else
            return null;
    }
}
