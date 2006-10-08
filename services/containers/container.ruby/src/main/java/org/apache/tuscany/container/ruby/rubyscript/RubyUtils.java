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
package org.apache.tuscany.container.ruby.rubyscript;

import java.io.ByteArrayInputStream;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.jruby.IRuby;
import org.jruby.RubyObject;
import org.jruby.javasupport.JavaUtil;

/**
 * @author administrator
 *
 */
public class RubyUtils {
    public static Object fromRubyToJava(IRuby rubyEngine, Class reqArgType, Object rubyArg) {
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
    
    public static Object[] fromJavaToRuby(IRuby rubyEngine, Object[] arg) {
        Object[] jsArgs;
        if (arg == null) {
            jsArgs = new Object[0];
        }  else {
            jsArgs = new Object[arg.length];
            for (int i = 0; i < jsArgs.length; i++) {
                jsArgs[i] = fromJavaToRuby(rubyEngine, arg[i]);
            }
        }
        
        return jsArgs;
    }
    
    public static Object fromJavaToRuby(IRuby rubyEngine, Object javaObj)  {
        Object rubyObj = JavaUtil.convertJavaToRuby(rubyEngine, javaObj, javaObj.getClass());
        return rubyObj;
    }

}
