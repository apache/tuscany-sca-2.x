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
package org.apache.tuscany.container.ruby.rubyscript;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.jruby.IRuby;
import org.jruby.RubyString;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * A RhinoScript represents a compiled JavaScript script
 */
public class RubyScript {
    protected final String NEW = ".new";
    protected final String EQUAL = "=";

    protected String scriptName;

    protected String script;

    protected Map<String, Class> responseClasses;

    protected ClassLoader classLoader;

    private IRuby rubyEngine = JavaEmbedUtils.initialize(new Vector());

    /**
     * Create a new RubyScript.
     * 
     * @param scriptName
     *            the name of the script. Can be anything, only used in messages to identify the script
     * @param script
     *            the complete script
     */
    public RubyScript(String scriptName, String script) {
        this(scriptName, script, (Map) null, null);
    }

    /**
     * Create a new RubyScript.
     * 
     * @param scriptName
     *            the name of the script. Can be anything, only used in messages to identify the script
     * @param script
     *            the complete script
     * @param context
     *            name-value pairs that are added in to the scope where the script is compiled. May be null. The value objects are made available to
     *            the script by using a variable with the name.
     * @param classLoader
     *            the ClassLoader to be used to locate any user Java classes used in the script
     */
    public RubyScript(String scriptName, String script, Map context, ClassLoader classLoader) {
        this.scriptName = scriptName;
        this.script = script;
        this.responseClasses = new HashMap<String, Class>();
        this.classLoader = classLoader;
        rubyEngine.loadScript((RubyString) JavaUtil.convertJavaToRuby(rubyEngine,
                                                                      "MyScript.rb",
                                                                      String.class),
                              (RubyString) JavaUtil.convertJavaToRuby(rubyEngine,
                                                                      this.script,
                                                                      String.class),
                              false);
    }

    /**
     * Create a new invokeable instance of the script
     * 
     * @return a IRubyObject
     */
public RubyScriptInstance createScriptInstance(Map<String, Object> context, String rubyClassName) {
        if ( rubyClassName == null ) {
            return new RubyScriptInstance(rubyEngine.evalScript(script), responseClasses);
        }
        else {
            IRubyObject rubyObject = rubyEngine.evalScript(rubyClassName + NEW);
            
            Iterator<String> keyIterator = context.keySet().iterator();
            String key = null;
            Object value = null;
            while (  keyIterator.hasNext()) {
                key = keyIterator.next();
                value = JavaUtil.convertJavaToRuby(rubyEngine, 
                                                   context.get(key), 
                                                   context.get(key).getClass());
            
                JavaEmbedUtils.invokeMethod(rubyEngine, 
                                            rubyObject,
                                            key + EQUAL, 
                                            new Object[]{value}, null);
            }
            
            return new RubyScriptInstance(rubyObject, responseClasses);
        }
    }
    public String getScript() {
        return script;
    }

    public String getScriptName() {
        return scriptName;
    }

    public Map<String, Class> getResponseClasses() {
        return responseClasses;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Set the Java type of a response value. JavaScript is dynamically typed so Rhino cannot always work out what the intended Java type of a
     * response should be, for example should the statement "return 42" be a Java int, or Integer or Double etc. When Rhino can't determine the type
     * it will default to returning a String, using this method enables overriding the Rhino default to use a specific Java type.
     */
    public void setResponseClass(String functionName, Class responseClasses) {
        this.responseClasses.put(functionName,
                                 responseClasses);
    }

    public RubySCAConfig getSCAConfig() {
        return new RubySCAConfig(rubyEngine.getGlobalVariables());
    }

    public void setScript(String script) {
        this.script = script;
    }

    public IRuby getRubyEngine() {
        return rubyEngine;
    }

    public void setRubyEngine(IRuby rubyEngine) {
        this.rubyEngine = rubyEngine;
    }

}
