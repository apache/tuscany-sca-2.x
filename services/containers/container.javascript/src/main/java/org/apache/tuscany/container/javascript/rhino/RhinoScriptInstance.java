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
package org.apache.tuscany.container.javascript.rhino;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.UniqueTag;

/**
 * An invokeable instance of a JavaScript script.
 */
public class RhinoScriptInstance {

    private Scriptable scriptScope;

    private Scriptable instanceScope;

    private Map<String, Class> responseClasses;

    public RhinoScriptInstance(Scriptable scriptScope, Scriptable instanceScope, Map<String, Object> context, Map<String, Class> responseClasses) {
        this.scriptScope = scriptScope;
        this.instanceScope = instanceScope;
        this.responseClasses = responseClasses;
        if (this.responseClasses == null) {
            this.responseClasses = new HashMap<String, Class>();
        }
        addContexts(instanceScope, context);
    }
    
    public Object invokeFunction(String functionName, Object[] args) {
        return invokeFunction(functionName, args, null);
    }

    public Object invokeFunction(String functionName, Object[] args, Class respClass) {
        RhinoFunctionInvoker invoker = createRhinoFunctionInvoker(functionName, respClass);
        return invoker.invoke(args);
    }

    public RhinoFunctionInvoker createRhinoFunctionInvoker(String functionName) {
        return createRhinoFunctionInvoker(functionName, null);
    }

    
    public RhinoFunctionInvoker createRhinoFunctionInvoker(String functionName, Class responseClass) {
        Function function = getFunction(functionName);
        //Class responseClass = responseClasses.get(functionName);
        RhinoFunctionInvoker invoker = new RhinoFunctionInvoker(instanceScope, function, responseClass);
        return invoker;
    }

    /**
     * Add the context to the scope. This will make the objects available to a script by using the name it was added with.
     */
    protected void addContexts(Scriptable scope, Map contexts) {
        if (contexts != null) {
            Context.enter();
            try {
                for (Iterator i = contexts.keySet().iterator(); i.hasNext();) {
                    String name = (String) i.next();
                    Object value = contexts.get(name);
                    if (value != null) {
                        scope.put(name, scope, Context.toObject(value, scope));
                    }
                }
            } finally {
                Context.exit();
            }
        }
    }

    /**
     * Get the Rhino Function object for the named script function
     */
    protected Function getFunction(String functionName) {

        Object handleObj = scriptScope.get(functionName, instanceScope);
        if (UniqueTag.NOT_FOUND.equals(handleObj)) {
            // Bit of a hack so E4X scripts don't need to define a function for every operation
            handleObj = scriptScope.get("process", instanceScope);
        }
        if (!(handleObj instanceof Function)) {
            throw new RuntimeException("script function '" + functionName + "' is undefined or not a function");
        }

        return (Function) handleObj;
    }

}

