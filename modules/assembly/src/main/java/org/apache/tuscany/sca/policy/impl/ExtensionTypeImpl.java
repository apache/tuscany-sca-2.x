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
 * Concrete implementation for a BindingType
 *
 * @version $Rev$ $Date$
 */
public class ExtensionTypeImpl implements ExtensionType {

    private List<Intent> alwaysProvides = new ArrayList<Intent>();
    private List<Intent> mayProvide = new ArrayList<Intent>();
    private QName typeName;
    private boolean unResolved = true;

    protected ExtensionTypeImpl() {

    }

    public List<Intent> getAlwaysProvidedIntents() {
        return alwaysProvides;
    }

    public List<Intent> getMayProvidedIntents() {
        return mayProvide;
    }

    public QName getType() {
        return typeName;
    }

    public void setType(QName type) {
        this.typeName = type;
    }

    public boolean isUnresolved() {
        return unResolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unResolved = unresolved;
    }

    @Override
    public int hashCode() {
        return String.valueOf(getType()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ExtensionTypeImpl) {
            if (getType() != null) {
                return getType().equals(((ExtensionTypeImpl)obj).getType());
            } else {
                return ((ExtensionTypeImpl)obj).getType() == null;
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return (this.typeName != null) ? getType().toString() : "null";
    }

    public QName getBaseType() {
        return null;
    }
};
