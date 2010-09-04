#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package ${package}.binding.foo.runtime;

import ${package}.binding.foo.FooBinding;
import ${package}.core.ExtensionPointRegistry;
import ${package}.provider.BindingProviderFactory;
import ${package}.provider.ReferenceBindingProvider;
import ${package}.provider.ServiceBindingProvider;
import ${package}.runtime.RuntimeEndpoint;
import ${package}.runtime.RuntimeEndpointReference;

public class FooBindingProviderFactory implements BindingProviderFactory<FooBinding> {

    public FooBindingProviderFactory(ExtensionPointRegistry extensionPoints) {
    }

    public Class<FooBinding> getModelType() {
        return FooBinding.class;
    }

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeEndpointReference endpoint) {
        return new FooReferenceBindingProvider(endpoint);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeEndpoint endpoint) {
        return new FooServiceBindingProvider(endpoint);
    }

}
