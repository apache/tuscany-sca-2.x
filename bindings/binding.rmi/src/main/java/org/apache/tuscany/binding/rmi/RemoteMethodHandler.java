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
package org.apache.tuscany.binding.rmi;    
 
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.tuscany.spi.wire.WireInvocationHandler;

public class RemoteMethodHandler implements MethodInterceptor  
{
    public static final String FINALIZE_METHOD = "finalize";
    private WireInvocationHandler wireHandler = null;
 

    public RemoteMethodHandler(WireInvocationHandler handler ) 
    {
        this.wireHandler = handler;
    }

    
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable 
    {
        //TO BE FIXED: don't know why it hangs for the finalize method... so blocking it for now
        if ( !method.getName().equals(FINALIZE_METHOD) )
        {
            return wireHandler.invoke(method, args);
        }
        return methodProxy.invoke(object, args);
        //return null;
    }

}

