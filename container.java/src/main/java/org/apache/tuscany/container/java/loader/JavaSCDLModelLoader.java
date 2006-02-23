package org.apache.tuscany.container.java.loader;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.container.java.assembly.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.assembly.impl.JavaAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;

/**
 * Populates the assembly model from an SCDL model
 */
public class JavaSCDLModelLoader implements SCDLModelLoader {
    
    private AssemblyModelContext modelContext;
    private ResourceLoader resourceLoader;
    private JavaAssemblyFactory javaFactory;

    /**
     * Constructs a new JavaSCDLModelLoader.
     */
    public JavaSCDLModelLoader(AssemblyModelContext modelContext) {
        this.modelContext=modelContext;
        this.resourceLoader=this.modelContext.getResourceLoader();
        this.javaFactory=new JavaAssemblyFactoryImpl();
    }

    /**
     * @see org.apache.tuscany.model.scdl.loader.SCDLModelLoader#load(java.lang.Object)
     */
    public AssemblyModelObject load(Object object) {
        if (object instanceof org.apache.tuscany.model.scdl.JavaImplementation) {
            org.apache.tuscany.model.scdl.JavaImplementation scdlJavaImplementation=(org.apache.tuscany.model.scdl.JavaImplementation)object;
            JavaImplementation implementation=javaFactory.createJavaImplementation();
            Class implementationClass;
            try {
                implementationClass=resourceLoader.loadClass(scdlJavaImplementation.getClass_());
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
            implementation.setImplementationClass(implementationClass);
            
            return implementation;
        } else
            return null;
    }
}
