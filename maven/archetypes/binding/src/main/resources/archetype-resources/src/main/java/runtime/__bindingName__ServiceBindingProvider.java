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

package ${package}.runtime;

import ${package}.${bindingName}Binding;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

public class ${bindingName}ServiceBindingProvider implements ServiceBindingProvider {

    private RuntimeEndpoint endpoint;
    private InterfaceContract contract;

    public ${bindingName}ServiceBindingProvider(RuntimeEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public void start() {
        // add some code here to start the service

        // For this sample we'll just share it in a static
        ${bindingName}Stash.addService(endpoint.getBinding().getURI(), new ${bindingName}ServiceInvoker(endpoint));
        
        System.out.println("someAttr=" + ((${bindingName}Binding)endpoint.getBinding()).getSomeAttr());
    }

    public void stop() {
        ${bindingName}Stash.removeService(endpoint.getBinding().getURI());
    }

    public InterfaceContract getBindingInterfaceContract() {
        return contract;
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

}
