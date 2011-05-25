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
import java.util.Arrays;
import java.util.List;

import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.Extension;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;

/**
 * Mock RuntimeComponentService implementation. Only few methods needs to be
 * implemented.
 */
public class TestRuntimeComponentService implements RuntimeComponentService {

    private InterfaceContract interfaceContract;
    private RuntimeEndpoint runtimeWire;

    public TestRuntimeComponentService(Object invocationTarget) {
        List<Operation> operations = new ArrayList<Operation>();
        Method[] methods = invocationTarget.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            int mod = methods[i].getModifiers();
            if (methods[i].getDeclaringClass().equals(invocationTarget.getClass()) && Modifier.isPublic(mod)
                && !methods[i].getName().startsWith("_")) {
                Operation operation = new TestOperation();
                DataType returnType = new TestDataType(methods[i].getReturnType());
                List<DataType> outputDataTypes = new ArrayList<DataType>();
                outputDataTypes.add(returnType);
                TestDataType<List<DataType>> outputDataType = new TestDataType<List<DataType>>(null, outputDataTypes);                
                operation.setOutputType(outputDataType);

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
        runtimeWire = new TestRuntimeWire(interfaceContract, invocationTarget);
    }

    public ComponentReference getCallbackReference() {
        // TODO Auto-generated method stub
        return null;
    }

    public Service getService() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setCallbackReference(ComponentReference callbackReference) {
        // TODO Auto-generated method stub
        
    }

    public void setService(Service service) {
        // TODO Auto-generated method stub
        
    }

    public List<Endpoint> getEndpoints() {
        // TODO Auto-generated method stub
        return Arrays.asList((Endpoint)runtimeWire);
    }

    public <B> B getBinding(Class<B> bindingClass) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Binding getBinding(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Binding> getBindings() {
        // TODO Auto-generated method stub
        return null;
    }

    public Callback getCallback() {
        // TODO Auto-generated method stub
        return null;
    }

    public <B> B getCallbackBinding(Class<B> bindingClass) {
        // TODO Auto-generated method stub
        return null;
    }

    public InterfaceContract getInterfaceContract(Binding binding) {
        // TODO Auto-generated method stub
        return interfaceContract;
    }

    public boolean isOverridingBindings() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setCallback(Callback callback) {
        // TODO Auto-generated method stub
        
    }

    public void setOverridingBindings(boolean overridingBindings) {
        // TODO Auto-generated method stub
        
    }

    public InterfaceContract getInterfaceContract() {
        // TODO Auto-generated method stub
        return interfaceContract;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isForCallback() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setForCallback(boolean isCallback) {
        // TODO Auto-generated method stub
        
    }

    public void setInterfaceContract(InterfaceContract interfaceContract) {
        // TODO Auto-generated method stub
        
    }

    public void setName(String name) {
        // TODO Auto-generated method stub
        
    }

    public List<Extension> getAttributeExtensions() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Object> getExtensions() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isUnresolved() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return super.clone();
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
    
    public boolean isJAXWSService() {
        return false;
    }
    
    public void setJAXWSService(boolean isJAXWSService) {
    }    
}
