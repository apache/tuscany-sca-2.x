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
 * Represents a configured port (e.g. a configured reference or configured service).
 */
public interface ConfiguredPort<P extends Port> extends AssemblyObject, ProxyFactoryHolder {

    /**
     * Returns the name of the port being configured.
     *
     * @return the name of the port being configured
     */
    String getName();

    /**
     * Set the name of the port being configured.
     *
     * @param name the name of the port being configured
     */
    void setName(String name);

    /**
     * Returns the port that is being configured.
     * @return the port that is being configured
     */
    P getPort();

    /**
     * Sets the port that is being configured.
     * @param port the port that is being configured
     */
    void setPort(P port);

    /**
     * Returns the part containing this port.
     * @return the part that contains this port
     */
    Part getPart();
    
    /**
     * Sets the configured part containing this port.
     * @param part the configured part containing this port.
     */
    void setPart(Part part);
    
}
