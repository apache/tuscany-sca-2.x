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

import org.apache.tuscany.sca.policy.ExtensionType;
import org.apache.tuscany.sca.policy.Intent;

/**
 * Represents a policy intent.
 * 
 * @version $Rev$ $Date$
 */
public class IntentImpl implements Intent {

    private QName name;
    private Type type;
    private List<ExtensionType> constrainedTypes = new ArrayList<ExtensionType>();
    private String description;
    private List<Intent> qualifiedIntents = new ArrayList<Intent>();
    private Intent defaultQualifiedIntent;
    private Intent parent;
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<Intent> excludedIntents = new ArrayList<Intent>();
    private boolean mutuallyExclusive;
    private boolean unresolved = true;

    protected IntentImpl() {
    }

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public List<ExtensionType> getConstrainedTypes() {
        return constrainedTypes;
    }

    public void setConstrainedTypes(List<ExtensionType> constrainedTypes) {
        this.constrainedTypes = constrainedTypes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Intent getQualifiableIntent() {
        return parent;
    }

    public void setQualifiableIntent(Intent parent) {
        this.parent = parent;
    }

    public List<Intent> getQualifiedIntents() {
        return qualifiedIntents;
    }

    public void setQualifiedIntents(List<Intent> qualifiedIntents) {
        this.qualifiedIntents = qualifiedIntents;
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public void setRequiredIntents(List<Intent> requiredIntents) {
        this.requiredIntents = requiredIntents;
    }

    public List<Intent> getExcludedIntents() {
        return excludedIntents;
    }

    public void setExcludedIntents(List<Intent> excludedIntents) {
        this.excludedIntents = excludedIntents;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isMutuallyExclusive() {
        return mutuallyExclusive;
    }

    public void setMutuallyExclusive(boolean mutuallyExclusive) {
        this.mutuallyExclusive = mutuallyExclusive;
    }

    public Intent getDefaultQualifiedIntent() {
        return defaultQualifiedIntent;
    }

    public void setDefaultQualifiedIntent(Intent defaultQualifiedIntent) {
        this.defaultQualifiedIntent = defaultQualifiedIntent;
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
        IntentImpl other = (IntentImpl)obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
    public String toString() {
        return String.valueOf(name);
    }
}
