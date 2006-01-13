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

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Interface;
import org.apache.tuscany.model.assembly.Service;

/**
 * An implementation of the model object '<em><b>Service</b></em>'.
 */
public class ServiceImpl extends org.apache.tuscany.model.assembly.sdo.impl.ServiceImpl implements Service {
    /**
     * Constructor
     */
    protected ServiceImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ServiceImpl#getName()
     */
    public String getName() {
        return super.getName();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ServiceImpl#setName(java.lang.String)
     */
    public void setName(String newName) {
        super.setName(newName);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Port#getInterfaceContract()
     */
    public Interface getInterfaceContract() {
        return (Interface) getInterface();
    }

    /**
     * @see org.apache.tuscany.model.assembly.Port#setInterfaceContract(org.apache.tuscany.model.assembly.Interface)
     */
    public void setInterfaceContract(Interface value) {
        setInterface((org.osoa.sca.model.Interface) value);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        Interface iface = getInterfaceContract();
        if (iface != null)
            iface.initialize(modelContext);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#freeze()
     */
    public void freeze() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        return AssemblyModelVisitorHelperImpl.accept(this, visitor);
    }

} //ServiceImpl
