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
 * Abstraction for the association of a service contract with a requestor or provider.
 */
public interface Port extends AssemblyModelObject {
    /**
     * Returns the contract for invocations of a service using this port.
     * @return the oontract for invocations of a service using this port
     */
    ServiceContract getServiceContract();

    /**
     * Set the contract for invocations of a service using this port.
     * @param contract the contract for invocations of a service using this port
     */
    void setServiceContract(ServiceContract contract);

    /**
     * Returns the name of the port where it is associated with a requestor or provider.
     * @return the name of the port
     */
    String getName();

    /**
     * Sets the name of the port where it is associated with a requestor or provider.
     * @param name the name of the port where it is associated with a requestor or provider
     */
    void setName(String name);

}
