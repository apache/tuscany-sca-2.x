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
public interface ConfiguredPort extends AssemblyModelObject, RuntimeConfigurationHolder {

    /**
     * Returns the port that is being configured.
     * @return the port that is being configured
     */
    Port getPort();

    /**
     * Sets the port that is being configured.
     * @param port the port that is being configured
     */
    void setPort(Port port);

    /**
     * Returns the aggregate part containing this port.
     * @return the aggregate part that contains this port
     */
    AggregatePart getAggregatePart();
    
    /**
     * Returns the port's proxy factory
     * @return the port's proxy factory
     * todo should this be here or should it be provided in a sub-interface?
     */
    Object getProxyFactory();

    /**
     * Sets the port's proxy factory
     * @param proxyFactory the port's proxy factory
     * todo should this be here or should it be provided in a sub-interface?
     */
    void setProxyFactory(Object proxyFactory);
	
}
