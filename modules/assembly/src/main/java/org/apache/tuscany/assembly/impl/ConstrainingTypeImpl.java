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
package org.apache.tuscany.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.assembly.AbstractProperty;
import org.apache.tuscany.assembly.AbstractReference;
import org.apache.tuscany.assembly.AbstractService;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.assembly.util.Visitor;
import org.apache.tuscany.policy.Intent;

/**
 * Represents a constraining type.
 * 
 * @version $Rev$ $Date$
 */
public class ConstrainingTypeImpl extends BaseImpl implements ConstrainingType {
    private QName name;
    private List<AbstractProperty> properties = new ArrayList<AbstractProperty>();
    private List<AbstractReference> references = new ArrayList<AbstractReference>();
    private List<AbstractService> services = new ArrayList<AbstractService>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    
    /**
     * Constructs a new ConstrainingType
     */
    protected ConstrainingTypeImpl() {
    }

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public List<AbstractProperty> getProperties() {
        return properties;
    }

    public List<AbstractReference> getReferences() {
        return references;
    }

    public List<AbstractService> getServices() {
        return services;
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public boolean accept(Visitor visitor) {
        if (!super.accept(visitor)) {
            return false;
        }
        for (AbstractProperty property : properties) {
            if (!visitor.visit(property)) {
                return false;
            }
        }
        for (AbstractReference reference : references) {
            if (!visitor.visit(reference)) {
                return false;
            }
        }
        for (AbstractService service : services) {
            if (!visitor.visit(service)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return String.valueOf(getName()).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ConstrainingType) {
            if (getName() != null) {
                return getName().equals(((ConstrainingType)obj).getName());
            } else {
                return ((ConstrainingType)obj).getName() == null;
            }
        } else {
            return false;
        }
    }
}
