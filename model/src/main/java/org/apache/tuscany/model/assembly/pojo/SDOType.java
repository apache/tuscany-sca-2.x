/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.model.assembly.pojo;

import java.util.List;

import commonj.sdo.Property;
import commonj.sdo.Type;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class SDOType implements Type {

    private String name;

    private String uri;
    
    private boolean instance;
    
    private Class instanceClass;
    
    private List properties;
    
    // ----------------------------------
    // Constructors
    // ----------------------------------

    public SDOType(String name, String uri, boolean isInstance, Class instanceClass, List properties) {
        assert(name != null): "Name was null";
        assert(uri != null):"Uri was null";
        assert(instanceClass != null ): "Instance class was null";
        assert(properties != null):"Properties collection was null";
        this.name=name;
        this.uri = uri;
        this.instance=isInstance;
        this.instanceClass=instanceClass;
        this.properties=properties;
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public String getName() {
        return name;
    }

    public String getURI() {
        return uri;
    }

    public Class getInstanceClass() {
        return instanceClass;
    }

    public boolean isInstance(Object arg0) {
        return instance;
    }

    public List getProperties() {
        return properties;
    }

    public Property getProperty(String arg0) {
        return null;
    }

}

