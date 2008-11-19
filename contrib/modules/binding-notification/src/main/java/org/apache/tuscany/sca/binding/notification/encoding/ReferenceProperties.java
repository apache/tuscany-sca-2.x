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
package org.apache.tuscany.sca.binding.notification.encoding;

import java.util.ArrayList;
import java.util.List;

/**
 * @version $Rev$ $Date$
 */
public class ReferenceProperties implements EncodingObject {
    
    private List<EncodingObject> properties;
    
    public List<EncodingObject> getProperties() {
        return properties;
    }

    public void addProperty(EncodingObject property) {
        if(this.properties == null) {
            this.properties = new ArrayList<EncodingObject>();
        }
        this.properties.add(property);
    }
    
    @SuppressWarnings("unchecked")
    public <E extends EncodingObject> E getProperty(Class<E> propertyType) {
        if (this.properties == null) {
            return null;
        }
        for (EncodingObject eo : properties) {
            if (propertyType.isInstance(eo)) {
                return (E)eo;
            }
        }
        return null;
    }
}
