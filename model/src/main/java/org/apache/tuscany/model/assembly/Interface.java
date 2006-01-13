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

import org.apache.tuscany.model.types.InterfaceType;


/**
 * A representation of the model object '<em><b>Interface</b></em>'.
 */
public interface Interface extends ExtensibleModelObject {

    /**
     * Returns the value of the '<em><b>Callback Interface</b></em>' attribute.
     */
    String getCallbackInterface();

    /**
     * Sets the value of the '{@link org.osoa.sca.model.JavaInterface#getCallbackInterface <em>Callback Interface</em>}' attribute.
     */
    void setCallbackInterface(String value);

    /**
     * Returns the value of the '<em><b>Interface</b></em>' attribute.
     */
    String getInterface();

    /**
     * Sets the value of the '{@link org.osoa.sca.model.JavaInterface#getInterface <em>Interface</em>}' attribute.
     */
    void setInterface(String value);

    /**
     * Returns the InterfaceType representing this interface.
     *
     * @return
     */
    InterfaceType getInterfaceType();

    /**
     * Returns the InterfaceType representing this interface.
     *
     * @return
     */
    InterfaceType getCallbackInterfaceType();

    /**
     * Returns the scope of this interface.
     *
     * @return
     */
    ScopeEnum getScope();

    /**
     * Sets the scope.
     *
     * @param scope
     */
    void setScope(ScopeEnum scope);

} // Interface
