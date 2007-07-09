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

import static org.apache.tuscany.sca.binding.ejb.java2idl.IDLUtil.toHexString;

import java.io.Externalizable;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common base class of ValueType and InterfaceType.
 */
public abstract class ContainerType extends ClassType {
    protected static final Map<Class, ContainerType> PARSED_TYPES = new ConcurrentHashMap<Class, ContainerType>();
    /** Flags a method as overloaded. */
    protected static final byte M_OVERLOADED = 1;
    /** Flags a method as the accessor of a read-write property. */
    protected static final byte M_READ = 2;
    /** Flags a method as the mutator of a read-write property. */
    protected static final byte M_WRITE = 4;
    /** Flags a method as the accessor of a read-only property. */
    protected static final byte M_READONLY = 8;
    /** Flags a method as being inherited. */
    protected static final byte M_INHERITED = 16;
    /**
     * Flags a method as being the writeObject() method used for serialization.
     */
    protected static final byte M_WRITEOBJECT = 32;
    /** Flags a field as being a constant (public final static). */
    protected static final byte F_CONSTANT = 1;
    /**
     * Flags a field as being the special <code> public final static
     *  java.io.ObjectStreamField[] serialPersistentFields</code>
     * field.
     */
    protected static final byte F_SPFFIELD = 2;

    /**
     * Array of all java methods.
     */
    protected Method[] methods;
    /**
     * Array with flags for all java methods.
     */
    protected byte[] mutatorFlags;
    /**
     * Index of the mutator for read-write attributes. Only entries
     * <code>i</code> where <code>(m_flags[i]&M_READ) != 0</code> are used.
     * These entries contain the index of the mutator method corresponding to
     * the accessor method.
     */
    protected int[] setters;
    /**
     * Array of all java fields.
     */
    protected Field[] fields;
    /**
     * Array with flags for all java fields.
     */
    protected byte[] fieldFlags;
    /**
     * The class hash code, as specified in "The Common Object Request Broker:
     * Architecture and Specification" (01-02-33), section 10.6.2.
     */
    protected long classHashCode;
    /**
     * The repository ID. This is in the RMI hashed format.
     */
    protected String repositoryId;
    /**
     * The prefix and postfix of members repository ID. These are used to
     * calculate member repository IDs.
     */
    protected String memberPrefix;
    protected String memberPostfix;
    /**
     * Array of type of the interfaces implemented/extended here.
     */
    protected InterfaceType[] interfaces;
    /**
     * Array of type of the abstract base valuetypes implemented/extended here.
     */
    protected ValueType[] abstractBaseValuetypes;
    /**
     * Array of attributes.
     */
    protected AttributeType[] attributes;
    /**
     * Array of Constants.
     */
    protected ConstantType[] constants;
    /**
     * Array of operations.
     */
    protected OperationType[] operations;

    /**
     * A cache for the fully qualified IDL name of the IDL module we belong to.
     */
    private String idlModuleName;

    protected ContainerType(Class cls) {
        super(cls);
        if (cls == Object.class || cls == Serializable.class || cls == Externalizable.class) {
            throw new IllegalArgumentException("Cannot parse special class: " + cls.getName());
        }
        this.javaClass = cls;
    }

    protected void parse() {
        parseInterfaces();
        parseMethods();
        parseFields();
        calculateClassHashCode();
        calculateRepositoryId();
        parseAttributes();
        parseConstants();
        parseOperations();
        fixupOverloadedOperationNames();
    }

    /**
     * Return the interfaces.
     */
    public InterfaceType[] getInterfaces() {
        return (InterfaceType[])interfaces.clone();
    }

    /**
     * Return the abstract base valuetypes.
     */
    public ValueType[] getAbstractBaseValuetypes() {
        return (ValueType[])abstractBaseValuetypes.clone();
    }

    /**
     * Return the attributes.
     */
    public AttributeType[] getAttributes() {
        return (AttributeType[])attributes.clone();
    }

    /**
     * Return the constants.
     */
    public ConstantType[] getConstants() {
        return (ConstantType[])constants.clone();
    }

