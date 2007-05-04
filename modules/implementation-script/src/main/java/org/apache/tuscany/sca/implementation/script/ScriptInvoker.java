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

package org.apache.tuscany.sca.implementation.script;

import java.lang.reflect.InvocationTargetException;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.apache.tuscany.implementation.spi.AbstractInterceptor;

/**
 * Perform the actual script invocation
 */
public class ScriptInvoker extends AbstractInterceptor {

    protected ScriptImplementation impl;
    protected String operationName;

    /**
     * TODO: pasing in the impl is a hack to get at scriptEngine as thats all this uses
     * but its not created till the start method which is called after the invokers are created 
     */
    public ScriptInvoker(ScriptImplementation impl, String operationName) {
        this.impl = impl;
        this.operationName = operationName;
    }

    @Override
    public Object doInvoke(Object[] objects) throws InvocationTargetException {
        try {

            return ((Invocable)impl.scriptEngine).invokeFunction(operationName, objects);

        } catch (ScriptException e) {
            throw new InvocationTargetException(e);
        }
    }

}
