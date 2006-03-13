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
package org.apache.tuscany.container.js.assembly.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.ModelInitException;
import org.apache.tuscany.model.assembly.impl.ComponentImplementationImpl;

/**
 * Default implementation of a JavScript component implementation type
 * 
 * @version $Rev$ $Date$
 */
public class JavaScriptImplementationImpl extends ComponentImplementationImpl implements JavaScriptImplementation {

    private String scriptFile;

    private String scriptCode;

    private ResourceLoader resourceLoader;

    public JavaScriptImplementationImpl() {
        super();
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void initialize(AssemblyModelContext modelContext) throws ModelInitException {
        if (isInitialized()) {
            return;
        }
        this.resourceLoader = modelContext.getApplicationResourceLoader();
        if(resourceLoader == null){
            throw new ModelInitException("No resource loader set on model context");
        }

        // Initialize the component type
        ComponentType componentType = getComponentType();
        if (componentType == null) {
            try {
                componentType = createComponentType(modelContext);
            } catch (IOException e) {
                throw new ModelInitException("Error retrieving component type file",e);
            }
            setComponentType(componentType);
        }

        super.initialize(modelContext);

    }

    public String getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(String fn) {
        scriptFile = fn;
    }

    public String getScript() throws ModelInitException {
        if (scriptCode != null) {
            return scriptCode;
        }
        try {
            URL url = resourceLoader.getResource(getScriptFile());
            if (url == null) {
                ModelInitException ce = new ModelInitException("Script not found");
                ce.setIdentifier(getScriptFile());
                throw ce;
            }
            InputStream inputStream = url.openStream();
            try {
                StringBuffer sb = new StringBuffer();
                int n = 0;
                while ((n = inputStream.read()) != -1) {
                    sb.append((char) n);
                }
                scriptCode = sb.toString();
                return scriptCode;
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            ModelInitException ce = new ModelInitException("Error reading script file",e);
            ce.setIdentifier(getScriptFile());
            throw ce;
        }
    }

    /**
     * Create the component type
     * 
     * @param modelContext
     * @param implementationClass
     */
    private ComponentType createComponentType(AssemblyModelContext modelContext) throws IOException{
        String prefix = scriptFile.substring(0,scriptFile.lastIndexOf('.'));
        URL componentTypeFile = resourceLoader.getResource(prefix + ".componentType");
        if (componentTypeFile != null) {
            return modelContext.getAssemblyLoader().loadComponentType(componentTypeFile.toString());
        } else {
            // TODO we could introspect the JavaScript source
            return modelContext.getAssemblyFactory().createComponentType();
        }
    }
    
    
}
