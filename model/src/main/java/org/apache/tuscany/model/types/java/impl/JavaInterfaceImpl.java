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
package org.apache.tuscany.model.types.java.impl;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.model.assembly.impl.AssemblyModelVisitorHelperImpl;
import org.apache.tuscany.model.types.InterfaceType;
import org.apache.tuscany.model.types.java.JavaInterface;

/**
 * An implementation of the model object '<em><b>Java Interface</b></em>'.
 */
public class JavaInterfaceImpl extends org.apache.tuscany.model.assembly.sdo.impl.JavaInterfaceImpl implements JavaInterface {
    private InterfaceType interfaceType;
    private InterfaceType callbackInterfaceType;
    private ScopeEnum scope;

    /**
     * Constructor
     */
    public JavaInterfaceImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.JavaInterfaceImpl#getInterface()
     */
    public String getInterface() {
        return super.getInterface();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.JavaInterfaceImpl#setInterface(java.lang.String)
     */
    public void setInterface(String newInterface) {
        super.setInterface(newInterface);
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.JavaInterfaceImpl#getCallbackInterface()
     */
    public String getCallbackInterface() {
        return super.getCallbackInterface();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.JavaInterfaceImpl#setCallbackInterface(java.lang.String)
     */
    public void setCallbackInterface(String newCallbackInterface) {
        super.setCallbackInterface(newCallbackInterface);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Interface#getInterfaceType()
     */
    public InterfaceType getInterfaceType() {
        return interfaceType;
    }

    public void setTInterfaceType(InterfaceType interfaceType) {
        this.interfaceType = interfaceType;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Interface#getCallbackInterfaceType()
     */
    public InterfaceType getCallbackInterfaceType() {
        return callbackInterfaceType;
    }

    public void setCallbackTInterfaceType(InterfaceType interfaceType) {
        this.callbackInterfaceType = interfaceType;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        // Resolve interface type and callback interface type
        if (interfaceType == null) {
            String interfaceName = getInterface();
            if (interfaceName != null) {
                interfaceType = modelContext.getJavaTypeHelper().getJavaInterfaceType(interfaceName);
            }
        }
        if (callbackInterfaceType == null) {
            String callbackInterfaceName = getCallbackInterface();
            if (callbackInterfaceName != null) {
                callbackInterfaceType = modelContext.getJavaTypeHelper().getJavaInterfaceType(callbackInterfaceName);
            }
        }

        if (scope == null)
            scope = ScopeEnum.MODULE_LITERAL;
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

    /**
     * @see org.apache.tuscany.model.assembly.Interface#getScope()
     */
    public ScopeEnum getScope() {
        return scope;
    }

    public void setScope(ScopeEnum scope) {
        this.scope = scope;
    }

} //JavaInterfaceImpl