    /**
     * Return the operations.
     */
    public OperationType[] getOperations() {
        return (OperationType[])operations.clone();
    }

    /**
     * Return the repository ID.
     */
    public String getRepositoryId() {
        return repositoryId;
    }

    /**
     * Return a repository ID for a member.
     * 
     * @param memberName The Java name of the member.
     */
    public String getMemberRepositoryId(String memberName) {
        return memberPrefix + escapeIRName(memberName) + memberPostfix;
    }

    /**
     * Return the fully qualified IDL module name that this type should be
     * placed in.
     */
    public String getIDLModuleName() {
        if (idlModuleName == null) {
            String pkgName = javaClass.getPackage().getName();
            StringBuffer b = new StringBuffer();
            while (!"".equals(pkgName)) {
                int idx = pkgName.indexOf('.');
                String n = (idx == -1) ? pkgName : pkgName.substring(0, idx);
                b.append("::").append(IDLUtil.javaToIDLName(n));
                pkgName = (idx == -1) ? "" : pkgName.substring(idx + 1);
            }
            idlModuleName = b.toString();
        }
        return idlModuleName;
    }

    /**
     * Check if a method is an accessor.
     */
    protected boolean isReadMethod(Method m) {
        Class returnType = m.getReturnType();
        if (!m.getName().startsWith("get"))
            if (!m.getName().startsWith("is") || !(returnType == boolean.class))
                return false;
        if (returnType == void.class)
            return false;
        if (m.getParameterTypes().length != 0)
            return false;
        return hasNonAppExceptions(m);
    }

    /**
     * Check if a method is a mutator.
     */
    protected boolean isWriteMethod(Method m) {
        if (!m.getName().startsWith("set"))
            return false;
        if (m.getReturnType() != void.class)
            return false;
        if (m.getParameterTypes().length != 1)
            return false;
        return hasNonAppExceptions(m);
    }

    private static boolean isCheckedException(Class type) {
        /*
         * Is a checked exception
         */
        if (!Throwable.class.isAssignableFrom(type)) {
            return false;
        }

        if (Error.class.isAssignableFrom(type)) {
            return false;
        }

        if (RuntimeException.class.isAssignableFrom(type)) {
            return false;
        }
        return true;
    }

