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

package org.apache.tuscany.sca.provider;

import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.invocation.Interceptor;

/**
 * @version $Rev$ $Date$
 * @tuscany.spi.extension.inheritfrom
 */
public interface WireFormatProvider {
    
    /**
     * Set up the contract that describes the interface that
     * is providing data to or accepting data from the 
     * wire format interceptor. The wire format's job
     * is to translate between this interface contract and the
     * format on the wire. The interface contract may be
     * configured separately for request and response
     * wire formats
     * 
     * @return the wire format interface contract
     */
    public InterfaceContract configureWireFormatInterfaceContract(InterfaceContract interfaceContract);    
    
    /**
     * Create an interceptor for the wire format
     * @return An interceptor that realize the policySet
     */
    Interceptor createInterceptor();

    /**
     * Get the phase that the interceptor should be added
     * @return The phase that this interceptor belongs to
     */
    String getPhase();
}
