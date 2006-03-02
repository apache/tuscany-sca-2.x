package org.apache.tuscany.container.java.loader;

import org.apache.tuscany.container.java.assembly.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.assembly.impl.JavaAssemblyFactoryImpl;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.loader.SCDLModelLoaderRegistry;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Reference;

/**
 * Populates the assembly model from an SCDL model
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class JavaSCDLModelLoader implements SCDLModelLoader {

    private RuntimeContext runtimeContext;
    private SCDLModelLoaderRegistry loaderRegistry;
    private JavaAssemblyFactory javaFactory;

    /**
     * Constructs a new JavaSCDLModelLoader.
     */
    public JavaSCDLModelLoader() {
        this.javaFactory=new JavaAssemblyFactoryImpl();
    }

    /**
     * @param runtimeContext The runtimeContext to set.
     */
    @Autowire
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

//    @Reference
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
//        loaderRegistry.unregisterLoader(this);
    }

    /**
     * @see org.apache.tuscany.model.scdl.loader.SCDLModelLoader#load(org.apache.tuscany.model.assembly.AssemblyModelContext, java.lang.Object)
     */
    public AssemblyModelObject load(AssemblyModelContext modelContext, Object object) {
        if (object instanceof org.apache.tuscany.model.scdl.JavaImplementation) {
            org.apache.tuscany.model.scdl.JavaImplementation scdlJavaImplementation=(org.apache.tuscany.model.scdl.JavaImplementation)object;
            JavaImplementation implementation=javaFactory.createJavaImplementation();
            Class implementationClass;
            try {
                implementationClass=modelContext.getApplicationResourceLoader().loadClass(scdlJavaImplementation.getClass_());
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
            implementation.setImplementationClass(implementationClass);

            return implementation;
        } else
            return null;
    }
}