    /**
     * Check if a method throws anything checked other than
     * java.rmi.RemoteException and its subclasses.
     */
    protected boolean hasNonAppExceptions(Method m) {
        Class[] ex = m.getExceptionTypes();
        for (int i = 0; i < ex.length; ++i) {
            if (!isCheckedException(ex[i])) {
                continue;
            }
            if (!RemoteException.class.isAssignableFrom(ex[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Analyse the fields of the class. This will fill in the
     * <code>fields</code> and <code>f_flags</code> arrays.
     */
    protected void parseFields() {
        fields = javaClass.getDeclaredFields();
        fieldFlags = new byte[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            int mods = fields[i].getModifiers();
            if (Modifier.isFinal(mods) && Modifier.isStatic(mods) && Modifier.isPublic(mods)) {
                fieldFlags[i] |= F_CONSTANT;
            }
        }
    }

    /**
     * Analyse the interfaces of the class. This will fill in the
     * <code>interfaces</code> array.
     */
    protected void parseInterfaces() {
        Class[] intfs = javaClass.getInterfaces();
        List<InterfaceType> iTypes = new ArrayList<InterfaceType>();
        List<ValueType> vTypes = new ArrayList<ValueType>();
        for (int i = 0; i < intfs.length; ++i) {
            // Ignore java.rmi.Remote
            if (intfs[i] == Remote.class || intfs[i] == Serializable.class || intfs[i] == Externalizable.class) {
                continue;
            }
            if (!Java2IDLUtil.isAbstractValueType(intfs[i])) {
                iTypes.add(InterfaceType.getInterfaceType(intfs[i]));
            } else {
                vTypes.add(ValueType.getValueType(intfs[i]));
            }
        }
        interfaces = new InterfaceType[iTypes.size()];
        interfaces = (InterfaceType[])iTypes.toArray(interfaces);
        abstractBaseValuetypes = new ValueType[vTypes.size()];
        abstractBaseValuetypes = (ValueType[])vTypes.toArray(abstractBaseValuetypes);
    }

    /**
     * Analyse the methods of the class. This will fill in the
     * <code>methods</code> and <code>m_flags</code> arrays.
     */
    protected void parseMethods() {
        // The dynamic stub and skeleton strategy generation mechanism
        // requires the inclusion of inherited methods in the type of
        // remote interfaces. To speed things up, inherited methods are
        // not considered in the type of a class or non-remote interface.
        if (javaClass.isInterface() && Remote.class.isAssignableFrom(javaClass)) {
            methods = javaClass.getMethods();
        } else {
            methods = javaClass.getDeclaredMethods();
        }
        mutatorFlags = new byte[methods.length];
        setters = new int[methods.length];
        // Find read-write properties
        for (int i = 0; i < methods.length; ++i) {
            setters[i] = -1; // no mutator here
        }
        for (int i = 0; i < methods.length; ++i) {
            if (isReadMethod(methods[i]) && (mutatorFlags[i] & M_READ) == 0) {
                String attrName = getAttributeNameFromGetter(methods[i].getName());
                Class iReturn = methods[i].getReturnType();
                for (int j = i + 1; j < methods.length; ++j) {
                    if (isWriteMethod(methods[j]) && (mutatorFlags[j] & M_WRITE) == 0
                        && attrName.equals(getAttributeNameFromSetter(methods[j].getName()))) {
                        Class[] jParams = methods[j].getParameterTypes();
                        if (jParams.length == 1 && jParams[0] == iReturn) {
                            mutatorFlags[i] |= M_READ;
                            mutatorFlags[j] |= M_WRITE;
                            setters[i] = j;
                            break;
                        }
                    }
                }
            } else if (isWriteMethod(methods[i]) && (mutatorFlags[i] & M_WRITE) == 0) {
                String attrName = getAttributeNameFromSetter(methods[i].getName());
                Class[] iParams = methods[i].getParameterTypes();
                for (int j = i + 1; j < methods.length; ++j) {
                    if (isReadMethod(methods[j]) && (mutatorFlags[j] & M_READ) == 0
                        && attrName.equals(getAttributeNameFromGetter(methods[j].getName()))) {
                        Class jReturn = methods[j].getReturnType();
                        if (iParams.length == 1 && iParams[0] == jReturn) {
                            mutatorFlags[i] |= M_WRITE;
                            mutatorFlags[j] |= M_READ;
                            setters[j] = i;
                            break;
                        }
                    }
                }
            }
        }
        // Find read-only properties
        for (int i = 0; i < methods.length; ++i) {
            if ((mutatorFlags[i] & (M_READ | M_WRITE)) == 0 && isReadMethod(methods[i])) {
                mutatorFlags[i] |= M_READONLY;
            }
        }
        // Check for overloaded and inherited methods
        for (int i = 0; i < methods.length; ++i) {
            if ((mutatorFlags[i] & (M_READ | M_WRITE | M_READONLY)) == 0) {
                String iName = methods[i].getName();
                for (int j = i + 1; j < methods.length; ++j) {
                    if (iName.equals(methods[j].getName())) {
                        mutatorFlags[i] |= M_OVERLOADED;
                        mutatorFlags[j] |= M_OVERLOADED;
                    }
                }
            }
            if (methods[i].getDeclaringClass() != javaClass) {
                mutatorFlags[i] |= M_INHERITED;
            }
        }
    }

    /**
     * Convert an attribute read method name in Java format to an attribute name
     * in Java format.
     */
    protected String getAttributeNameFromGetter(String attrName) {
        String name = attrName;
        if (name.startsWith("get")) {
            name = name.substring(3);
        } else if (name.startsWith("is")) {
            name = name.substring(2);
        } else {
            throw new IllegalArgumentException("Invalid accessor: " + name);
        }
        return name;
    }

    /**
     * Convert an attribute write method name in Java format to an attribute
     * name in Java format.
     */
    protected String getAttributeNameFromSetter(String name) {
        if (name.startsWith("set")) {
            return name.substring(3);
        } else {
            throw new IllegalArgumentException("Invalid accessor: " + name);
        }
    }

    /**
     * Analyse constants. This will fill in the <code>constants</code> array.
     */
    protected void parseConstants() {
        List<ConstantType> types = new ArrayList<ConstantType>();
        for (int i = 0; i < fields.length; ++i) {
            if ((fieldFlags[i] & F_CONSTANT) == 0) {
                continue;
            }
            Class type = fields[i].getType();
            // Only map primitives and java.lang.String
            if (!type.isPrimitive() && type != java.lang.String.class) {
                // It is an RMI/IIOP violation for interfaces.
                if (javaClass.isInterface()) {
                    throw new IDLViolationException("Field \"" + fields[i].getName()
                        + "\" of interface \""
                        + javaClass.getName()
                        + "\" is a constant, but not of one "
                        + "of the primitive types, or String.", "1.2.3");
                }
                continue;
            }
            String name = fields[i].getName();
            Object value;
            try {
                value = fields[i].get(null);
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex.toString());
            }
            types.add(new ConstantType(name, type, value));
        }
        constants = new ConstantType[types.size()];
        constants = (ConstantType[])types.toArray(constants);
    }

    /**
     * Analyse attributes. This will fill in the <code>attributes</code>
     * array.
     */
    protected void parseAttributes() {
        List<AttributeType> types = new ArrayList<AttributeType>();
        for (int i = 0; i < methods.length; ++i) {
            // if ((m_flags[i]&M_INHERITED) != 0)
            // continue;
            if ((mutatorFlags[i] & (M_READ | M_READONLY)) != 0) {
                // Read method of an attribute.
                String name = getAttributeNameFromGetter(methods[i].getName());
                if ((mutatorFlags[i] & M_READONLY) != 0) {
                    types.add(new AttributeType(name, methods[i]));
                } else {
                    types.add(new AttributeType(name, methods[i], methods[setters[i]]));
                }
            }
        }

        attributes = new AttributeType[types.size()];
        attributes = (AttributeType[])types.toArray(attributes);
    }

    /**
     * Analyse operations. This will fill in the <code>operations</code>
     * array. This implementation just creates an empty array; override in
     * subclasses for a real type.
     */
    protected void parseOperations() {
        operations = new OperationType[0];
    }

    /**
     * Fixup overloaded operation names. As specified in section 1.3.2.6.
     */
    protected void fixupOverloadedOperationNames() {
        for (int i = 0; i < methods.length; ++i) {
            if ((mutatorFlags[i] & M_OVERLOADED) == 0) {
                continue;
            }
            // Find the operation
            OperationType oa = null;
            for (int opIdx = 0; oa == null && opIdx < operations.length; ++opIdx) {
                if (operations[opIdx].getMethod().equals(methods[i])) {
                    oa = operations[opIdx];
                }
            }
            if (oa == null) {
                continue; // This method is not mapped.
                // Calculate new IDL name
            }
            ParameterType[] parms = oa.getParameters();
            StringBuffer b = new StringBuffer(oa.getIDLName());
            if (parms.length == 0) {
                b.append("__");
            }
            for (int j = 0; j < parms.length; ++j) {
                String s = parms[j].getTypeIDLName();
                if (s.startsWith("::")) {
                    s = s.substring(2);
                }
                b.append('_');
                while (!"".equals(s)) {
                    int idx = s.indexOf("::");
                    b.append('_');
                    if (idx == -1) {
                        b.append(s);
                        s = "";
                    } else {
                        b.append(s.substring(0, idx));
                        s = s.substring(idx + 2);
                    }
                }
            }
            // Set new IDL name
            oa.setIDLName(b.toString());
        }
    }

    /**
     * Fixup names differing only in case. As specified in section 1.3.2.7.
     */
    protected void fixupCaseNames() {
        List<IDLType> entries = getContainedEntries();
        boolean[] clash = new boolean[entries.size()];
        String[] upperNames = new String[entries.size()];
        for (int i = 0; i < entries.size(); ++i) {
            IDLType aa = (IDLType)entries.get(i);
            clash[i] = false;
            upperNames[i] = aa.getIDLName().toUpperCase();
            for (int j = 0; j < i; ++j) {
                if (upperNames[i].equals(upperNames[j])) {
                    clash[i] = true;
                    clash[j] = true;
                }
            }
        }
        for (int i = 0; i < entries.size(); ++i) {
            if (!clash[i]) {
                continue;
            }
            IDLType aa = (IDLType)entries.get(i);
            boolean noUpper = true;
            String name = aa.getIDLName();
            StringBuffer b = new StringBuffer(name);
            b.append('_');
            for (int j = 0; j < name.length(); ++j) {
                if (!Character.isUpperCase(name.charAt(j))) {
                    continue;
                }
                if (noUpper) {
                    noUpper = false;
                } else {
                    b.append('_');
                }
                b.append(j);
            }
            aa.setIDLName(b.toString());
        }
    }

    /**
     * Return a list of all the entries contained here.
     * 
     * @param entries The list of entries contained here. Entries in this list
     *            must be subclasses of <code>IDLType</code>.
     */
    protected abstract List<IDLType> getContainedEntries();

    /**
     * Return the class hash code, as specified in "The Common Object Request
     * Broker: Architecture and Specification" (01-02-33), section 10.6.2.
     */
    protected void calculateClassHashCode() {
        // The simple cases
        if (javaClass.isInterface()) {
            classHashCode = 0;
        } else if (!Serializable.class.isAssignableFrom(javaClass)) {
            classHashCode = 0;
        } else if (Externalizable.class.isAssignableFrom(javaClass)) {
            classHashCode = 1;
        } else {
            // Go ask Util class for the hash code
            classHashCode = IDLUtil.getClassHashCode(javaClass);
        }
    }

    /**
     * Escape non-ISO characters for an IR name.
     */
    protected String escapeIRName(String name) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (c < 256) {
                b.append(c);
            } else {
                b.append("\\U").append(IDLUtil.toHexString((int)c));
            }
        }
        return b.toString();
    }

    /**
     * Return the IR global ID of the given class or interface. This is
     * described in section 1.3.5.7. The returned string is in the RMI hashed
     * format, like "RMI:java.util.Hashtable:C03324C0EA357270:13BB0F25214AE4B8".
     */
    protected void calculateRepositoryId() {
        if (javaClass.isArray() || javaClass.isPrimitive()) {
            throw new IllegalArgumentException("Not a class or interface.");
        }
        if (javaClass.isInterface() && org.omg.CORBA.Object.class.isAssignableFrom(javaClass)
            && org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(javaClass)) {
            StringBuffer b = new StringBuffer("IDL:");
            b.append(javaClass.getPackage().getName().replace('.', '/'));
            b.append('/');
            String base = javaClass.getName();
            base = base.substring(base.lastIndexOf('.') + 1);
            b.append(base).append(":1.0");
            repositoryId = b.toString();
        } else {
            StringBuffer b = new StringBuffer("RMI:");
            b.append(escapeIRName(javaClass.getName()));
            memberPrefix = b.toString() + ".";
            String hashStr = toHexString(classHashCode);
            b.append(':').append(hashStr);
            ObjectStreamClass osClass = ObjectStreamClass.lookup(javaClass);
            if (osClass != null) {
                long serialVersionUID = osClass.getSerialVersionUID();
                String uid = toHexString(serialVersionUID);
                if (classHashCode != serialVersionUID) {
                    b.append(':').append(uid);
                }
                memberPostfix = ":" + hashStr + ":" + uid;
            } else {
                memberPostfix = ":" + hashStr;
            }
            repositoryId = b.toString();
        }
    }
    
}
