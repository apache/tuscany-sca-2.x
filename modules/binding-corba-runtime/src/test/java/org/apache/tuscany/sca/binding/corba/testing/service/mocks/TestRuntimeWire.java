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

package org.apache.tuscany.sca.binding.corba.testing.service.mocks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Contract;
import org.apache.tuscany.sca.assembly.EndpointReference;
import org.apache.tuscany.sca.context.CompositeContext;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.InvokerAsync;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.EndpointRegistry;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * Mock RuntimeWire implementation. Only few methods needs to be implemented.
 */
public class TestRuntimeWire implements RuntimeEndpoint {
    private Object invocationTarget;
    private InterfaceContract interfaceContract;

    public TestRuntimeWire(InterfaceContract interfaceContract, Object invocationTarget) {
        this.interfaceContract = interfaceContract;
        this.invocationTarget = invocationTarget;
    }


    public Object invoke(Operation operation, Object[] args) throws InvocationTargetException {
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
        }
        Object result = null;
        try {
            Method[] methods = invocationTarget.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(operation.getName())) {
                    result = methods[i].invoke(invocationTarget, args);
                    break;
                }
            }
        } catch (InvocationTargetException e) {
            throw e;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return result;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
    }


    public InterfaceContract getBindingInterfaceContract() {
        // TODO Auto-generated method stub
        return interfaceContract;
    }


    public ServiceBindingProvider getBindingProvider() {
        // TODO Auto-generated method stub
        return null;
    }


    public InterfaceContract getComponentTypeServiceInterfaceContract() {
        // TODO Auto-generated method stub
        return interfaceContract;
    }


    public void setBindingProvider(ServiceBindingProvider provider) {
        // TODO Auto-generated method stub
        
    }


    public boolean isUnresolved() {
        // TODO Auto-generated method stub
        return false;
    }


    public void setUnresolved(boolean unresolved) {
        // TODO Auto-generated method stub
        
    }


    public ExtensionType getExtensionType() {
        // TODO Auto-generated method stub
        return null;
    }


    public List<PolicySet> getPolicySets() {
        // TODO Auto-generated method stub
        return null;
    }


    public List<Intent> getRequiredIntents() {
        // TODO Auto-generated method stub
        return null;
    }


    public void setExtensionType(ExtensionType type) {
        // TODO Auto-generated method stub
        
    }


    public void bind(CompositeContext context) {
        // TODO Auto-generated method stub
        
    }


    public void bind(ExtensionPointRegistry registry, EndpointRegistry endpointRegistry) {
        // TODO Auto-generated method stub
        
    }


    public InvocationChain getBindingInvocationChain() {
        // TODO Auto-generated method stub
        return null;
    }


    public CompositeContext getCompositeContext() {
        // TODO Auto-generated method stub
        return null;
    }


    public Contract getContract() {
        // TODO Auto-generated method stub
        return null;
    }


    public InvocationChain getInvocationChain(Operation operation) {
        // TODO Auto-generated method stub
        return null;
    }


    public List<InvocationChain> getInvocationChains() {
        // TODO Auto-generated method stub
        return null;
    }


    public List<PolicyProvider> getPolicyProviders() {
        // TODO Auto-generated method stub
        return null;
    }


    public Message invoke(Message msg) {
        // TODO Auto-generated method stub
        return null;
    }


    public Message invoke(Operation operation, Message msg) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void invokeAsync(Operation operation, Message msg) {
    }
    
    public void invokeAsyncResponse(InvokerAsync tailInvoker, Message msg) {
    }

    public void unbind() {
        // TODO Auto-generated method stub
        
    }


    public Binding getBinding() {
        // TODO Auto-generated method stub
        return null;
    }


    public List<EndpointReference> getCallbackEndpointReferences() {
        // TODO Auto-generated method stub
        return null;
    }


    public Component getComponent() {
        // TODO Auto-generated method stub
        return null;
    }


    public InterfaceContract getComponentServiceInterfaceContract() {
        // TODO Auto-generated method stub
        return interfaceContract;
    }


    public ComponentService getService() {
        // TODO Auto-generated method stub
        return null;
    }


    public String getURI() {
        // TODO Auto-generated method stub
        return null;
    }


    public boolean isRemote() {
        // TODO Auto-generated method stub
        return false;
    }


    public boolean matches(String serviceURI) {
        // TODO Auto-generated method stub
        return false;
    }


    public void setBinding(Binding binding) {
        // TODO Auto-generated method stub
        
    }


    public void setComponent(Component component) {
        // TODO Auto-generated method stub
        
    }


    public void setRemote(boolean remote) {
        // TODO Auto-generated method stub
        
    }


    public void setService(ComponentService service) {
        // TODO Auto-generated method stub
        
    }


    public void setURI(String uri) {
        // TODO Auto-generated method stub
        
    }
    
    public void setGeneratedWSDLContract(InterfaceContract wsdlContract){
        
    }
    
    public InterfaceContract getGeneratedWSDLContract(){
        return null;
    }
    
    public void validateServiceInterfaceCompatibility() {
        // TODO Auto-generated method stub
        
    }
    
    public InterfaceContract getGeneratedWSDLContract(
            InterfaceContract interfaceContract) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isAsyncInvocation() {
        // TODO Auto-generated method stub
        return false;
    }
}
