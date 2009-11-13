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

package org.apache.tuscany.sca.binding.jms.wireformat.jmstext.runtime;

import org.apache.tuscany.sca.binding.jms.wireformat.WireFormatJMSText;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.provider.WireFormatProvider;
import org.apache.tuscany.sca.provider.WireFormatProviderFactory;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * @version $Rev$ $Date$
 */
public class WireFormatJMSTextProviderFactory implements WireFormatProviderFactory<WireFormatJMSText> {
    private ExtensionPointRegistry registry;
    
    public WireFormatJMSTextProviderFactory(ExtensionPointRegistry registry) {
        super();
        this.registry = registry;
    }

    /**
     */
    public WireFormatProvider createReferenceWireFormatProvider(RuntimeEndpointReference endpointReference) {
        return new WireFormatJMSTextReferenceProvider(registry, endpointReference);
    }

    /**
      */
    public WireFormatProvider createServiceWireFormatProvider(RuntimeEndpoint endpoint) {
        return new WireFormatJMSTextServiceProvider(registry, endpoint);
    }

    /**
     * @see org.apache.tuscany.sca.provider.ProviderFactory#getModelType()
     */
    public Class getModelType() {
        // TODO Auto-generated method stub
        return null;
    }

}
