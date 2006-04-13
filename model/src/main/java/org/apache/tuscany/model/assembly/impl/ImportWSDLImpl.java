/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.model.assembly.impl;

import java.net.URL;

import javax.wsdl.Definition;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.ImportWSDL;

/**
 * Implementation of ImportWSDL.
 *
 * @version $Rev$ $Date$
 */
public class ImportWSDLImpl extends AssemblyObjectImpl implements ImportWSDL {
    private String location;
    private String namespace;
    private Definition definition;

    protected ImportWSDLImpl() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String uri) {
        checkNotFrozen();
        this.location = uri;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String uri) {
        checkNotFrozen();
        this.namespace = uri;
    }

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        checkNotFrozen();
        this.definition = definition;
    }
    
    public void initialize(AssemblyContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);
        
        // Load the WSDL definition if necessary
        ResourceLoader resourceLoader = modelContext.getApplicationResourceLoader();
        if (definition == null) {
            URL url = resourceLoader.getResource(location);
            if (url == null)
                throw new IllegalArgumentException("Cannot find " + location);
            definition = modelContext.getAssemblyLoader().loadDefinition(url.toString());
        }
    }
    
}
