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
package org.apache.tuscany.model.assembly.impl;

import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.ServiceContract;

/**
 * An implementation of ServiceContract.
 */
public class ServiceContractImpl extends ExtensibleImpl implements ServiceContract {
    
    private Class interface_;
    private Class callbackInterface;
    private Scope scope;
    
    /**
     * Constructor
     */
    protected ServiceContractImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.ServiceContract#getCallbackInterface()
     */
    public Class getCallbackInterface() {
        return callbackInterface;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ServiceContract#getInterface()
     */
    public Class getInterface() {
        return interface_;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ServiceContract#getScope()
     */
    public Scope getScope() {
        return scope;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ServiceContract#setCallbackInterface(java.lang.Class)
     */
    public void setCallbackInterface(Class value) {
        checkNotFrozen();
        callbackInterface=value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ServiceContract#setInterface(java.lang.Class)
     */
    public void setInterface(Class value) {
        checkNotFrozen();
        interface_=value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ServiceContract#setScope(org.apache.tuscany.model.assembly.Scope)
     */
    public void setScope(Scope scope) {
        checkNotFrozen();
        this.scope=scope;
    }
}
