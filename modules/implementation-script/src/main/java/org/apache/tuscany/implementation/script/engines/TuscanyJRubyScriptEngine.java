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
package org.apache.tuscany.implementation.script.engines;

import java.io.File;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.jruby.Ruby;
import org.jruby.ast.Node;
import org.jruby.internal.runtime.GlobalVariable;
import org.jruby.internal.runtime.GlobalVariables;
import org.jruby.internal.runtime.ReadonlyAccessor;
import org.jruby.javasupport.Java;
import org.jruby.javasupport.JavaObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Block;
import org.jruby.runtime.IAccessor;
import org.jruby.runtime.builtin.IRubyObject;

import com.sun.script.jruby.JRubyScriptEngineFactory;

/* 
 * This class is a copy of the class com.sun.script.ruby.JRubyScriptEngine with some minor modifications.
 * */

public class TuscanyJRubyScriptEngine extends AbstractScriptEngine 
        implements Compilable, Invocable { 

    // my factory, may be null
    private ScriptEngineFactory factory;
    private Ruby runtime;
   
    public TuscanyJRubyScriptEngine() {
        init(System.getProperty("com.sun.script.jruby.loadpath"));
    }

    public TuscanyJRubyScriptEngine(String loadPath) {
        init(loadPath);
    }

    // my implementation for CompiledScript
    private class JRubyCompiledScript extends CompiledScript {
        // my compiled code
        private Node node;     

        JRubyCompiledScript (Node node) {
            this.node = node;
        }

        public ScriptEngine getEngine() {
            return TuscanyJRubyScriptEngine.this;
        }

        public Object eval(ScriptContext ctx) throws ScriptException {
            return evalNode(node, ctx);
        }
    }

    // Compilable methods
    public CompiledScript compile(String script) 
                                  throws ScriptException {  
        Node node = compileScript(script, context);
        return new JRubyCompiledScript(node);
    }

    public CompiledScript compile (Reader reader) 
                                  throws ScriptException {  
        Node node = compileScript(reader, context);
        return new JRubyCompiledScript(node);
    }

    // Invocable methods
    public Object invokeFunction(String name, Object... args) 
                         throws ScriptException {       
        return invokeImpl(null, name, args, Object.class);
    }

    public Object invokeMethod(Object obj, String name, Object... args) 
                         throws ScriptException {       
        if (obj == null) {
            throw new IllegalArgumentException("script object is null");
        }
        return invokeImpl(obj, name, args, Object.class);
    }

    public Object getInterface(Object obj, Class clazz) {
        if (obj == null) {
            throw new IllegalArgumentException("script object is null");
        }
        return makeInterface(obj, clazz);
    }

    public Object getInterface(Class clazz) {
        return makeInterface(null, clazz);
    }

    private <T> T makeInterface(Object obj, Class<T> clazz) {
        if (clazz == null || !clazz.isInterface()) {
            throw new IllegalArgumentException("interface Class expected");
        }
        final Object thiz = obj;
        return (T) Proxy.newProxyInstance(
              clazz.getClassLoader(),
              new Class[] { clazz },
              new InvocationHandler() {
                  public Object invoke(Object proxy, Method m, Object[] args)
                                       throws Throwable {
                      return invokeImpl(thiz, m.getName(),
                                        args, m.getReturnType());
                  }
              });
    }

    // ScriptEngine methods
    public synchronized Object eval(String str, ScriptContext ctx) 
                       throws ScriptException { 
        Node node = compileScript(str, ctx);
        return evalNode(node, ctx);
    }

    public synchronized Object eval(Reader reader, ScriptContext ctx)
                       throws ScriptException { 
        Node node = compileScript(reader, ctx);
        return evalNode(node, ctx);
    }

    public ScriptEngineFactory getFactory() {
        synchronized (this) {
            if (factory == null) {
                factory = new JRubyScriptEngineFactory();
            }
        }
        return factory;
    }

    public Bindings createBindings() {
        return new SimpleBindings();
    }

    // package-private methods
    void setFactory(ScriptEngineFactory factory) {
        this.factory = factory;
    } 

    // internals only below this point    

    private Object rubyToJava(IRubyObject value) {
        return rubyToJava(value, Object.class);
    }

    private Object rubyToJava(IRubyObject value, Class type) {
        return JavaUtil.convertArgument(
                 Java.ruby_to_java(value, value, Block.NULL_BLOCK), 
                 type);
    }

    private IRubyObject javaToRuby(Object value) {
        if (value instanceof IRubyObject) {
            return (IRubyObject) value;
        }
        IRubyObject result = JavaUtil.convertJavaToRuby(runtime, value);
        if (result instanceof JavaObject) {
            return runtime.getModule("JavaUtilities").callMethod(runtime.getCurrentContext(), "wrap", result);
        }
        return result;
    }   

    private synchronized Node compileScript(String script, ScriptContext ctx) 
                                 throws ScriptException {        
        GlobalVariables oldGlobals = runtime.getGlobalVariables();  
        try {
            setGlobalVariables(ctx);
            String filename = (String) ctx.getAttribute(ScriptEngine.FILENAME);
            if (filename == null) {
                filename = "<unknown>";
            }
            return runtime.parse(script, filename, null);
        } catch (Exception exp) {
            throw new ScriptException(exp);
        } finally {
            if (oldGlobals != null) {
                //setGlobalVariables(oldGlobals);
            }
        }
    }

    private synchronized Node compileScript(Reader reader, ScriptContext ctx) 
                                 throws ScriptException {        
        GlobalVariables oldGlobals = runtime.getGlobalVariables();  
        try {
            setGlobalVariables(ctx);
            String filename = (String) ctx.getAttribute(ScriptEngine.FILENAME);
            if (filename == null) {
                filename = "<unknown>";
            }
            return runtime.parse(reader, filename, null);
        } catch (Exception exp) {
            throw new ScriptException(exp);
        } finally {
            if (oldGlobals != null) {
                //setGlobalVariables(oldGlobals);
            }
        }
    }

    private void setGlobalVariables(final ScriptContext ctx) {
        ctx.setAttribute("context", ctx, ScriptContext.ENGINE_SCOPE);
        setGlobalVariables(new GlobalVariables(runtime) {
                GlobalVariables parent = runtime.getGlobalVariables();
                
                public void define(String name, IAccessor accessor) {
                    assert name != null;
                    assert accessor != null;
                    assert name.startsWith("$");
                    synchronized (ctx) {
                        Bindings engineScope = ctx.getBindings(ScriptContext.ENGINE_SCOPE);                  
                        engineScope.put(name, new GlobalVariable(accessor)); 
                    }
                }


                public void defineReadonly(String name, IAccessor accessor) {
                    assert name != null;
                    assert accessor != null;
                    assert name.startsWith("$");
                    synchronized (ctx) {
                        Bindings engineScope = ctx.getBindings(ScriptContext.ENGINE_SCOPE);
                        engineScope.put(name, new GlobalVariable(new 
                                             ReadonlyAccessor(name, accessor)));
                    }
                } 

                public boolean isDefined(String name) {
                    assert name != null;
                    assert name.startsWith("$");
                    synchronized (ctx) {
                        String modifiedName = name.substring(1);
                        boolean defined = ctx.getAttributesScope(modifiedName) != -1;
                        return defined ? true : parent.isDefined(name);
                    }
                }

                public void alias(String name, String oldName) {
                    assert name != null;
                    assert oldName != null;
                    assert name.startsWith("$");
                    assert oldName.startsWith("$");

                    if (runtime.getSafeLevel() >= 4) {
                        throw runtime.newSecurityError("Insecure: can't alias global variable");
                    }

                    synchronized (ctx) {
                        int scope = ctx.getAttributesScope(name);
                        if (scope == -1) {
                            scope = ScriptContext.ENGINE_SCOPE;
                        }

                        IRubyObject value = get(oldName);
                        ctx.setAttribute(name, rubyToJava(value), scope);
                    }
                }

                public IRubyObject get(String name) {
                    assert name != null;
                    assert name.startsWith("$");

                    synchronized (ctx) {
                        // skip '$' and try
                        String modifiedName = name.substring(1);
                        int scope = ctx.getAttributesScope(modifiedName);
                        if (scope == -1) {
                            return parent.get(name);
                        }

                        Object obj = ctx.getAttribute(modifiedName, scope);
                        if (obj instanceof IAccessor) {
                            return ((IAccessor)obj).getValue();
                        } else {
                            return javaToRuby(obj);
                        }
                    }                    
                }

                public IRubyObject set(String name, IRubyObject value) {
                    assert name != null;
                    assert name.startsWith("$");

                    if (runtime.getSafeLevel() >= 4) {
                        throw runtime.newSecurityError("Insecure: can't change global variable value");
                    }

                    synchronized (ctx) {
                        // skip '$' and try
                        String modifiedName = name.substring(1);
                        int scope = ctx.getAttributesScope(modifiedName);
                        if (scope == -1) {
                            scope = ScriptContext.ENGINE_SCOPE;
                        }
                        IRubyObject oldValue = get(name);
                        Object obj = ctx.getAttribute(modifiedName, scope);
                        if (obj instanceof IAccessor) {
                            ((IAccessor)obj).setValue(value);
                        } else {                        
                            ctx.setAttribute(modifiedName, rubyToJava(value), scope);
                        }
                        return oldValue;
                    }
                }

                public Iterator getNames() {                    
                    List list = new ArrayList();
                    synchronized (ctx) {
                        Iterator<Integer> iterator = ctx.getScopes().iterator();
                        int scope;
                        while (iterator.hasNext()) {
                            scope = iterator.next().intValue();
                            Bindings b = ctx.getBindings(scope);
                            if (b != null) {
                                Iterator<String> bIterator = b.keySet().iterator();
                                while (bIterator.hasNext()) {
                                    list.add(bIterator.next());
                                }
                            }
                        }
                    }
                    for (Iterator names = parent.getNames(); names.hasNext();) {
                        list.add(names.next());
                    }
                    return Collections.unmodifiableList(list).iterator();
                }
            });
    }

    private void setGlobalVariables(GlobalVariables globals) {
        runtime.setGlobalVariables(globals);
    }

    private synchronized Object evalNode(Node node, ScriptContext ctx) 
                            throws ScriptException {
        GlobalVariables oldGlobals = runtime.getGlobalVariables();
        try {
            setGlobalVariables(ctx);
            return rubyToJava(runtime.eval(node));
        } catch (Exception exp) {
            throw new ScriptException(exp);
        } finally {
            if (oldGlobals != null) {
                //setGlobalVariables(oldGlobals);
            }
        }
    }

    private void init(String loadPath) {        
        runtime = Ruby.getDefaultInstance();
        if (loadPath == null) {
            loadPath = System.getProperty("java.class.path");
        }
        List list = Arrays.asList(loadPath.split(File.pathSeparator));
        runtime.getLoadService().init(list);                
        runtime.getLoadService().require("java");
    }

    private Object invokeImpl(final Object obj, String method, 
                        Object[] args, Class returnType)
                        throws ScriptException {
        if (method == null) {
            throw new NullPointerException("method name is null");
        }

        try {
            IRubyObject rubyRecv = obj != null ? 
                  JavaUtil.convertJavaToRuby(runtime, obj) : runtime.getTopSelf();

            IRubyObject[] rubyArgs = JavaUtil.convertJavaArrayToRuby(runtime, args);

            // Create Ruby proxies for any input arguments that are not primitives.
            IRubyObject javaUtilities = runtime.getObject().getConstant("JavaUtilities");
            for (int i = 0; i < rubyArgs.length; i++) {
                IRubyObject tmp = rubyArgs[i];
                if (tmp instanceof JavaObject) {
                    rubyArgs[i] = javaUtilities.callMethod(runtime.getCurrentContext(), "wrap", tmp);
                }
            }

            IRubyObject result = rubyRecv.callMethod(runtime.getCurrentContext(), method, rubyArgs);
            return rubyToJava(result, returnType);
        } catch (Exception exp) {
            throw new ScriptException(exp);
        }
    }
}