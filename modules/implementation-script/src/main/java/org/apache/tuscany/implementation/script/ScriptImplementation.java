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
package org.apache.tuscany.implementation.script;

import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.impl.ComponentTypeImpl;

/**
 * Represents a Script implementation.
 */
public class ScriptImplementation  extends ComponentTypeImpl implements Implementation {

    private String scriptName;
    private String scriptSrc;

    public String getName() {
        return scriptName;
    }

    public void setName(String scriptName) {
        if (!isUnresolved()) {
            throw new IllegalStateException();
        }
        this.scriptName = scriptName;
    }

    public String getScriptSrc() {
        return scriptSrc;
    }

    public void setScriptSrc(String scriptSrc) {
        this.scriptSrc = scriptSrc;
    }

    @Override
    public int hashCode() {
        return String.valueOf(getName()).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        else if (obj instanceof ScriptImplementation && getName().equals(((ScriptImplementation)obj).getName()))
             return true;
        else
            return false;
    }
}
