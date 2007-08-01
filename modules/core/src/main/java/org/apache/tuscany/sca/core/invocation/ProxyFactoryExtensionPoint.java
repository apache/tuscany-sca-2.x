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

package org.apache.tuscany.sca.core.invocation;

import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;

/**
 * The extension point to plug in proxy factories
 * @version $Rev$ $Date$
 */
public interface ProxyFactoryExtensionPoint extends ProxyFactory {
    /**
     * Get the proxy factory for java interfaces
     * @return
     */
    ProxyFactory getInterfaceProxyFactory();

    /**
     * Get the proxy factory for java classes
     * @return
     */
    ProxyFactory getClassProxyFactory();

    /**
     * Set the proxy factory for java interfaces
     * @param factory
     */
    void setInterfaceProxyFactory(ProxyFactory factory);

    /**
     * Set the proxy factory for java classes
     * @param factory
     */
    void setClassProxyFactory(ProxyFactory factory);
    
    /**
     * @return the interfaceContractMapper
     */
    InterfaceContractMapper getInterfaceContractMapper();

}
