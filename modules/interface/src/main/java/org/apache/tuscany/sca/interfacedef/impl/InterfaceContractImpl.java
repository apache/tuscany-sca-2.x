/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.interfacedef.impl;

import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;

/**
 * Represents an interface contract. InterfaceContractImpl
 * 
 * @version $Rev$ $Date$
 */
public abstract class InterfaceContractImpl implements InterfaceContract {
    private Interface callInterface;
    private Interface callbackInterface;

    public Interface getCallbackInterface() {
        return callbackInterface;
    }

    public Interface getInterface() {
        return callInterface;
    }

    public void setCallbackInterface(Interface callbackInterface) {
        this.callbackInterface = callbackInterface;
    }

    public void setInterface(Interface callInterface) {
        this.callInterface = callInterface;
    }

    public InterfaceContract makeUnidirectional(boolean isCallback) {
        if (!isCallback && callbackInterface == null)
            return this; // already a unidirectional forward interface contract

        if (isCallback && callInterface == null)
            return this; // already a unidirectional callback interface contract

        // contract is bidrectional, so create a new unidirectional contract        
        try {
            InterfaceContract newContract = clone();
            if (!isCallback) {
                newContract.setCallbackInterface(null); // create unidirectional forward interface contract
            } else {
                newContract.setInterface(null); // create unidirectional callback interface contract
            }
            return newContract;
        } catch (CloneNotSupportedException e) {
            // will not happen
            return null;
        }
    }

    @Override
    public InterfaceContractImpl clone() throws CloneNotSupportedException {
        InterfaceContractImpl copy = (InterfaceContractImpl)super.clone();
        if (this.callbackInterface != null) {
            copy.callbackInterface = (Interface)this.callbackInterface.clone();
        }
        if (this.callInterface != null) {
            copy.callInterface = (Interface)this.callInterface.clone();
        }
        return copy;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((callInterface == null) ? 0 : callInterface.hashCode());
        result = prime * result + ((callbackInterface == null) ? 0 : callbackInterface.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InterfaceContractImpl other = (InterfaceContractImpl)obj;
        if (callInterface == null) {
            if (other.callInterface != null) {
                return false;
            }
        } else if (!callInterface.equals(other.callInterface)) {
            return false;
        }
        if (callbackInterface == null) {
            if (other.callbackInterface != null) {
                return false;
            }
        } else if (!callbackInterface.equals(other.callbackInterface)) {
            return false;
        }
        return true;
    }

}
