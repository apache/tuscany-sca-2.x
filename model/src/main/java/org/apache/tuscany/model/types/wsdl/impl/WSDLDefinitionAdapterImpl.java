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
package org.apache.tuscany.model.types.wsdl.impl;

import javax.wsdl.Definition;

import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 *         <p/>
 *         This is an EMF adapter used to associate WSDL definitions with EMF EPackages.
 */
public class WSDLDefinitionAdapterImpl extends AdapterImpl implements WSDLDefinitionAdapter {

    private Definition definition;

    /**
     * Constructor
     */
    public WSDLDefinitionAdapterImpl(Definition definition) {
        super();
        this.definition = definition;
    }

    /**
     * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
     */
    public boolean isAdapterForType(Object type) {
        return type == WSDLDefinitionAdapter.class;
    }

    /**
     * @see org.apache.tuscany.model.types.wsdl.impl.WSDLDefinitionAdapter#getWSDLDefinition()
     */
    public Definition getWSDLDefinition() {
        return definition;
    }
}