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
package org.apache.tuscany.sca.implementation.script.impl;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.Base;
import org.apache.tuscany.sca.assembly.impl.ImplementationImpl;
import org.apache.tuscany.sca.implementation.script.ScriptImplementation;

/**
 * Represents a Script implementation.
 *
 * @version $Rev$ $Date$
 */
public class ScriptImplementationImpl extends ImplementationImpl implements ScriptImplementation {
    public static final QName TYPE = new QName(Base.SCA11_TUSCANY_NS, "implementation.script");

    private String uri;
    private String language;
    private String location;
    
    public ScriptImplementationImpl() {
        super(TYPE);
    }

    public String getScript() {
        return uri;
    }

    public void setScript(String uri) {
        this.uri = uri;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "Script : " + getURI(); 
    }
}
