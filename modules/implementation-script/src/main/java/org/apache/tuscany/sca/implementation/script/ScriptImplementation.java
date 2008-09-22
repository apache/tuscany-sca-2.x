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
package org.apache.tuscany.sca.implementation.script;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;

/**
 * Represents a Script implementation.
 *
 * @version $Rev$ $Date$
 */
public class ScriptImplementation implements Implementation {

    private String uri;
    private String language;
    private List<Property> properties = new ArrayList<Property>();
    private List<Reference> references = new ArrayList<Reference>();
    private List<Service> services = new ArrayList<Service>();
    private String location;
    private boolean unresolved;
    
    public ScriptImplementation() {
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

    public ConstrainingType getConstrainingType() {
        // The script implementation does not support constrainingTypes
        return null;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<Service> getServices() {
        return services;
    }
    
    public List<Reference> getReferences() {
        return references;
    }

    public String getURI() {
        return uri;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        // The script implementation does not support constrainingTypes
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }    

    @Override
    public String toString() {
        return "Script : " + getURI(); 
    }
}
