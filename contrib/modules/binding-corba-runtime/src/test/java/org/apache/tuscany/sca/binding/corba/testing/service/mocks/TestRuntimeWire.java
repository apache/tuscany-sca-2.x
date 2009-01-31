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

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.InvocationChain;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Mock RuntimeWire implementation. Only few methods needs to be implemented.
 */
public class TestRuntimeWire implements RuntimeWire {
    private Object invocationTarget;

    public TestRuntimeWire(Object invocationTarget) {
        this.invocationTarget = invocationTarget;
    }

    public InvocationChain getInvocationChain(Operation arg0) {
        return null;
    }

    public List<InvocationChain> getInvocationChains() {
        return null;
    }

    public EndpointReference getSource() {
        return null;
    }

    public EndpointReference getTarget() {
        return null;
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

    public Object invoke(Operation operation, Message arg1) throws InvocationTargetException {

        return null;
    }
    
    public Object invoke(Message arg1) throws InvocationTargetException {

        return null;
    }    

    public void rebuild() {

    }

    public void setTarget(EndpointReference arg0) {

    }

    @Override
    public Object clone() {
        return null;
    }

    public InvocationChain getBindingInvocationChain() {
        return null;
    }
}
