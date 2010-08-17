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
package org.apache.tuscany.sca.implementation.spring;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a <constructor-arg> element in a Spring application-context
 * - this has ref attribute
 * 
 * @version $Rev$ $Date$
 */
public class SpringConstructorArgElement {

    private String type;
    private int autoIndex = -1;
    private int index = -1;
    private List<String> refs = new ArrayList<String>();
    private List<String> values = new ArrayList<String>();

    public SpringConstructorArgElement() {
        
    }
    
    public SpringConstructorArgElement(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public List<String> getRefs() {
        return this.refs;
    }

    public void addRef(String ref) {
        this.refs.add(ref);
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getAutoIndex() {
        return this.autoIndex;
    }

    public void setAutoIndex(int index) {
        this.autoIndex = index;
    }

    public List<String> getValues() {
        return this.values;
    }

    public void addValue(String value) {
        this.values.add(value);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SpringConstructorArgElement [type=").append(type).append(", autoIndex=").append(autoIndex)
            .append(", index=").append(index).append(", refs=").append(refs).append(", values=").append(values)
            .append("]");
        return builder.toString();
    }
}
