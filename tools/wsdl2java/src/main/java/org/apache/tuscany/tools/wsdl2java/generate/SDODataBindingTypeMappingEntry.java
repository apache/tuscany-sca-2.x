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
package org.apache.tuscany.tools.wsdl2java.generate;

import java.util.List;

/**
 * This represents a type mapping entry, used by the JavaInterfaceEmitter to generate
 * method signatures.  
 */
public class SDODataBindingTypeMappingEntry {

    private final boolean anonymous;
    private final List<String> propertyClassNames;
    private final String className;

    public SDODataBindingTypeMappingEntry(String className, boolean anonymous, List<String> propertyClassNames) {
        this.className = className;
        this.anonymous = anonymous;
        this.propertyClassNames = propertyClassNames;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getPropertyClassNames() {
        return propertyClassNames;
    }

}
