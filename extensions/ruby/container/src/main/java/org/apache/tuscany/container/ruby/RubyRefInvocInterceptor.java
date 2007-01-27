/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at

             http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.     
 */
package org.apache.tuscany.container.ruby;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.jruby.IRuby;
import org.jruby.RubyObject;
import org.jruby.javasupport.JavaUtil;

/**
 * This Interceptor encasulates the data mediation required  by the JavaScriptReferenceProxy.  The 
 * invocation handler of this class traps the javascript reference calls, performs data mediation 
 * and calls then calls the actual referred service.  This classes implementation is subject to 
 * review and change when the DataMediation infrastructure of Tuscany is ready.
 *
 */
public class RubyRefInvocInterceptor implements InvocationHandler {
    private Object actualProxy;

    private Class wireInterface;
    
    private IRuby rubyEngine;

    RubyRefInvocInterceptor(Object wireProxy, Class wireIfc, IRuby rubyEng) {
        this.actualProxy = wireProxy;
        this.wireInterface = wireIfc;
        this.rubyEngine = rubyEng;
    }

    public Object invoke(Object arg0, Method method, Object[] args) throws Throwable {
        if ( method.getName().equals("hashCode"))
            return new Integer(1);
        
        Method invokedMethod = getInvokedMethod(method.getName());
        Object[] tranformedArgs = new Object[args.length];
        for (int count = 0; count < args.length; ++count) {
            tranformedArgs[count] = fromRubyToJava(invokedMethod.getParameterTypes()[count], args[count]);
        }

        Object response = invokedMethod.invoke(actualProxy, tranformedArgs);
        response = fromJavaToRuby(response);
        return response;
    }

    private Method getInvokedMethod(String methodName) {
        Method[] methods = wireInterface.getMethods();

        for (int count = 0; count < methods.length; ++count) {
            if (methods[count].getName().equals(methodName)) {
                return methods[count];
            }
        }
        throw new RuntimeException("Unable to find invocation method");
    }

    protected Object fromRubyToJava(Class reqArgType, Object rubyArg) throws Exception {
        Object javaArg = null;
        
        //for known cases the JRuby runtime handles the conversion before calling the Java objects
        //so nothing to do.  When it cannot convert it simply passed the instance of RubyObject
        if ( rubyArg instanceof RubyObject ) {
            //need to deal with this
        } else { 
            javaArg = rubyArg;
        }

        return javaArg;
    }

    protected Object fromJavaToRuby(Object retVal) throws RuntimeException {
        Object rubyRetVal = JavaUtil.convertJavaToRuby(rubyEngine, retVal, retVal.getClass());
        return rubyRetVal;
    }
}
