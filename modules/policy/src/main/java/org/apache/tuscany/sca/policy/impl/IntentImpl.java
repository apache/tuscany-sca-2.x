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

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.policy.Intent;

/**
 * Represents a policy intent.
 * 
 * @version $Rev$ $Date$
 */
public class IntentImpl implements Intent {

    private static final String QUALIFIED_SEPARATOR = ".";
    private static final String DOMAIN_SEPARATOR = ".";
    private QName name;
    private List<Operation> operations = new ArrayList<Operation>();
    private List<QName> constrains = new ArrayList<QName>();
    private String description;
    private List<Intent> qualifiedIntents = new ArrayList<Intent>();;
    // private List<Intent> requiredIntents = new ArrayList<Intent>();;
    private boolean unresolved = true;
    private String domain;
    private String[] qualifiedNames;

    protected IntentImpl() {
    }

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
        String iname = name.getLocalPart();
        int domainIdx = iname.indexOf(DOMAIN_SEPARATOR);
        if (domainIdx > -1) {
            domain = iname.substring(0, domainIdx);
            String qualifNamesStr = iname.substring(domainIdx + 1);
            String pattern = "\\" + QUALIFIED_SEPARATOR;
            qualifiedNames = qualifNamesStr.split(pattern);
        } else
            domain = iname;
    }

    public String getDomain() {
        return domain;
    }

    public String[] getQualifiedNames() {
        String[] results = new String[qualifiedNames.length];
        System.arraycopy(qualifiedNames, 0, results, 0, qualifiedNames.length);
        return results;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public List<QName> getConstrains() {
        return constrains;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Intent> getQualifiedIntents() {
        return qualifiedIntents;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        QName intentName = getName();
        result = prime * result + ((intentName == null) ? 0 : intentName.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof IntentImpl)) {
            return false;
        }
        final IntentImpl other = (IntentImpl)obj;
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }
        return true;
    }

    public String toString() {
        return String.valueOf(getName());
    }
}
