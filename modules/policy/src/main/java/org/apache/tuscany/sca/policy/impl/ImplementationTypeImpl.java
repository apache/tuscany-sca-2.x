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

import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;

/**
 * Concrete implementation for a Implementation Type
 * 
 */
public class ImplementationTypeImpl implements IntentAttachPointType {
    private List<Intent> alwaysProvides = new ArrayList<Intent>();
    private List<Intent> mayProvides = new ArrayList<Intent>();
    private QName typeName;
    private boolean unResolved = true;
    
    public List<Intent> getAlwaysProvidedIntents() {
        return alwaysProvides;
    }

    public List<Intent> getMayProvideIntents() {
        return mayProvides;
    }

    public QName getName() {
        return typeName;
    }

    public void setName(QName type) {
        this.typeName = type;
    }
    
    public boolean isUnresolved() {
        return unResolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unResolved = unresolved;
    }
}
