/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.js.rhino;

import java.util.Iterator;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

/**
 * RhinoInvoker simplifies invoking JavaScript functions with Rhino.
 */
public class RhinoInvoker {

    private String scriptName;

    private String script;

    private Scriptable scriptScope;

    private Scriptable sharedScope;

    /*
     * Enable dynamic scopes so a script can be used concurrently with a global shared scope and individual execution scopes. See
     * http://www.mozilla.org/rhino/scopes.html TODO: need to review how ths fits in with Tuscany scopes
     */
    private static class MyFactory extends ContextFactory {
        protected boolean hasFeature(Context cx, int featureIndex) {
            if (featureIndex == Context.FEATURE_DYNAMIC_SCOPE) {
                return true;
            }
            return super.hasFeature(cx, featureIndex);
        }
    }

    static {
        ContextFactory.initGlobal(new MyFactory());
    }

    /**
     * Create a new RhinoInvoker.
     * 
     * @param scriptName
     *            the name of the script. Can be anything, only used in messages to identify the script
     * @param script
     *            the complete script
     */
    public RhinoInvoker(String scriptName, String script) {
        this(scriptName, script, (Map) null);
    }

    /**
     * Create a new RhinoInvoker.
     * 
     * @param scriptName
     *            the name of the script. Can be anything, only used in messages to identify the script
     * @param script
     *            the complete script
     * @param context
     *            name-value pairs that are added in to the scope where the script is compiled. May be null. The value objects are made available to
     *            the script by using a variable with the name.
     */
    public RhinoInvoker(String scriptName, String script, Map context) {
        this.scriptName = scriptName;
        this.script = script;
        initScriptScope(scriptName, script, context);
        initSharedScope();
    }

    /**
     * Construct a RhinoInvoker from another RhinoInvoker object. This uses the original script scope so the script doesn't need to be recompiled.
     */
    protected RhinoInvoker(String scriptName, String script, Scriptable scriptScope) {
        this.scriptName = scriptName;
        this.script = script;
        this.scriptScope = scriptScope;
        initSharedScope();
    }

    /**
     * Invoke a script function
     * 
     * @param functionName
     *            the name of the function to invoke.
     * @param arg
     *            arguments to the function, may be a single object or an array of objects.
     * @return the function return value.
     */
    public Object invoke(String functionName, Object args) {
        return invoke(functionName, args, null, null);
    }

    /**
     * Invoke a script function
     * 
     * @param functionName
     *            the name of the function to invoke.
     * @param arg
     *            arguments to the function, may be a single object or an array of objects.
     * @param contexts
     *            a Map of name-value pairs which are added to the invocation Scope to enable the script to access the values by using the variable in
     *            name.
     * @return the function return value.
     */
    public Object invoke(String functionName, Object args, Map contexts) {
        return invoke(functionName, args, null, contexts);
    }

    /**
     * Invoke a script function
     * 
     * @param functionName
     *            the name of the function to invoke.
     * @param arg
     *            arguments to the function, may be a single object or an array of objects.
     * @param responseClass
     *            the desired class of the response object.
     * @param contexts
     *            a Map of name-value pairs which are added to the invocation Scope to enable the script to access the values by using the variable in
     *            name.
     * @return the function return value.
     */
    public Object invoke(String functionName, Object arg, Class responseClass, Map contexts) {

        Context cx = Context.enter();
        try {

            Function function = getFunction(scriptScope, functionName);
            Scriptable invocationScope = getInvocationScope(cx, contexts);

            Object[] args = processArgs(arg, invocationScope);

            Object jsResponse = function.call(cx, invocationScope, invocationScope, args);

            Object response = processResponse(jsResponse, responseClass);

            return response;

        } finally {
            Context.exit();
        }
    }

