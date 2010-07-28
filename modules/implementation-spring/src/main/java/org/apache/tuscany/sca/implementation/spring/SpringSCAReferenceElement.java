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

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Represents a <sca:reference> element in a Spring application-context
 * - this has id and className attributes
 * - plus zero or more property elements as children
 *
 * @version $Rev$ $Date$
 */
public class SpringSCAReferenceElement {

    private String name;
    private String type;
    private String defaultBean;
    private List<Intent> intents = new ArrayList<Intent>();
    private List<PolicySet> policySets = new ArrayList<PolicySet>();

    public SpringSCAReferenceElement(String name, String type) {
        this.name = name;
        this.type = type;
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

    public void setDefaultBean(String defaultBean) {
        this.defaultBean = defaultBean;
    }

    public String getDefaultBean() {
        return defaultBean;
    }

    public List<Intent> getRequiredIntents() {
        return intents;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

} // end class SpringSCAReferenceElement
