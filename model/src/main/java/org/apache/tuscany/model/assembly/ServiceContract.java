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
package org.apache.tuscany.model.assembly;



/**
 * Represents service contract.
 */
public interface ServiceContract extends Extensible {

    /**
     * Returns the interface.
     * @return
     */
    Class getInterface();

    /**
     * Sets the interface.
     * @param value
     */
    void setInterface(Class value);
    
    /**
     * Returns the callback interface.
     * @return
     */
    Class getCallbackInterface();

    /**
     * Sets the callback interface.
     * @param value
     */
    void setCallbackInterface(Class value);
    
    /**
     * Returns the scope of this service contract.
     * @return
     */
    Scope getScope();

    /**
     * Sets the scope.
     * @param scope of this service contract.
     */
    void setScope(Scope scope);

}
