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

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.policy.PolicyExpression;

public class PolicyExpressionImpl implements PolicyExpression {
    private QName name;
    private Object policy;
    private boolean unresolved = true;

    protected PolicyExpressionImpl() {
    }

    public QName getName() {
        return name;
    }

    public <T> T getPolicy() {
        return (T)policy;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public <T> void setPolicy(T policy) {
        this.policy = policy;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
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
        PolicyExpressionImpl other = (PolicyExpressionImpl)obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
