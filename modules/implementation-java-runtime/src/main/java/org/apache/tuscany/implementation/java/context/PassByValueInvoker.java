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

package org.apache.tuscany.implementation.java.context;

import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.tuscany.databinding.DataBinding;
import org.apache.tuscany.databinding.DataBindingRegistry;
import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.extension.TargetInvokerExtension;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * An interceptor to enforce pass-by-value semantics for remotable interfaces
 * 
 * @version $Rev$ $Date$
 */
public class PassByValueInvoker extends JavaTargetInvoker {
    private DataBindingRegistry registry;
    private Operation operation;

    /**
     * @param method
     * @param component
     * @param scopeContainer
     * @param passByValue
     */
    public PassByValueInvoker(DataBindingRegistry registry, Operation operation, Method method, AtomicComponent component, ScopeContainer scopeContainer) {
        super(method, component, scopeContainer);
        this.registry = registry;
        this.operation = operation;
    }

    
    public Message invoke(Message msg) {
        Object obj = msg.getBody();
        msg.setBody(copy((Object[])obj));
        
        Message result = super.invoke(msg);

        if (!result.isFault()) {
            String dataBindingId = operation.getOutputType().getDataBinding();
            DataBinding dataBinding = registry.getDataBinding(dataBindingId);
            result.setBody(copy(result.getBody(), dataBinding));
        }
        return result;
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
                    DataBinding dataBinding = registry.getDataBinding(dataBindingId);
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
            DataType<?> dataType = registry.introspectType(arg);
            if (dataType != null) {
                DataBinding binding = registry.getDataBinding(dataType.getDataBinding());
                if (binding != null) {
                    copiedArg = binding.copy(arg);
                }
            }
            // FIXME: What to do if it's not recognized?
        }
        return copiedArg;
    }

}
