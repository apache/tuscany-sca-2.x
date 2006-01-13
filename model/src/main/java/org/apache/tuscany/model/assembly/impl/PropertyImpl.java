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

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Property;

/**
 * An implementation of the model object '<em><b>Property</b></em>'.
 */
public class PropertyImpl extends org.apache.tuscany.model.assembly.sdo.impl.PropertyImpl implements Property {
    /**
     * Constructor
     */
    protected PropertyImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.PropertyImpl#getName()
     */
    public String getName() {
        return super.getName();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.PropertyImpl#setName(java.lang.String)
     */
    public void setName(String newName) {
        super.setName(newName);
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.PropertyImpl#getDefault()
     */
    public String getDefault() {
        return super.getDefault();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.PropertyImpl#setDefault(java.lang.String)
     */
    public void setDefault(String newDefault) {
        super.setDefault(newDefault);
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.PropertyImpl#isMany()
     */
    public boolean isMany() {
        return super.isMany();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.PropertyImpl#setMany(boolean)
     */
    public void setMany(boolean newMany) {
        super.setMany(newMany);
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.PropertyImpl#isRequired()
     */
    public boolean isRequired() {
        return super.isRequired();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.PropertyImpl#setRequired(boolean)
     */
    public void setRequired(boolean newRequired) {
        super.setRequired(newRequired);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#getType_()
     */
    public Object getType_() {
        return super.getType_();
    }

    public void setType(Object value) {
        super.setType(value);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#freeze()
     */
    public void freeze() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        return AssemblyModelVisitorHelperImpl.accept(this, visitor);
    }

} //PropertyImpl
