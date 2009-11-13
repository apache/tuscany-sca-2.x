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

import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * @version $Rev$ $Date$
 */
public interface WireFormatProviderFactory<M extends WireFormat> extends ProviderFactory<M> {
    /**
     * Create wire format provider for a given reference binding
     * @param endpointReference The endpoint reference
     * @return
     */
    WireFormatProvider createReferenceWireFormatProvider(RuntimeEndpointReference endpointReference);

    /**
     * Create policy provider for a given service binding
     * @param endpoint TODO
     * @return
     */
    WireFormatProvider createServiceWireFormatProvider(RuntimeEndpoint endpoint);
}
