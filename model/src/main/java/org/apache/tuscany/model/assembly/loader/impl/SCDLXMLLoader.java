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
package org.apache.tuscany.model.assembly.loader.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.scdl.ComponentType;
import org.apache.tuscany.model.assembly.scdl.DocumentRoot;
import org.apache.tuscany.model.assembly.scdl.Module;
import org.apache.tuscany.model.assembly.scdl.ModuleFragment;
import org.apache.tuscany.model.assembly.scdl.Subsystem;
import org.apache.tuscany.model.assembly.scdl.impl.SCDLPackageImpl;
import org.apache.tuscany.model.assembly.scdl.util.SCDLResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;

/**
 */
public class SCDLXMLLoader extends ResourceSetImpl {

    /**
     * Constructor
     */
    public SCDLXMLLoader() {
        
        // Register resource factories
        Map extensionToFactoryMap=getResourceFactoryRegistry().getExtensionToFactoryMap();
        SCDLResourceFactoryImpl resourceFactory=new SCDLResourceFactoryImpl(); 
        extensionToFactoryMap.put("module", resourceFactory);
        extensionToFactoryMap.put("fragment", resourceFactory);
        extensionToFactoryMap.put("subsystem", resourceFactory);
        extensionToFactoryMap.put("componentType", resourceFactory);
        
        // Initialize SCDL package
        SCDLPackageImpl.eINSTANCE.eClass();
        
    }

    /**
     * Returns an SCDL document root.
     * @param uri
     * @return
     */
    private DocumentRoot getDocumentRoot(String uri) {
        Resource resource = (Resource) getResource(URI.createURI(uri), true);
        List contents=resource.getContents();
        if (!contents.isEmpty()) {
            return (DocumentRoot)contents.get(0);
        } else
            return null;
    }

    /**
     * Returns an SCDL module.
     * @param uri
     * @return
     */
    public Module getModule(String uri) {
        DocumentRoot documentRoot=getDocumentRoot(uri);
        return documentRoot!=null? documentRoot.getModule():null;
    }

    /**
     * Returns an SCDL module fragment.
     * @param uri
     * @return
     */
    public ModuleFragment getModuleFragment(String uri) {
        DocumentRoot documentRoot=getDocumentRoot(uri);
        return documentRoot!=null? documentRoot.getModuleFragment():null;
    }

    /**
     * Returns an SCDL component type.
     * @param uri
     * @return
     */
    public ComponentType getComponentType(String uri) {
        DocumentRoot documentRoot=getDocumentRoot(uri);
        return documentRoot!=null? documentRoot.getComponentType():null;
    }
    
    /**
     * Returns an SCDL subsystem.
     * @param uri
     * @return
     */
    public Subsystem getSubsystem(String uri) {
        DocumentRoot documentRoot=getDocumentRoot(uri);
        return documentRoot!=null? documentRoot.getSubsystem():null;
    }
    
    /**
     * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#getLoadOptions()
     */
    public Map getLoadOptions() {
        if (loadOptions == null) {
            loadOptions = new HashMap();
            XMLResource.XMLMap xmlMap = new XMLMapImpl();
            loadOptions.put(XMLResource.OPTION_XML_MAP, xmlMap);
            loadOptions.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
            loadOptions.put(XMLResource.OPTION_ENCODING, "UTF-8");
            loadOptions.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
            loadOptions.put(XMLResource.OPTION_USE_LEXICAL_HANDLER, Boolean.TRUE);
        }
        return loadOptions;
    }

}
