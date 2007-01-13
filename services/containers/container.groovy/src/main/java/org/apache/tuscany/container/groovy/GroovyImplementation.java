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
package org.apache.tuscany.container.groovy;

import org.apache.tuscany.spi.model.AtomicImplementation;

/**
 * Model object for a Groovy implementation.
 */
public class GroovyImplementation extends AtomicImplementation<GroovyComponentType> {

    //the Groovy source to be executed
    private String script;
    
    // the application class loader, we need this to construct a GroovyClassLoader in GroovyComponentBuilder
    private ClassLoader applicationLoader;
    
    // TODO , Suggest to change the current script to scriptContent, acutally, we need script file name to get component side file name.
    private String scriptResourceName;
    
    public String getScriptResourceName() {
        return scriptResourceName;
    }

    public void setScriptResourceName(String scriptResourceName) {
        this.scriptResourceName = scriptResourceName;
    }

    /**
     * Return Application class loader to be executed.
     */
    public ClassLoader getApplicationLoader() {
        return applicationLoader;
    }

    /**
     * Sets the Application class loader to be executed.
     */
    public void setApplicationLoader(ClassLoader applicationLoader) {
        this.applicationLoader = applicationLoader;
    }

    /**
     * Returns the Groovy source to be executed.
     */
    public String getScript() {
        return script;
    }

    /**
     * Sets the Groovy source to be executed.
     */
    public void setScript(String script) {
        this.script = script;
    }

}
