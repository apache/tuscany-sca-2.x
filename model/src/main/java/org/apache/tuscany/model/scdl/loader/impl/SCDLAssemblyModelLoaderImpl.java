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
package org.apache.tuscany.model.scdl.loader.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.assembly.Subsystem;
import org.apache.tuscany.model.scdl.loader.SCDLAssemblyModelLoader;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.model.util.ModelTransformer;
import org.apache.tuscany.model.util.ModelTransformerImpl;
import org.apache.tuscany.sdo.util.DataObjectUtil;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 */
public class SCDLAssemblyModelLoaderImpl implements SCDLAssemblyModelLoader {
    
    private SCDLXMLReader xmlReader=new SCDLXMLReader();
    private AssemblyModelContext modelContext;
    
    private List<SCDLModelLoader> scdlModelLoaders=new ArrayList<SCDLModelLoader>();
    
    static {
        DataObjectUtil.initRuntime();
    }

    /**
     * Constructor
     */
    public SCDLAssemblyModelLoaderImpl() {
    }
    
    /**
     * @param modelContext The modelContext to set.
     */
    public void setModelContext(AssemblyModelContext modelContext) {
        this.modelContext = modelContext;
    }

    /**
     * @see org.apache.tuscany.model.assembly.loader.AssemblyModelLoader#getComponentType(java.lang.String)
     */
    public ComponentType getComponentType(String uri) {

        // Load the SCDL component type
        org.apache.tuscany.model.scdl.ComponentType scdlComponentType=xmlReader.getComponentType(uri);
        
        // Transform it to an assembly component type
        return transform(scdlComponentType).getComponentType();
    }

    /**
     * @see org.apache.tuscany.model.assembly.loader.AssemblyModelLoader#getModule(java.lang.String)
     */
    public Module getModule(String uri) {

        // Load the SCDL module
        org.apache.tuscany.model.scdl.Module scdlModule=xmlReader.getModule(uri);
        
        // Transform it to an assembly module
        return transform(scdlModule).getModule();
    }

    /**
     * @see org.apache.tuscany.model.assembly.loader.AssemblyModelLoader#getModuleFragment(java.lang.String)
     */
    public ModuleFragment getModuleFragment(String uri) {

        // Load the SCDL module fragment
        org.apache.tuscany.model.scdl.ModuleFragment scdlFragment=xmlReader.getModuleFragment(uri);
        
        // Transform it to an assembly module fragment
        return transform(scdlFragment).getModuleFragment();
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.loader.AssemblyModelLoader#getSubsystem(java.lang.String)
     */
    public Subsystem getSubsystem(String uri) {

        // Load the SCDL subsystem
        org.apache.tuscany.model.scdl.Subsystem scdlSubsystem=xmlReader.getSubsystem(uri);
        
        return transform(scdlSubsystem).getSubsystem();
    }
    
    /**
     * @see org.apache.tuscany.model.scdl.loader.SCDLAssemblyModelLoader#getSCDLModelLoaders()
     */
    public List<SCDLModelLoader> getSCDLModelLoaders() {
        return scdlModelLoaders;
    }
    
    /**
     * Transform a model and return the handler containing the result of the transformation.
     * @param object
     * @return
     */
    private SCDLContentHandlerImpl transform(Object object) {
        //FIXME Remove this dependency on EMF
        Iterator contents=EcoreUtil.getAllContents(Collections.singleton(object), true);
        
        ModelTransformer transformer=new ModelTransformerImpl();
        SCDLContentHandlerImpl handler=new SCDLContentHandlerImpl(modelContext,scdlModelLoaders);
        transformer.transform(contents, handler);
        return handler;
    }
    
}
