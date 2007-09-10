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

package org.apache.tuscany.sca.core.databinding.wire;

import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.DataBindingExtensionPoint;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

public class PassByValueInteceptor implements Interceptor {

    private DataBindingExtensionPoint dataBindings;
    private Operation operation;

    private Invoker nextInvoker;

    public PassByValueInteceptor(DataBindingExtensionPoint dataBindings, Operation operation) {
        this.dataBindings = dataBindings;
        this.operation = operation;
    }

    public Message invoke(Message msg) {
        Object obj = msg.getBody();
        msg.setBody(copy((Object[])obj));

        Message resultMsg = nextInvoker.invoke(msg);

        if (!msg.isFault() && operation.getOutputType() != null) {
            String dataBindingId = operation.getOutputType().getDataBinding();
            DataBinding dataBinding = dataBindings.getDataBinding(dataBindingId);
            resultMsg.setBody(copy(resultMsg.getBody(), dataBinding));
        }
        return resultMsg;
    }

    public Object[] copy(Object[] args) {
        if (args == null) {
            return null;
        }
        Object[] copiedArgs = new Object[args.length];
        Map<Object, Object> map = new IdentityHashMap<Object, Object>();
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                copiedArgs[i] = null;
            } else {
                Object copiedArg = map.get(args[i]);
                if (copiedArg != null) {
                    copiedArgs[i] = copiedArg;
                } else {
                    String dataBindingId = operation.getInputType().getLogical().get(i).getDataBinding();
                    DataBinding dataBinding = dataBindings.getDataBinding(dataBindingId);
                    copiedArg = copy(args[i], dataBinding);
                    map.put(args[i], copiedArg);
                    copiedArgs[i] = copiedArg;
                }
            }
        }
        return copiedArgs;
    }

    public Object copy(Object arg, DataBinding argDataBinding) {
        if (arg == null) {
            return null;
        }
        Object copiedArg;
        if (argDataBinding != null) {
            copiedArg = argDataBinding.copy(arg);
        } else {
            copiedArg = arg;
            DataType<?> dataType = dataBindings.introspectType(arg);
            if (dataType != null) {
                DataBinding binding = dataBindings.getDataBinding(dataType.getDataBinding());
                if (binding != null) {
                    copiedArg = binding.copy(arg);
                }
            }
            // FIXME: What to do if it's not recognized?
        }
        return copiedArg;
    }

    public Invoker getNext() {
        return nextInvoker;
    }

    public void setNext(Invoker next) {
        this.nextInvoker = next;
    }

}
