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

package org.apache.tuscany.sca.binding.jms.impl;

public class BindingProperty {
    
    private String name;
    private String type;
    private Object value;
    
    public BindingProperty(String name, String type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
    
    public Object getValue() {
        return value;
    }
    
    @Override
    public boolean equals( Object object ) {
        return ( object instanceof BindingProperty ) && equals( (BindingProperty) object );
    }

    /**
     * Test whether this and another Binding Property are equal.
     * @param property
     * @return true if all fields of property match.
     */
    public boolean equals( BindingProperty property ) {
        if ( name == null && property.getName() != null )
            return false;
        else if ( !name.equals( property.getName() ))
            return false;
        else if ( type == null && property.getType() != null )
            return false;
        else if ( !type.equals( property.getType() ))
            return false;
        else if ( value == null && property.getValue() != null )
            return false;
        else if ( !value.equals( property.getValue() ))
                return false;
        return true;
    }
}
