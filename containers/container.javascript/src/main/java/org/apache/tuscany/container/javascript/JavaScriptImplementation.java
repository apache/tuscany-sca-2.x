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
package org.apache.tuscany.container.javascript;

import org.apache.tuscany.spi.model.AtomicImplementation;

/**
 * Model object for a JavaScript implementation.
 */
public class JavaScriptImplementation extends AtomicImplementation<JavaScriptComponentType> {

    private String script;
    private String scriptName;
    private ClassLoader cl;
    private JavaScriptComponentType componentType;

    /**
     * Returns the JavaScript source to be executed.
     */
    public String getScript() {
        return script;
    }

    /**
     * Sets the JavaScript source to be executed.
     */
    public void setScript(String script) {
        this.script = script;
    }

    public JavaScriptComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(JavaScriptComponentType componentType) {
        this.componentType =  componentType;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public ClassLoader getClassLoader() {
        return cl;
    }

    public void setClassLoader(ClassLoader cl) {
        this.cl = cl;
    }
}
