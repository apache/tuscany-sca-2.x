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
package org.apache.tuscany.sca.implementation.spring.elements.tie;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;


/**
 * Represents a <sca:service> element in a Spring application-context
 * - this has id and className attributes
 * - plus zero or more property elements as children
 *
 * @version $Rev$ $Date$
 */
public class SpringSCAServiceElement {

    private String name;
    private String type;
    private String target;

    private List<QName> intentNames = new ArrayList<QName>();
    private List<QName> policySetNames = new ArrayList<QName>();


    public SpringSCAServiceElement(String name, String target) {
        this.name = name;
        this.target = target;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }
    
    public List<QName> getIntentNames() {
        return intentNames;
    }

    public List<QName> getPolicySetNames() {
        return policySetNames;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SpringSCAServiceElement [name=").append(name).append(", type=").append(type)
            .append(", target=").append(target).append(", intentNames=").append(intentNames)
            .append(", policySetNames=").append(policySetNames).append("]");
        return builder.toString();
    }

} // end class SpringSCAServiceElement
