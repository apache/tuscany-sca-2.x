/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.model.assembly.pojo;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Interface;
import org.apache.tuscany.model.assembly.ScopeEnum;
import org.apache.tuscany.model.types.InterfaceType;

import commonj.sdo.Sequence;

public abstract class PojoInterface implements Interface {

    private boolean frozen;
    
    //----------------------------------
    // Constructors
    //----------------------------------

    public PojoInterface() {
    }

    //----------------------------------
    // Methods
    //----------------------------------

    ScopeEnum scope;

    public ScopeEnum getScope() {
        return scope;
    }

    public void setScope(ScopeEnum scope) {
        check();
        this.scope = scope;
    }

    String callbackInterface;
    
    public String getCallbackInterface() {
        return callbackInterface;
    }

    public void setCallbackInterface(String callbackInterface) {
        check();
        this.callbackInterface = callbackInterface;
    }

    String interf;

    public String getInterface() {
        return interf;
    }

    public void setInterface(String interf) {
        check();
        this.interf = interf;
    }

    private InterfaceType type;

    public InterfaceType getInterfaceType() {
        return type;
    }

    public void setInterfaceType(InterfaceType type) {
        check();
        this.type = type;
    }

    private InterfaceType callbackType;
    
    public InterfaceType getCallbackInterfaceType() {
        return callbackType;
    }

    public void setCallbackInterfaceType(InterfaceType callbackType) {
        check();
        this.callbackType = callbackType;
    }
    
    public Sequence getAny() {
       throw new UnsupportedOperationException();
    }

    public Sequence getAnyAttribute() {
        throw new UnsupportedOperationException();
    }

    public void initialize(AssemblyModelContext modelContext) {
        check();
    }
    
    public void freeze() {
        frozen = true;
    }

    public boolean accept(AssemblyModelVisitor visitor) {
        if (visitor.visit(this)) {
            if (type != null && !type.accept(visitor)){
                return false;
            }
            if (callbackType != null && !callbackType.accept(visitor)){
                return false;
            }
        }
        return true;
    }

    protected void check(){
        if (frozen == true){
            throw new IllegalStateException("Attempt to modify a frozen configuration");
        }
    }

}
