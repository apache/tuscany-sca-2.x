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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.sdo.util.SDOUtil;

import commonj.sdo.Type;

/**
 * An implementation of Property.
 */
public class PropertyImpl extends ExtensibleImpl implements Property {
    
    private Object defaultValue; 
    private String name;
    private boolean many;
    private boolean required;
    private Class<?> type;
    
    private Type sdoType;

    /**
     * Constructor
     */
    protected PropertyImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#getDefaultValue()
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#getType()
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#isMany()
     */
    public boolean isMany() {
        return many;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#isRequired()
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#setDefaultValue(java.lang.Object)
     */
    public void setDefaultValue(Object value) {
        defaultValue=value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#setMany(boolean)
     */
    public void setMany(boolean value) {
        checkNotFrozen();
        many=value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#setName(java.lang.String)
     */
    public void setName(String value) {
        checkNotFrozen();
        name=value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#setRequired(boolean)
     */
    public void setRequired(boolean value) {
        checkNotFrozen();
        required=value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Property#setType(java.lang.Class)
     */
    public void setType(Class<?> value) {
        checkNotFrozen();
        type=value;
    }

    /**
     * @param sdoType The sdoType to set.
     */
    public void setSDOType(Type sdoType) {
        checkNotFrozen();
        this.sdoType = sdoType;
    }
    
    /**
     * @return Returns the sdoType.
     */
    public Type getSDOType() {
        return sdoType;
    }
    
    private static final Map<Class, Type> typeMapping=new HashMap<Class, Type>();
    
    static {
        typeMapping.put(BigDecimal.class, SDOUtil.getXSDSDOType("decimal"));
        typeMapping.put(BigInteger.class, SDOUtil.getXSDSDOType("integer"));
        typeMapping.put(boolean.class, SDOUtil.getXSDSDOType("boolean"));
        typeMapping.put(Boolean.class, SDOUtil.getXSDSDOType("boolean"));
        typeMapping.put(byte.class, SDOUtil.getXSDSDOType("byte"));
        typeMapping.put(Byte.class, SDOUtil.getXSDSDOType("Byte"));
        typeMapping.put(byte[].class, SDOUtil.getXSDSDOType("hexBinary"));
        typeMapping.put(char.class, SDOUtil.getXSDSDOType("string"));
        typeMapping.put(Character.class, SDOUtil.getXSDSDOType("string"));
        typeMapping.put(Date.class, SDOUtil.getXSDSDOType("dateTime"));
        typeMapping.put(double.class, SDOUtil.getXSDSDOType("double"));
        typeMapping.put(Double.class, SDOUtil.getXSDSDOType("double"));
        typeMapping.put(float.class, SDOUtil.getXSDSDOType("float"));
        typeMapping.put(Float.class, SDOUtil.getXSDSDOType("float"));
        typeMapping.put(int.class, SDOUtil.getXSDSDOType("int"));
        typeMapping.put(Integer.class, SDOUtil.getXSDSDOType("int"));
        typeMapping.put(long.class, SDOUtil.getXSDSDOType("long"));
        typeMapping.put(Long.class, SDOUtil.getXSDSDOType("long"));
        typeMapping.put(short.class, SDOUtil.getXSDSDOType("short"));
        typeMapping.put(Short.class, SDOUtil.getXSDSDOType("short"));
        typeMapping.put(String.class, SDOUtil.getXSDSDOType("string"));
    }

    /*
     * @see org.apache.tuscany.model.assembly.impl.ExtensibleImpl#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);

        // Get the SDO type corresponding to the property's Java type
        if (sdoType==null) {
            sdoType=typeMapping.get(type);
        }
    }
}
