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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.model.scdl.ComponentType;
import org.apache.tuscany.model.scdl.Module;
import org.apache.tuscany.model.scdl.ModuleFragment;
import org.apache.tuscany.model.scdl.ScdlFactory;
import org.apache.tuscany.model.scdl.Subsystem;
import org.apache.tuscany.model.scdl.impl.ScdlPackageImpl;
import org.apache.tuscany.sdo.util.DataObjectUtil;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

/**
 */
public class SCDLXMLReader extends ResourceSetImpl {
    
    private Map<String, Object> cache=new HashMap<String, Object>();

    // Initialize the SDO runtime and register the SCDL model
    static {
        DataObjectUtil.initRuntime();
        SDOUtil.registerStaticTypes(ScdlFactory.class);
    }

    /**
     * Constructor
     */
    public SCDLXMLReader() {
        
        // Initialize SCDL package
        ScdlPackageImpl.eINSTANCE.eClass();
    }

    /**
     * Returns an SCDL module.
     * @param uri
     * @return
     */
    public Module getModule(String uri) {
        return (Module)getRootObject(uri);
    }

    /**
     * Returns an SCDL module fragment.
     * @param uri
     * @return
     */
    public ModuleFragment getModuleFragment(String uri) {
        return (ModuleFragment)getRootObject(uri);
    }

    /**
     * Returns an SCDL component type.
     * @param uri
     * @return
     */
    public ComponentType getComponentType(String uri) {
        return (ComponentType)getRootObject(uri);
    }
    
    /**
     * Returns an SCDL subsystem.
     * @param uri
     * @return
     */
    public Subsystem getSubsystem(String uri) {
        return (Subsystem)getRootObject(uri);
    }

    /**
     * Returns the root object at the given URI.
     * @param uri
     * @return
     */
    private Object getRootObject(String uri) {
        Object object = cache.get(uri);
        if (object==null) {
            try {
                XMLDocument document=XMLHelper.INSTANCE.load(new URL(uri).openStream());
                return document.getRootObject();
           } catch (IOException e) {
               throw new RuntimeException(uri, e);
           }
        }
        return object;
    }
    
}