    /**
     * Turn args to JS objects and convert any OMElement to E4X XML
     */
    protected Object[] processArgs(Object arg, Scriptable scope) {
        // TODO: implement pluggable way to transform objects (eg SDO or AXIOM) to E4X XML objects
        // if (arg instanceof OMElement) {
        // try {
        // arg = E4XAXIOMUtils.toScriptableObject((OMElement) arg, scope);
        // } catch (XmlException e) {
        // throw new RuntimeException(e);
        // }
        // } else if (arg instanceof MessageContext) {
        // arg = new E4XMessageContext((MessageContext) arg, scope);
        // }
        Object[] args;
        if (arg == null) {
            args = new Object[] { null };
        } else if (arg.getClass().isArray()) {
            args = (Object[]) arg;
            for (int i = 0; i < args.length; i++) {
                args[i] = Context.toObject(args[i], scope);
            }
        } else {
            args = new Object[] { Context.toObject(arg, scope) };
        }
        return args;
    }

    /**
     * Unwrap and convert response
     */
    protected Object processResponse(Object response, Class responseClass) {
        // TODO: implement pluggable way to transform E4X XML into specific objects (eg SDO or AXIOM)
        // } else if (response instanceof XMLObject) {
        // response = E4XAXIOMUtils.toOMElement((XMLObject) response);
        if (Context.getUndefinedValue().equals(response)) {
            response = null;
        } else if (response instanceof Wrapper) {
            response = ((Wrapper) response).unwrap();
        } else {
            if (responseClass != null) {
                response = Context.toType(response, responseClass);
            } else {
                response = Context.toType(response, String.class);
            }
        }
        return response;
    }

    /**
     * Create a Rhino scope and compile the script into it
     */
    protected void initScriptScope(String scriptName, String script, Map context) {
        Context cx = Context.enter();
        try {

            this.scriptScope = cx.initStandardObjects(null, true);
            Script compiledScript = cx.compileString(script, scriptName, 1, null);
            compiledScript.exec(cx, scriptScope);
            addContexts(scriptScope, context);

        } finally {
            Context.exit();
        }
    }

    /**
     * Initializes the shared scope
     */
    protected void initSharedScope() {
        Context cx = Context.enter();
        try {

            this.sharedScope = cx.newObject(scriptScope);
            sharedScope.setPrototype(scriptScope);
            sharedScope.setParentScope(null);

        } finally {
            Context.exit();
        }
    }

    /**
     * Get a Rhino scope for the function invocation. If the invocation has no context objects then this will use the shared scope otherwise a new
     * scope is created to hold the context objects. Any new variables the executing script might define will go in the sharedScope. This new scope is
     * just to hold the invocation specific context objects.
     */
    protected Scriptable getInvocationScope(Context cx, Map contexts) {

        Scriptable scope;
        if (contexts == null || contexts.size() == 0) {
            scope = sharedScope;
        } else {
            scope = cx.newObject(sharedScope);
            scope.setPrototype(sharedScope);
            scope.setParentScope(null);
            addContexts(scope, contexts);
        }

        return scope;
    }

    /**
     * Add the context to the scope. This will make the objects available to a script by using the name it was added with.
     */
    protected void addContexts(Scriptable scope, Map contexts) {
        if (contexts != null) {
            for (Iterator i = contexts.keySet().iterator(); i.hasNext();) {
                String name = (String) i.next();
                Object value = contexts.get(name);
                if (value != null) {
                    scope.put(name, scope, Context.toObject(value, scope));
                }
            }
        }
    }

    /**
     * Get the Rhino Function object for the named script function
     */
    protected Function getFunction(Scriptable scope, String functionName) {

        Object handleObj = scope.get(functionName, scope);

        if (!(handleObj instanceof Function)) {
            throw new RuntimeException("script function '" + functionName + "' is undefined or not a function in script " + scriptName);
        }

        return (Function) handleObj;
    }

    /**
     * Make a copy of this RhinoScript object. This shares the script scope to avoid the overhead of recompiling the script, and to allow any
     * initialization done by the script to be shared.
     */
    public RhinoInvoker copy() {
        return new RhinoInvoker(scriptName, script, scriptScope);
    }

    /**
     * Update the scope where the script is complied with new context values
     * 
     * @param properties
     */
    public void updateScriptScope(Map context) {
        Context.enter();
        try {
            addContexts(scriptScope, context);
        } finally {
            Context.exit();
        }
    }

}