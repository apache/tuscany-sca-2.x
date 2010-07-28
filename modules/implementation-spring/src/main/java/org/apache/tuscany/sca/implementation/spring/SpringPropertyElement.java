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
 * Represents a <property> element in a Spring application-context
 * - this has name and ref attributes
 * 
 * @version $Rev$ $Date$
 */
public class SpringPropertyElement {

    private String name;
    private List<String> refs = new ArrayList<String>();
    private List<String> values = new ArrayList<String>();

    public SpringPropertyElement(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getRefs() {
        return this.refs;
    }

    public void addRef(String ref) {
        this.refs.add(ref);
    }

    public List<String> getValues() {
        return this.values;
    }

    public void addValue(String value) {
        this.values.add(value);
    }

} // end class SpringPropertyElement
