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

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.assembly.Wire;
import org.apache.tuscany.assembly.util.Visitor;

public class CompositeImpl extends ComponentTypeImpl implements Composite {
    private List<Component> components = new ArrayList<Component>();
    private List<Composite> includes = new ArrayList<Composite>();
    private QName name;
    private List<Wire> wires = new ArrayList<Wire>();
    private boolean autowire;
    private boolean local = true;
    
    /**
     * Constructs a new composite.
     */
    public CompositeImpl() {
    }
    
    /**
     * Copy constructor.
     * @param other
     */
    public CompositeImpl(Composite other) {
        super(other);
        for (Component component: other.getComponents()) {
            components.add(new ComponentImpl(component));
        }
        getServices().clear();
        for (Service service: other.getServices()) {
            getServices().add(new CompositeServiceImpl((CompositeService)service));
        }
        getReferences().clear();
        for (Reference reference: other.getReferences()) {
            getReferences().add(new CompositeReferenceImpl((CompositeReference)reference));
        }
        for (Property property: other.getProperties()) {
            getProperties().add(new PropertyImpl(property));
        }
        name = other.getName();
        for (Wire wire: other.getWires()) {
            wires.add(new WireImpl(wire));
        }
        autowire = other.isAutowire();
        local = other.isLocal();
    }

    public List<Component> getComponents() {
        return components;
    }

    public List<Composite> getIncludes() {
        return includes;
    }

    public QName getName() {
        return name;
    }

    public List<Wire> getWires() {
        return wires;
    }

    public boolean isAutowire() {
        return autowire;
    }

    public boolean isLocal() {
        return local;
    }

    public void setAutowire(boolean autowire) {
        this.autowire = autowire;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public void setName(QName name) {
        this.name = name;
    }
    
    @Override
    public boolean accept(Visitor visitor) {
        boolean result = super.accept(visitor);
        if (!result) {
            return false;
        }
        
        for (Wire wire: wires) {
            if (!wire.accept(visitor))
                return false;
        }
        return true;
    }
    
    public Composite copy() {
        CompositeImpl copy = new CompositeImpl(this);
        return copy;
    }

    @Override
    public int hashCode() {
        return String.valueOf(getName()).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Composite) {
            if (getName() != null) {
                return getName().equals(((Composite)obj).getName());
            } else {
                return ((Composite)obj).getName() == null;
            }
        } else {
            return false;
        }
    }
}
