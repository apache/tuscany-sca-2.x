/**
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
package org.apache.tuscany.container.rhino.rhino;

import java.util.Map;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.xml.XMLObject;

/**
 * Invokes a JavaScript/E4X function with argument and return values that may be E4X XML objects. 
 */
public class RhinoE4XScript extends RhinoScript {

    private E4XDataBinding dataBinding;

    public RhinoE4XScript(String scriptName, String script, Map context, ClassLoader cl, E4XDataBinding dataBinding) {
        super(scriptName, script, context, cl);
        this.dataBinding = dataBinding;
    }

    protected RhinoE4XScript(String scriptName, String script, Scriptable scriptScope, E4XDataBinding dataBinding) {
        super(scriptName, script, scriptScope);
        this.dataBinding = dataBinding;
    }

    /**
     * Turn args to JS objects and convert any OMElement to E4X XML
     */
    @Override
    protected Object[] processArgs(String functionName, Object[] args, Scriptable scope) {
        return new Object[] { dataBinding.toE4X(functionName, args, scope) };
    }

    /**
     * Unwrap and convert response converting any E4X XML into Java objects
     */
    @Override
    protected Object processResponse(String functionName, Object response, Class responseClass) {
        if (response instanceof XMLObject) {
            Object[] os = dataBinding.toObjects((XMLObject) response);
            if (os == null || os.length < 1) {
                return null;
            } else {
                return os[0];
            }
        } else {
            return super.processResponse(functionName, response, responseClass);
        }
    }

    @Override
    public RhinoE4XScript copy() {
        return new RhinoE4XScript(scriptName, script, scriptScope, dataBinding);
    }

}
