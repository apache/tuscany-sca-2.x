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
package org.apache.tuscany.container.script;

import org.apache.tuscany.spi.model.AtomicImplementation;

//import org.apache.tuscany.container.script.helper.ScriptFactory;

/**
 * Model object for a script implementation.
 */
public class ScriptImplementation extends AtomicImplementation<ScriptComponentType> {

    private String resourceName;
    private String className;
    private String scriptSource;
    private String scriptName;
    private ClassLoader classLoader;

//    private ScriptFactory scriptFactory;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getScriptSource() {
        return scriptSource;
    }

    public void setScriptSource(String scriptSource) {
        this.scriptSource = scriptSource;
    }

//    public ScriptFactory getScriptFactory() {
//        return scriptFactory;
//    }
//
//    public void setScriptFactory(ScriptFactory scriptFactory) {
//        this.scriptFactory = scriptFactory;
//    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
//    public ScriptFactory getScriptInstanceFactory() {
//        return scriptFactory;
//    }
//
//    public void setScriptInstanceFactory(ScriptFactory scriptFactory) {
//        this.scriptFactory = scriptFactory;
//    }
}
