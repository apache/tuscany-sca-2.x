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
package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Responsible for invoking on an outbound wire associated with a callback. The handler retrieves the correct outbound
 * callback wire from the work context.  
 *
 * TODO cache target invoker
 * @version $Rev$ $Date$
 */
public class JDKCallbackInvocationHandler extends AbstractJDKOutboundInvocationHandler {

    private WorkContext context;

    public JDKCallbackInvocationHandler(WorkContext context) {
        this.context = context;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        OutboundWire<?> wire = context.getCurrentInvocationWire();
        context.setCurrentInvocationWire(null);
        OutboundInvocationChain chain = wire.getSourceCallbackInvocationChains().get(method);
        TargetInvoker invoker = chain.getTargetInvoker();
        return invoke(chain, invoker, args);
    }


    public Object invoke(Method method, Object[] args) throws Throwable {
        return invoke(null, method, args);
    }
}
