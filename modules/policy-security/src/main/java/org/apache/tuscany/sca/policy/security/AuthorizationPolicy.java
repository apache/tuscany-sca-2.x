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

package org.apache.tuscany.sca.policy.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.policy.Policy;

/**
 * Models the SCA Implementatatoin Security Policy Assertion for Athorization
 * 
 * @version $Rev$ $Date$
 */
public abstract class AuthorizationPolicy implements Policy {
    private List<String> roleNames = null;

    public enum AcessControl {
        PERMIT_ALL, DENY_ALL, ALLOW
    };

    public AuthorizationPolicy(AcessControl accessLevel) {
        this.accessLevel = accessLevel;
    }

    private AcessControl accessLevel;

    public AcessControl getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AcessControl accessLevel) {
        this.accessLevel = accessLevel;
    }

    public List<String> getRoleNames() {
        if (accessLevel == AcessControl.ALLOW && roleNames == null) {
            roleNames = new ArrayList<String>();
        }
        return roleNames;
    }

    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean unresolved) {
    }
}
