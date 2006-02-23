package org.apache.tuscany.core.system.loader;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.core.system.scdl.SystemImplementation;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;

/**
 * Populates the assembly model from an SCDL model
 */
public class SystemSCDLModelLoader implements SCDLModelLoader {
    
    private AssemblyModelContext modelContext;
    private SystemAssemblyFactory systemFactory;
    private ResourceLoader resourceLoader;

    /**
     * Constructs a new JavaSCDLModelLoader.
     */
    public SystemSCDLModelLoader(AssemblyModelContext modelContext) {
        this.modelContext=modelContext;
        this.resourceLoader=this.modelContext.getResourceLoader();
        this.systemFactory=new SystemAssemblyFactoryImpl();
    }

    /**
     * @see org.apache.tuscany.model.scdl.loader.SCDLModelLoader#load(java.lang.Object)
     */
    public AssemblyModelObject load(Object object) {
        if (object instanceof SystemImplementation) {
            SystemImplementation scdlImplementation=(SystemImplementation)object;
            org.apache.tuscany.core.system.assembly.SystemImplementation implementation=systemFactory.createSystemImplementation();
            Class implementationClass;
            try {
                implementationClass=resourceLoader.loadClass(scdlImplementation.getClass_());
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
            implementation.setImplementationClass(implementationClass);
            return implementation;
        } else
            return null;
    }
}
