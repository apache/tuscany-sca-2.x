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
import org.apache.tuscany.model.assembly.Reference;

/**
 * An implementation of the model object '<em><b>Reference</b></em>'.
 */
public class ReferenceImpl extends org.apache.tuscany.model.assembly.sdo.impl.ReferenceImpl implements Reference {
    /**
     * Constructor
     */
    protected ReferenceImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ReferenceImpl#getName()
     */
    public String getName() {
        return super.getName();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ReferenceImpl#setName(java.lang.String)
     */
    public void setName(String newName) {
        super.setName(newName);
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ReferenceImpl#getMultiplicity()
     */
    public String getMultiplicity() {
        return super.getMultiplicity();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ReferenceImpl#setMultiplicity(java.lang.String)
     */
    public void setMultiplicity(String newMultiplicity) {
        super.setMultiplicity(newMultiplicity);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Reference#isMultiplicityN()
     */
    public boolean isMultiplicityN() {
        String multiplicity = super.getMultiplicity();
        return "0..n".equals(multiplicity) || "1..n".equals(multiplicity);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Port#getInterfaceContract()
     */
    public Interface getInterfaceContract() {
        return (Interface) super.getInterface();
    }

    /**
     * @see org.apache.tuscany.model.assembly.Port#setInterfaceContract(org.apache.tuscany.model.assembly.Interface)
     */
    public void setInterfaceContract(Interface value) {
        super.setInterface((org.osoa.sca.model.Interface) value);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        // Initialize the interface
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

} //ReferenceImpl
