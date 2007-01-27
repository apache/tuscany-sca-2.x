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
import java.util.Map;

import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * An invokeable instance of a JavaScript script.
 */
public class RubyScriptInstance {

    private IRubyObject rubyInstance;

    private Map<String, Class> responseClasses;

    public RubyScriptInstance(IRubyObject rubyInstance, Map<String, Class> responseClasses) {
        this.rubyInstance = rubyInstance;
        this.responseClasses = responseClasses;
        if (this.responseClasses == null) {
            this.responseClasses = new HashMap<String, Class>();
        }
    }

    public Object invokeFunction(String functionName, Object[] args, Class returnType) {
        Object[] rubyArgs = RubyUtils.fromJavaToRuby(rubyInstance.getRuntime(), args);
        
        Object rubyResponse = JavaEmbedUtils.invokeMethod(rubyInstance.getRuntime(),
                                                           rubyInstance,
                                                           functionName,
                                                           rubyArgs,
                                                           returnType);
        Object response = RubyUtils.fromRubyToJava(rubyInstance.getRuntime(),
                                                   returnType, 
                                                   rubyResponse);
        return response;
    }
    
    
    

}
