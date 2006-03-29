/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.js.loader;

import org.apache.tuscany.container.js.assembly.JavaScriptAssemblyFactory;
import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.js.assembly.impl.JavaScriptAssemblyFactoryImpl;
import org.apache.tuscany.container.js.scdl.ScdlFactory;
import org.apache.tuscany.container.js.scdl.impl.ScdlPackageImpl;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.loader.SCDLModelLoaderRegistry;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Reference;

/**
 * Populates the assembly model from an SCDL model
 */
@org.osoa.sca.annotations.Scope("MODULE")
public class JavaScriptSCDLModelLoader implements SCDLModelLoader {

    private RuntimeContext runtimeContext;
    private SCDLModelLoaderRegistry loaderRegistry;
    private JavaScriptAssemblyFactory jsFactory;

    static {
        // Register the JavaScript SCDL model
        SDOUtil.registerStaticTypes(ScdlFactory.class);
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
     * Constructs a new JavaSCDLModelLoader.
     */
    public JavaScriptSCDLModelLoader() {
        this.jsFactory=new JavaScriptAssemblyFactoryImpl();
    }

    /**
     * @see org.apache.tuscany.model.scdl.loader.SCDLModelLoader#load(org.apache.tuscany.model.assembly.AssemblyModelContext, java.lang.Object)
     */
    public AssemblyModelObject load(AssemblyModelContext modelContext, Object object) {
        if (object instanceof org.apache.tuscany.container.js.scdl.JavaScriptImplementation) {
            org.apache.tuscany.container.js.scdl.JavaScriptImplementation scdlImplementation=(org.apache.tuscany.container.js.scdl.JavaScriptImplementation)object;
            JavaScriptImplementation implementation=jsFactory.createJavaScriptImplementation();
            implementation.setScriptFile(scdlImplementation.getScriptFile());
            implementation.setStyle(scdlImplementation.getStyle().getLiteral());
            return implementation;
        } else
            return null;
    }
}
