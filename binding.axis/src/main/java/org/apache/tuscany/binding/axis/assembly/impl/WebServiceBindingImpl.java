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
package org.apache.tuscany.binding.axis.assembly.impl;

import javax.wsdl.Definition;
import javax.wsdl.Port;

import org.apache.tuscany.binding.axis.assembly.WebServiceBinding;
import org.apache.tuscany.model.assembly.impl.BindingImpl;

/**
 * An implementation of WebServiceBinding.
 */
public class WebServiceBindingImpl extends BindingImpl implements WebServiceBinding {
    
    private Definition definition;
    private Port port;

    /**
     * Constructor
     */
    protected WebServiceBindingImpl() {
    }
    
    /**
     * @see org.apache.tuscany.binding.axis.assembly.WebServiceBinding#getWSDLPort()
     */
    public Port getWSDLPort() {
        return port;
    }
    
    /**
     * @see org.apache.tuscany.binding.axis.assembly.WebServiceBinding#setWSDLPort(javax.wsdl.Port)
     */
    public void setWSDLPort(Port value) {
        checkNotFrozen();
        this.port=value;
    }
    
    /**
     * @see org.apache.tuscany.binding.axis.assembly.WebServiceBinding#getWSDLDefinition()
     */
    public Definition getWSDLDefinition() {
        return definition;
    }
    
    /**
     * @see org.apache.tuscany.binding.axis.assembly.WebServiceBinding#setWSDLDefinition(javax.wsdl.Definition)
     */
    public void setWSDLDefinition(Definition definition) {
        checkNotFrozen();
        this.definition=definition;
    }

}
