/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.model.assembly.impl;

import org.eclipse.emf.ecore.sdo.impl.EDataObjectImpl;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.Property;

/**
 */
public class ConfiguredPropertyImpl extends EDataObjectImpl implements ConfiguredProperty {
    private Component component;
    private Property property;
    private Object value;

    /**
     * Constructor
     */
    protected ConfiguredPropertyImpl() {
        super();
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredProperty#getComponent()
     */
    public Component getComponent() {
        return component;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredProperty#setComponent(org.apache.tuscany.model.assembly.Component)
     */
    public void setComponent(Component component) {
        this.component = component;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredProperty#getProperty()
     */
    public Property getProperty() {
        return property;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredProperty#setProperty(org.apache.tuscany.model.assembly.Property)
     */
    public void setProperty(Property property) {
        this.property = property;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredProperty#getValue()
     */
    public Object getValue() {
        return value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredProperty#setValue(java.lang.Object)
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        return AssemblyModelVisitorHelperImpl.accept(this, visitor);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#freeze()
     */
    public void freeze() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
    }

}
