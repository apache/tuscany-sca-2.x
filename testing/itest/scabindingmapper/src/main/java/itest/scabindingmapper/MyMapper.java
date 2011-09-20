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

package itest.scabindingmapper;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.binding.jsonp.JSONPBinding;
import org.apache.tuscany.sca.binding.sca.provider.DefaultSCABindingMapper;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeEndpointReference;

/**
 * A test SCABindingMapper to demonstrate changing the protocol on a per service basis. 
 * This uses JSONP if the service name ends with a "2" character.
 * Uses a high ranking in the The meta-inf/services file to override the default mapper.
 */
public class MyMapper extends DefaultSCABindingMapper {

    public MyMapper(ExtensionPointRegistry registry, Map<String, String> attributes) {
        super(registry, attributes);
        alwaysDistributed = true;
    }

    @Override
    protected QName chooseBinding(RuntimeEndpoint endpoint) {
        QName bindingType = super.chooseBinding(endpoint);
        
        if (!bindingType.equals(super.defaultLocalBinding)) {
            if (endpoint.getComponent().getName().endsWith("2")) {
                bindingType = JSONPBinding.TYPE;
            } else {
                bindingType = super.defaultMappedBinding;
            }
        }
        return bindingType;
    }

    @Override
    protected QName chooseBinding(RuntimeEndpointReference endpointReference) {
        QName bindingType = super.chooseBinding(endpointReference);
        
        if (!bindingType.equals(super.defaultLocalBinding)) {
            if (endpointReference.getBinding().getURI().contains("Service2")) {
                bindingType = JSONPBinding.TYPE;
            } else {
                bindingType = super.defaultMappedBinding;
            }
        }
        return bindingType;
    }

}
