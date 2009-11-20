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
package org.apache.tuscany.sca.policy.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpression;

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentMap;
import org.apache.tuscany.sca.policy.PolicyExpression;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Represents a policy set.
 * 
 * @version $Rev$ $Date$
 */
public class PolicySetImpl implements PolicySet {

    private QName name;
    private String appliesTo;
    private String attachTo;
    private List<Intent> providedIntents = new ArrayList<Intent>();
    private List<PolicySet> referencedPolicySets = new ArrayList<PolicySet>();
    private boolean unresolved = true;

    private XPathExpression appliesToXPathExpression;
    private XPathExpression attachToXPathExpression;

    private List<IntentMap> intentMaps = new ArrayList<IntentMap>();
    private List<PolicyExpression> policies = new ArrayList<PolicyExpression>();

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public String getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(String appliesTo) {
        this.appliesTo = appliesTo;
    }

    public String getAttachTo() {
        return attachTo;
    }

    public void setAttachTo(String attachTo) {
        this.attachTo = attachTo;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    public XPathExpression getAppliesToXPathExpression() {
        return appliesToXPathExpression;
    }

    public void setAppliesToXPathExpression(XPathExpression appliesToXPathExpression) {
        this.appliesToXPathExpression = appliesToXPathExpression;
    }

    public XPathExpression getAttachToXPathExpression() {
        return attachToXPathExpression;
    }

    public void setAttachToXPathExpression(XPathExpression attachToXPathExpression) {
        this.attachToXPathExpression = attachToXPathExpression;
    }

    public List<IntentMap> getIntentMaps() {
        return intentMaps;
    }

    public List<Intent> getProvidedIntents() {
        return providedIntents;
    }

    public List<PolicySet> getReferencedPolicySets() {
        return referencedPolicySets;
    }

    public List<PolicyExpression> getPolicies() {
        return policies;
    }
    
    public String toString() {
        return String.valueOf(name);
    }    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PolicySetImpl other = (PolicySetImpl)obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
