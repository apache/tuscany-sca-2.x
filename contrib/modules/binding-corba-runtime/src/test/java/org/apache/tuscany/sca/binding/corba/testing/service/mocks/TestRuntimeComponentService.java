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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.provider.PolicyProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Mock RuntimeComponentService implementation. Only few methods needs to be
 * implemented.
 */
public class TestRuntimeComponentService implements RuntimeComponentService {

    private InterfaceContract interfaceContract;
    private RuntimeWire runtimeWire;

    public TestRuntimeComponentService(Object invocationTarget) {
        runtimeWire = new TestRuntimeWire(invocationTarget);
        List<Operation> operations = new ArrayList<Operation>();
        Method[] methods = invocationTarget.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            int mod = methods[i].getModifiers();
            if (methods[i].getDeclaringClass().equals(invocationTarget.getClass()) && Modifier.isPublic(mod)
                && !methods[i].getName().startsWith("_")) {
                Operation operation = new TestOperation();
                DataType returnType = new TestDataType(methods[i].getReturnType());
                operation.setOutputType(returnType);
                Class<?>[] argTypes = methods[i].getParameterTypes();
                List<DataType> argDataTypes = new ArrayList<DataType>();
                for (int j = 0; j < argTypes.length; j++) {
                    argDataTypes.add(new TestDataType(argTypes[j]));
                }
                TestDataType<List<DataType>> inputDataType = new TestDataType<List<DataType>>(null, argDataTypes);
                operation.setInputType(inputDataType);
                operations.add(operation);
                operation.setName(methods[i].getName());
            }
        }
        TestInterface iface = new TestInterface(operations, invocationTarget.getClass());
        interfaceContract = new TestInterfaceContract();
        interfaceContract.setInterface(iface);
    }

    public void addPolicyProvider(Binding binding, PolicyProvider policyProvider) {

    }

    public ServiceBindingProvider getBindingProvider(Binding binding) {
        return null;
    }

    public List<RuntimeWire> getCallbackWires() {
        return null;
    }

    public InvocationChain getInvocationChain(Binding binding, Operation operation) {
        return null;
    }

    public InvocationChain getInvocationChain(Binding binding, InterfaceContract interfaceContract, Operation operation) {
        return null;
    }

    public Invoker getInvoker(Binding binding, Operation operation) {
        return null;
    }

    public Invoker getInvoker(Binding binding, InterfaceContract interfaceContract, Operation operation) {
        return null;
    }

    public List<PolicyProvider> getPolicyProviders(Binding binding) {
        return null;
    }

    public RuntimeWire getRuntimeWire(Binding binding) {
        return runtimeWire;
    }

    public RuntimeWire getRuntimeWire(Binding binding, InterfaceContract interfaceContract) {
        return null;
    }

    public List<RuntimeWire> getRuntimeWires() {
        return null;
    }

    public void setBindingProvider(Binding binding, ServiceBindingProvider bindingProvider) {

    }

    public ComponentReference getCallbackReference() {
        return null;
    }

    public Service getService() {
        return null;
    }

    public void setCallbackReference(ComponentReference callbackReference) {

    }

    public void setService(Service service) {

    }

    public InterfaceContract getInterfaceContract() {
        return interfaceContract;
    }

    public InterfaceContract getInterfaceContract(Binding binding) {
        return getInterfaceContract();
    }     

    public String getName() {
        return null;
    }

    public boolean isCallback() {
        return false;
    }

    public void setInterfaceContract(InterfaceContract interfaceContract) {
        this.interfaceContract = interfaceContract;
    }

    public void setIsCallback(boolean isCallback) {

    }

    public void setName(String name) {

    }

    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean unresolved) {

    }

    public List<Object> getExtensions() {
        return null;
    }

    public List<Intent> getRequiredIntents() {
        return null;
    }

    public IntentAttachPointType getType() {
        return null;
    }

    public void setType(IntentAttachPointType type) {

    }

    public List<ConfiguredOperation> getConfiguredOperations() {
        return null;
    }

    public <B> B getBinding(Class<B> bindingClass) {
        return null;
    }

    public List<Binding> getBindings() {
        return null;
    }

    public Callback getCallback() {
        return null;
    }

    public <B> B getCallbackBinding(Class<B> bindingClass) {
        return null;
    }

    public void setCallback(Callback callback) {

    }

    public List<PolicySet> getApplicablePolicySets() {
        return null;
    }

    public List<PolicySet> getPolicySets() {
        return null;
    }

    @Override
    public Object clone() {
        return null;
    }

}
