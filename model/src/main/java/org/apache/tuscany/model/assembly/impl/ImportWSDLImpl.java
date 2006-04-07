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

import javax.wsdl.Definition;

import org.apache.tuscany.model.assembly.ImportWSDL;

/**
 * Implementation of ImportWSDL.
 *
 * @version $Rev$ $Date$
 */
public class ImportWSDLImpl extends AssemblyModelObjectImpl implements ImportWSDL {
    private String location;
    private String namespace;
    private Definition definition;

    /**
     * Hide default constructor.
     */
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
        this.definition = definition;
    }
}
