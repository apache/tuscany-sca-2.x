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
package org.apache.tuscany.sca.binding.ejb.java2idl;

import java.io.Externalizable;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.osoa.sca.ServiceRuntimeException;

/**
 * Value Type.
 */
public class ValueType extends ContainerType {

    private static WorkCache cache = new WorkCache(ValueType.class);

    /**
     * Type of our superclass, of null if our superclass is java.lang.Object.
     */
    ValueType superType;
    /**
     * Flags that this is an abstract value.
     */
    private boolean abstractValue = false;
    /**
     * Flags that this implements <code>java.io.Externalizable</code>.
     */
    private boolean externalizable = false;
    /**
     * Flags that this has a <code>writeObject()</code> method.
     */
    private boolean hasWriteObjectMethod = false;
    /**
     * The <code>serialPersistentFields of the value, or <code>null</code>
     *  if the value does not have this field.
     */
    private ObjectStreamField[] serialPersistentFields;
    /**
     * The value members of this value class.
     */
    private ValueMemberType[] members;

    public static ValueType getValueType(Class cls) {
        return (ValueType)cache.getType(cls);
    }

    protected ValueType(Class cls) {
        super(cls);
    }

    protected void parse() {
        super.parse();
        if (javaClass == String.class)
            throw new IllegalArgumentException("Cannot parse java.lang.String here: It is a " + "special case."); // 1.3.5.11
        if (javaClass == Class.class)
            throw new IllegalArgumentException("Cannot parse java.lang.Class here: It is a " + "special case."); // 1.3.5.10
        if (Remote.class.isAssignableFrom(javaClass))
            throw new IDLViolationException("Value type " + javaClass.getName() + " cannot implement java.rmi.Remote.",
                                            "1.2.4");
        if (javaClass.getName().indexOf('$') != -1)
            throw new ServiceRuntimeException(javaClass.getName() + " is not supported (proxy or inner classes).");
        externalizable = Externalizable.class.isAssignableFrom(javaClass);
        if (!externalizable) {
            // Look for serialPersistentFields field.
            Field spf = null;
            try {
                spf = javaClass.getField("serialPersistentFields");
            } catch (NoSuchFieldException ex) {
                // ignore
            }
            if (spf != null) { // Right modifiers?
                int mods = spf.getModifiers();
                if (!Modifier.isFinal(mods) || !Modifier.isStatic(mods) || !Modifier.isPrivate(mods))
                    spf = null; // wrong modifiers
            }
            if (spf != null) { // Right type?
                Class type = spf.getType();
                if (type.isArray()) {
                    type = type.getComponentType();
                    if (type != ObjectStreamField.class)
                        spf = null; // Array of wrong type
                } else
                    spf = null; // Wrong type: Not an array
            }
            if (spf != null) {
                // We have the serialPersistentFields field
                // Get this constant
                try {
                    serialPersistentFields = (ObjectStreamField[])spf.get(null);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException("Unexpected IllegalException: " + ex.toString());
                }
                // Mark this in the fields array
                for (int i = 0; i < fields.length; ++i) {
                    if (fields[i] == spf) {
                        f_flags[i] |= F_SPFFIELD;
                        break;
                    }
                }
            }
            // Look for a writeObject Method
            Method wo = null;
            try {
                wo = javaClass.getMethod("writeObject", new Class[] {java.io.OutputStream[].class});
            } catch (NoSuchMethodException ex) {
                // ignore
            }
            if (wo != null) { // Right return type?
                if (wo.getReturnType() != Void.TYPE)
                    wo = null; // Wrong return type
            }
            if (wo != null) { // Right modifiers?
                int mods = spf.getModifiers();
                if (!Modifier.isPrivate(mods))
                    wo = null; // wrong modifiers
            }
            if (wo != null) { // Right arguments?
                Class[] paramTypes = wo.getParameterTypes();
                if (paramTypes.length != 1)
                    wo = null; // Bad number of parameters
                else if (paramTypes[0] != java.io.OutputStream.class)
                    wo = null; // Bad parameter type
            }
            if (wo != null) {
                // We have the writeObject() method.
                hasWriteObjectMethod = true;
                // Mark this in the methods array
                for (int i = 0; i < methods.length; ++i) {
                    if (methods[i] == wo) {
                        m_flags[i] |= M_WRITEOBJECT;
                        break;
                    }
                }
            }
        }
        // Map all fields not flagged constant or serialPersistentField.
        SortedSet m = new TreeSet(new ValueMemberComparator());
        for (int i = 0; i < fields.length; ++i) {
            if (f_flags[i] != 0)
                continue; // flagged
            int mods = fields[i].getModifiers();
            if (Modifier.isStatic(mods) || Modifier.isTransient(mods))
                continue; // don't map this
            ValueMemberType vma =
                new ValueMemberType(fields[i].getName(), fields[i].getType(), Modifier.isPublic(mods));
            m.add(vma);
        }
        members = new ValueMemberType[m.size()];
        members = (ValueMemberType[])m.toArray(members);
        // Get superclass analysis
        Class superClass = javaClass.getSuperclass();
        if (superClass == java.lang.Object.class)
            superClass = null;
        if (superClass == null)
            superType = null;
        else {
            superType = getValueType(superClass);
        }
        if (!Serializable.class.isAssignableFrom(javaClass))
            abstractValue = true;
        fixupCaseNames();
    }

    /**
     * Returns the superclass analysis, or null if this inherits from
     * java.lang.Object.
     */
    public ValueType getSuperType() {
        return superType;
    }

    /**
     * Returns true if this value is abstract.
     */
    public boolean isAbstractValue() {
        return abstractValue;
    }

    /**
     * Returns true if this value is custom.
     */
    public boolean isCustom() {
        return externalizable || hasWriteObjectMethod;
    }

    /**
     * Returns true if this value implements java.io.Externalizable.
     */
    public boolean isExternalizable() {
        return externalizable;
    }

    /**
     * Return the value members of this value class.
     */
    public ValueMemberType[] getMembers() {
        return (ValueMemberType[])members.clone();
    }

    /**
     * Analyse attributes. This will fill in the <code>attributes</code>
     * array. Here we override the implementation in ContainerType and create an
     * empty array, because for valuetypes we don't want to parse IDL attributes
     * or operations (as in "rmic -idl -noValueMethods").
     */
    protected void parseAttributes() {
        attributes = new AttributeType[0];
    }

    /**
     * Return a list of all the entries contained here.
     * 
     * @param entries The list of entries contained here. Entries in this list
     *            are subclasses of <code>AbstractType</code>.
     */
    protected ArrayList getContainedEntries() {
        ArrayList ret = new ArrayList(constants.length + attributes.length + members.length);
        for (int i = 0; i < constants.length; ++i)
            ret.add(constants[i]);
        for (int i = 0; i < attributes.length; ++i)
            ret.add(attributes[i]);
        for (int i = 0; i < members.length; ++i)
            ret.add(members[i]);
        return ret;
    }

    /**
     * A <code>Comparator</code> for the field ordering specified at the end
     * of section 1.3.5.6.
     */
    private static class ValueMemberComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            if (o1 == o2)
                return 0;
            ValueMemberType m1 = (ValueMemberType)o1;
            ValueMemberType m2 = (ValueMemberType)o2;
            boolean p1 = m1.getJavaClass().isPrimitive();
            boolean p2 = m2.getJavaClass().isPrimitive();
            if (p1 && !p2)
                return -1;
            if (!p1 && p2)
                return 1;
            return m1.getJavaName().compareTo(m2.getJavaName());
        }
    }
}
