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
package org.apache.tuscany.container.script.helper;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.spi.extension.TargetInvokerExtension;

/**
 * TargetInvoker that calls a function on a ScriptInstance
 */
public class ScriptHelperInvoker extends TargetInvokerExtension {

    protected ScriptHelperComponent component;

    protected String functionName;

    public ScriptHelperInvoker(String functionName, ScriptHelperComponent component) {
		  super(null, null, null);
        this.functionName = functionName;
        this.component = component;
    }

    /**
     * Invoke the function
     */
    public Object invokeTarget(final Object payload) throws InvocationTargetException {
        ScriptHelperInstance target = (ScriptHelperInstance) component.getTargetInstance();
        try {

            return target.invokeTarget(functionName, (Object[]) payload);

        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }

}
