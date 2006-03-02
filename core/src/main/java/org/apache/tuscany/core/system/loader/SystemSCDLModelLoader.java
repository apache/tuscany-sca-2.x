package org.apache.tuscany.core.system.loader;

import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.core.system.scdl.ScdlFactory;
import org.apache.tuscany.core.system.scdl.SystemImplementation;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.sdo.util.SDOUtil;

/**
 * Populates the assembly model from an SCDL model
 */
public class SystemSCDLModelLoader implements SCDLModelLoader {
    
    private SystemAssemblyFactory systemFactory;
    
    static {
        // Register the system SCDL model
        SDOUtil.registerStaticTypes(ScdlFactory.class);
    }

    /**
     * Constructs a new JavaSCDLModelLoader.
     */
    public SystemSCDLModelLoader() {
        this.systemFactory=new SystemAssemblyFactoryImpl();
    }

    /**
     * @see org.apache.tuscany.model.scdl.loader.SCDLModelLoader#load(org.apache.tuscany.model.assembly.AssemblyModelContext, java.lang.Object)
     */
    public AssemblyModelObject load(AssemblyModelContext modelContext, Object object) {
        if (object instanceof SystemImplementation) {
            SystemImplementation scdlImplementation=(SystemImplementation)object;
            org.apache.tuscany.core.system.assembly.SystemImplementation implementation=systemFactory.createSystemImplementation();
            Class implementationClass;
            try {
                implementationClass=modelContext.getSystemResourceLoader().loadClass(scdlImplementation.getClass_());
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
            implementation.setImplementationClass(implementationClass);
            return implementation;
        } else
            return null;
    }
}
