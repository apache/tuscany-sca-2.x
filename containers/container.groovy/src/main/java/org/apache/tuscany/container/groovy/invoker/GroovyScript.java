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
package org.apache.tuscany.container.groovy.invoker;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * Models a Groovy script.
 */
public class GroovyScript {

    // Script to run.
    private String script;

    /**
     * Initializes the script name.
     *
     * @param script Full path to the script.
     */
    public GroovyScript(String script) {
        this.script = script;
    }

    /**
     * Executes the script.
     *
     * @param methodName Name of the method to be executed.
     * @param args       Arguments to the method.
     * @return Result of invocation.
     * @throws CompilationFailedException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Object runScript(String methodName, Object[] args) throws CompilationFailedException, InstantiationException, IllegalAccessException {
        ClassLoader parent = getClass().getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class groovyClass = loader.parseClass(parent.getResourceAsStream(script));
        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
        return groovyObject.invokeMethod(methodName, args);
    }

}
