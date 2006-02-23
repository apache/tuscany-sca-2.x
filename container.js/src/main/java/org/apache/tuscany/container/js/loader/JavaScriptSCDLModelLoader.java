package org.apache.tuscany.container.js.loader;

import org.apache.tuscany.container.js.assembly.JavaScriptAssemblyFactory;
import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.js.assembly.impl.JavaScriptAssemblyFactoryImpl;
import org.apache.tuscany.container.js.scdl.ScdlFactory;
import org.apache.tuscany.container.js.scdl.impl.ScdlPackageImpl;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.sdo.util.SDOUtil;

/**
 * Populates the assembly model from an SCDL model
 */
public class JavaScriptSCDLModelLoader implements SCDLModelLoader {
    
    private AssemblyModelContext modelContext;
    private JavaScriptAssemblyFactory jsFactory;

    static {
        // Register the JavaScript SCDL model
        ScdlPackageImpl.eINSTANCE.eClass();
        SDOUtil.registerStaticTypes(ScdlFactory.class);
    }
    
    /**
     * Constructs a new JavaSCDLModelLoader.
     */
    public JavaScriptSCDLModelLoader(AssemblyModelContext modelContext) {
        this.modelContext=modelContext;
        this.jsFactory=new JavaScriptAssemblyFactoryImpl();
    }

    /**
     * @see org.apache.tuscany.model.scdl.loader.SCDLModelLoader#load(java.lang.Object)
     */
    public AssemblyModelObject load(Object object) {
        if (object instanceof org.apache.tuscany.container.js.scdl.JavaScriptImplementation) {
            org.apache.tuscany.container.js.scdl.JavaScriptImplementation scdlImplementation=(org.apache.tuscany.container.js.scdl.JavaScriptImplementation)object;
            JavaScriptImplementation implementation=jsFactory.createJavaScriptImplementation();
            implementation.setScriptFile(scdlImplementation.getScriptFile());
            return implementation;
        } else
            return null;
    }
}
