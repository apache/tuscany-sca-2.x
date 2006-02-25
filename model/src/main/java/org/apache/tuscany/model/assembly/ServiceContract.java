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
 * The contract specified by a requestor or provider for invocations across a port.
 */
public interface ServiceContract extends Extensible {

    /**
     * Returns the interface for invocations from the requestor to the provider.
     * @return the interface for invocations from the requestor to the provider
     */
    Class getInterface();

    /**
     * Sets the interface for invocations from the requestor to the provider.
     * @param value the interface for invocations from the requestor to the provider
     */
    void setInterface(Class value);
    
    /**
     * Returns the callback interface for invocation from the provider back to its requestor.
     * @return the callback interface for invocation from the provider back to its requestor
     */
    Class getCallbackInterface();

    /**
     * Sets the callback interface for invocation from the provider back to its requestor.
     * @param value the callback interface for invocation from the provider back to its requestor
     */
    void setCallbackInterface(Class value);
    
    /**
     * Returns the scope of this service contract.
     * @return
     * todo missing javadoc
     */
    Scope getScope();

    /**
     * Sets the scope.
     * @param scope of this service contract.
     * todo missing javadoc
     */
    void setScope(Scope scope);

}
