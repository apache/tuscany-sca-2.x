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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.assembly.Subsystem;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.model.util.ModelTransformer;
import org.apache.tuscany.model.util.ModelTransformerImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 */
public class SCDLAssemblyModelLoaderImpl implements AssemblyModelLoader {
    
    private SCDLXMLReader xmlReader=new SCDLXMLReader();
    private WSDLReader wsdlReader;
    private AssemblyModelContext modelContext;
    
    private List<SCDLModelLoader> scdlModelLoaders;
    
    private Map<String, ComponentType> componentTypes=new HashMap<String, ComponentType>();
    private Map<String, Module> modules=new HashMap<String, Module>();
    private Map<String, ModuleFragment> moduleFragments=new HashMap<String, ModuleFragment>();
    private Map<String, Subsystem> subsystems=new HashMap<String, Subsystem>();
    private Map<String, Definition> definitions=new HashMap<String, Definition>();
    private Map<String, List<Definition>> definitionsByNamespace=new HashMap<String, List<Definition>>();
    
    /**
     * Constructor
     */
    public SCDLAssemblyModelLoaderImpl(List<SCDLModelLoader> loaders) {
        scdlModelLoaders=loaders!=null? loaders:new ArrayList<SCDLModelLoader>(); 
    }
    
    /**
     * @param modelContext The modelContext to set.
     */
    public void setModelContext(AssemblyModelContext modelContext) {
        this.modelContext = modelContext;
    }

    /**
     * @see org.apache.tuscany.model.assembly.loader.AssemblyModelLoader#loadComponentType(java.lang.String)
     */
    public ComponentType loadComponentType(String uri) {
        ComponentType componentType=componentTypes.get(uri);
        if (componentType!=null)
            return componentType;

        // Load the SCDL component type
        org.apache.tuscany.model.scdl.ComponentType scdlComponentType=xmlReader.getComponentType(uri);

        // Transform it to an assembly component type
        componentType=transform(scdlComponentType).getComponentType();

        componentTypes.put(uri, componentType);
        return componentType;
    }

    /**
     * @see org.apache.tuscany.model.assembly.loader.AssemblyModelLoader#loadDefinition(java.lang.String)
     */
    public Definition loadDefinition(String uri) {
        Definition definition=definitions.get(uri);
        if (definition!=null)
            return definition;

        try {
            if (wsdlReader==null)
                wsdlReader=WSDLFactory.newInstance().newWSDLReader();
            definition = wsdlReader.readWSDL(uri);
        } catch (WSDLException e) {
            throw new IllegalArgumentException(e);
        }
        if (definition==null)
            throw new IllegalArgumentException("Could not load WSDL definition at "+uri);

        definitions.put(uri, definition);

        String namespace=definition.getTargetNamespace();
        List<Definition> list=definitionsByNamespace.get(namespace);
        if (list==null) {
            list=new ArrayList<Definition>();
            definitionsByNamespace.put(namespace, list);
        }
        list.add(definition);

        return definition;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.loader.AssemblyModelLoader#loadDefinitions(java.lang.String)
     */
    public List<Definition> loadDefinitions(String namespace) {
        return definitionsByNamespace.get(namespace);
    }

    /**
     * Transform a model and return the handler containing the result of the transformation.
     * @param object
     * @return a transformed model
     */
    @SuppressWarnings("unchecked")
    private SCDLModelContentHandlerImpl transform(Object object) {
        //FIXME Remove this dependency on EMF
        Iterator<Object> contents=EcoreUtil.getAllContents(Collections.singleton(object), true);
        
        ModelTransformer transformer=new ModelTransformerImpl();
        SCDLModelContentHandlerImpl handler=new SCDLModelContentHandlerImpl(modelContext, scdlModelLoaders);
        transformer.transform(contents, handler);
        return handler;
    }
    
}
