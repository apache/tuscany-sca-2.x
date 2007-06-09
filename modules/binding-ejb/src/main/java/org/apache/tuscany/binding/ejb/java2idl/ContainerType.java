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
package org.apache.tuscany.binding.ejb.java2idl;

import java.io.Externalizable;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Common base class of ValueType and InterfaceType.
 */
public abstract class ContainerType extends ClassType {

    /** Flags a method as overloaded. */
    protected final byte M_OVERLOADED = 1;
    /** Flags a method as the accessor of a read-write property. */
    protected final byte M_READ = 2;
    /** Flags a method as the mutator of a read-write property. */
    protected final byte M_WRITE = 4;
    /** Flags a method as the accessor of a read-only property. */
    protected final byte M_READONLY = 8;
    /** Flags a method as being inherited. */
    protected final byte M_INHERITED = 16;
    /**
     * Flags a method as being the writeObject() method used for serialization.
     */
    protected final byte M_WRITEOBJECT = 32;
    /** Flags a field as being a constant (public final static). */
    protected final byte F_CONSTANT = 1;
    /**
     * Flags a field as being the special <code> public final static
     *  java.io.ObjectStreamField[] serialPersistentFields</code>
     * field.
     */
    protected final byte F_SPFFIELD = 2;

    /**
     * Array of all java methods.
     */
    protected Method[] methods;
    /**
     * Array with flags for all java methods.
     */
    protected byte[] m_flags;
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
    protected byte[] f_flags;
    /**
     * The class hash code, as specified in "The Common Object Request Broker:
     * Architecture and Specification" (01-02-33), section 10.6.2.
     */
    protected long classHashCode = 0;
    /**
     * The repository ID. This is in the RMI hashed format, like
     * "RMI:java.util.Hashtable:C03324C0EA357270:13BB0F25214AE4B8".
     */
    protected String repositoryId;
    /**
     * The prefix and postfix of members repository ID. These are used to
     * calculate member repository IDs and are like "RMI:java.util.Hashtable."
     * and ":C03324C0EA357270:13BB0F25214AE4B8".
     */
    protected String memberPrefix, memberPostfix;
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
    private String idlModuleName = null;

    protected ContainerType(Class cls) {
        super(cls);
        if (cls == java.lang.Object.class || cls == java.io.Serializable.class || cls == java.io.Externalizable.class)
            throw new IllegalArgumentException("Cannot parse special class: " + cls.getName());
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

    // Protected -----------------------------------------------------
    /**
     * Convert an integer to a 16-digit hex string.
     */
    protected String toHexString(int i) {
        String s = Integer.toHexString(i).toUpperCase();
        if (s.length() < 8)
            return "00000000".substring(0, 8 - s.length()) + s;
        else
            return s;
    }

    /**
     * Convert a long to a 16-digit hex string.
     */
    protected String toHexString(long l) {
        String s = Long.toHexString(l).toUpperCase();
        if (s.length() < 16)
            return "0000000000000000".substring(0, 16 - s.length()) + s;
        else
            return s;
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
        if (!Throwable.class.isAssignableFrom(type))
            return false;

        if (Error.class.isAssignableFrom(type))
            return false;

        if (RuntimeException.class.isAssignableFrom(type))
            return false;
        return true;
    }

    /**
     * Check if a method throws anything checked other than
     * java.rmi.RemoteException and its subclasses.
     */
    protected boolean hasNonAppExceptions(Method m) {
        Class[] ex = m.getExceptionTypes();
        for (int i = 0; i < ex.length; ++i) {
            if (!isCheckedException(ex[i]))
                continue;
            if (!RemoteException.class.isAssignableFrom(ex[i]))
                return false;
        }
        return true;
    }

    /**
     * Analyse the fields of the class. This will fill in the
     * <code>fields</code> and <code>f_flags</code> arrays.
     */
    protected void parseFields() {
        fields = javaClass.getDeclaredFields();
        f_flags = new byte[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            int mods = fields[i].getModifiers();
            if (Modifier.isFinal(mods) && Modifier.isStatic(mods) && Modifier.isPublic(mods))
                f_flags[i] |= F_CONSTANT;
        }
    }

    /**
     * Analyse the interfaces of the class. This will fill in the
     * <code>interfaces</code> array.
     */
    protected void parseInterfaces() {
        Class[] intfs = javaClass.getInterfaces();
        ArrayList a = new ArrayList();
        ArrayList b = new ArrayList();
        for (int i = 0; i < intfs.length; ++i) {
            // Ignore java.rmi.Remote
            if (intfs[i] == java.rmi.Remote.class)
                continue;
            // Ignore java.io.Serializable
            if (intfs[i] == java.io.Serializable.class)
                continue;
            // Ignore java.io.Externalizable
            if (intfs[i] == java.io.Externalizable.class)
                continue;
            if (!Java2IDLUtil.isAbstractValueType(intfs[i])) {
                a.add(InterfaceType.getInterfaceType(intfs[i]));
            } else {
                b.add(ValueType.getValueType(intfs[i]));
            }
        }
        interfaces = new InterfaceType[a.size()];
        interfaces = (InterfaceType[])a.toArray(interfaces);
        abstractBaseValuetypes = new ValueType[b.size()];
        abstractBaseValuetypes = (ValueType[])b.toArray(abstractBaseValuetypes);
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
        if (javaClass.isInterface() && java.rmi.Remote.class.isAssignableFrom(javaClass))
            methods = javaClass.getMethods();
        else
            methods = javaClass.getDeclaredMethods();
        m_flags = new byte[methods.length];
        setters = new int[methods.length];
        // Find read-write properties
        for (int i = 0; i < methods.length; ++i)
            setters[i] = -1; // no mutator here
        for (int i = 0; i < methods.length; ++i) {
            if (isReadMethod(methods[i]) && (m_flags[i] & M_READ) == 0) {
                String attrName = attributeReadName(methods[i].getName());
                Class iReturn = methods[i].getReturnType();
                for (int j = i + 1; j < methods.length; ++j) {
                    if (isWriteMethod(methods[j]) && (m_flags[j] & M_WRITE) == 0
                        && attrName.equals(attributeWriteName(methods[j].getName()))) {
                        Class[] jParams = methods[j].getParameterTypes();
                        if (jParams.length == 1 && jParams[0] == iReturn) {
                            m_flags[i] |= M_READ;
                            m_flags[j] |= M_WRITE;
                            setters[i] = j;
                            break;
                        }
                    }
                }
            } else if (isWriteMethod(methods[i]) && (m_flags[i] & M_WRITE) == 0) {
                String attrName = attributeWriteName(methods[i].getName());
                Class[] iParams = methods[i].getParameterTypes();
                for (int j = i + 1; j < methods.length; ++j) {
                    if (isReadMethod(methods[j]) && (m_flags[j] & M_READ) == 0
                        && attrName.equals(attributeReadName(methods[j].getName()))) {
                        Class jReturn = methods[j].getReturnType();
                        if (iParams.length == 1 && iParams[0] == jReturn) {
                            m_flags[i] |= M_WRITE;
                            m_flags[j] |= M_READ;
                            setters[j] = i;
                            break;
                        }
                    }
                }
            }
        }
        // Find read-only properties
        for (int i = 0; i < methods.length; ++i)
            if ((m_flags[i] & (M_READ | M_WRITE)) == 0 && isReadMethod(methods[i]))
                m_flags[i] |= M_READONLY;
        // Check for overloaded and inherited methods
        for (int i = 0; i < methods.length; ++i) {
            if ((m_flags[i] & (M_READ | M_WRITE | M_READONLY)) == 0) {
                String iName = methods[i].getName();
                for (int j = i + 1; j < methods.length; ++j) {
                    if (iName.equals(methods[j].getName())) {
                        m_flags[i] |= M_OVERLOADED;
                        m_flags[j] |= M_OVERLOADED;
                    }
                }
            }
            if (methods[i].getDeclaringClass() != javaClass)
                m_flags[i] |= M_INHERITED;
        }
    }

    /**
     * Convert an attribute read method name in Java format to an attribute name
     * in Java format.
     */
    protected String attributeReadName(String name) {
        if (name.startsWith("get"))
            name = name.substring(3);
        else if (name.startsWith("is"))
            name = name.substring(2);
        else
            throw new IllegalArgumentException("Not an accessor: " + name);
        return name;
    }

    /**
     * Convert an attribute write method name in Java format to an attribute
     * name in Java format.
     */
    protected String attributeWriteName(String name) {
        if (name.startsWith("set"))
            name = name.substring(3);
        else
            throw new IllegalArgumentException("Not an accessor: " + name);
        return name;
    }

    /**
     * Analyse constants. This will fill in the <code>constants</code> array.
     */
    protected void parseConstants() {
        ArrayList a = new ArrayList();
        for (int i = 0; i < fields.length; ++i) {
            if ((f_flags[i] & F_CONSTANT) == 0)
                continue;
            Class type = fields[i].getType();
            // Only map primitives and java.lang.String
            if (!type.isPrimitive() && type != java.lang.String.class) {
                // It is an RMI/IIOP violation for interfaces.
                if (javaClass.isInterface())
                    throw new IDLViolationException("Field \"" + fields[i].getName()
                        + "\" of interface \""
                        + javaClass.getName()
                        + "\" is a constant, but not of one "
                        + "of the primitive types, or String.", "1.2.3");
                continue;
            }
            String name = fields[i].getName();
            Object value;
            try {
                value = fields[i].get(null);
            } catch (Exception ex) {
                throw new RuntimeException(ex.toString());
            }
            a.add(new ConstantType(name, type, value));
        }
        constants = new ConstantType[a.size()];
        constants = (ConstantType[])a.toArray(constants);
    }

    /**
     * Analyse attributes. This will fill in the <code>attributes</code>
     * array.
     */
    protected void parseAttributes() {
        ArrayList a = new ArrayList();
        for (int i = 0; i < methods.length; ++i) {
            // if ((m_flags[i]&M_INHERITED) != 0)
            // continue;
            if ((m_flags[i] & (M_READ | M_READONLY)) != 0) {
                // Read method of an attribute.
                String name = attributeReadName(methods[i].getName());
                if ((m_flags[i] & M_READONLY) != 0)
                    a.add(new AttributeType(name, methods[i]));
                else
                    a.add(new AttributeType(name, methods[i], methods[setters[i]]));
            }
        }

        attributes = new AttributeType[a.size()];
        attributes = (AttributeType[])a.toArray(attributes);
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
            if ((m_flags[i] & M_OVERLOADED) == 0)
                continue;
            // Find the operation
            OperationType oa = null;
            for (int opIdx = 0; oa == null && opIdx < operations.length; ++opIdx)
                if (operations[opIdx].getMethod().equals(methods[i]))
                    oa = operations[opIdx];
            if (oa == null)
                continue; // This method is not mapped.
            // Calculate new IDL name
            ParameterType[] parms = oa.getParameters();
            StringBuffer b = new StringBuffer(oa.getIDLName());
            if (parms.length == 0)
                b.append("__");
            for (int j = 0; j < parms.length; ++j) {
                String s = parms[j].getTypeIDLName();
                if (s.startsWith("::"))
                    s = s.substring(2);
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
        ArrayList entries = getContainedEntries();
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
            if (!clash[i])
                continue;
            IDLType aa = (IDLType)entries.get(i);
            boolean noUpper = true;
            String name = aa.getIDLName();
            StringBuffer b = new StringBuffer(name);
            b.append('_');
            for (int j = 0; j < name.length(); ++j) {
                if (!Character.isUpperCase(name.charAt(j)))
                    continue;
                if (noUpper)
                    noUpper = false;
                else
                    b.append('_');
                b.append(j);
            }
            aa.setIDLName(b.toString());
        }
    }

    /**
     * Return a list of all the entries contained here.
     * 
     * @param entries The list of entries contained here. Entries in this list
     *            must be subclasses of <code>AbstractType</code>.
     */
    abstract protected ArrayList getContainedEntries();

    /**
     * Return the class hash code, as specified in "The Common Object Request
     * Broker: Architecture and Specification" (01-02-33), section 10.6.2.
     */
    protected void calculateClassHashCode() {
        // The simple cases
        if (javaClass.isInterface())
            classHashCode = 0;
        else if (!Serializable.class.isAssignableFrom(javaClass))
            classHashCode = 0;
        else if (Externalizable.class.isAssignableFrom(javaClass))
            classHashCode = 1;
        else
            // Go ask Util class for the hash code
            classHashCode = IDLUtil.getClassHashCode(javaClass);
    }

    /**
     * Escape non-ISO characters for an IR name.
     */
    protected String escapeIRName(String name) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (c < 256)
                b.append(c);
            else
                b.append("\\U").append(toHexString((int)c));
        }
        return b.toString();
    }

    /**
     * Return the IR global ID of the given class or interface. This is
     * described in section 1.3.5.7. The returned string is in the RMI hashed
     * format, like "RMI:java.util.Hashtable:C03324C0EA357270:13BB0F25214AE4B8".
     */
    protected void calculateRepositoryId() {
        if (javaClass.isArray() || javaClass.isPrimitive())
            throw new IllegalArgumentException("Not a class or interface.");
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
                String SVUID = toHexString(serialVersionUID);
                if (classHashCode != serialVersionUID)
                    b.append(':').append(SVUID);
                memberPostfix = ":" + hashStr + ":" + SVUID;
            } else
                memberPostfix = ":" + hashStr;
            repositoryId = b.toString();
        }
    }

    /**
     * A simple aggregate of work-in-progress, and the thread doing the work.
     */
    private static class Task {
        ContainerType type;
        Thread thread;

        Task(ContainerType type, Thread thread) {
            this.type = type;
            this.thread = thread;
        }
    }

    /**
     * Instances of this class cache the most complex types. The types cached
     * are:
     * <ul>
     * <li><code>InterfaceType</code> for interfaces.</li>
     * <li><code>ValueType</code> for value types.</li>
     * <li><code>ExceptionType</code> for exceptions.</li>
     * </ul>
     * Besides caching work already done, this caches work in progress, as we
     * need to know about this to handle cyclic graphs of parses. When a thread
     * re-enters the <code>getType()</code> metohd, an unfinished type will be
     * returned if the same thread is already working on this type.
     */
    protected static class WorkCache {
        /**
         * The type constructor of our type class. This constructor takes a
         * single argument of type <code>Class</code>.
         */
        private Constructor constructor;

        /**
         * This maps the classes of completely done parses to soft references of
         * their type.
         */
        private Map workDone;

        /**
         * This maps the classes of parses in progress to their type.
         */
        private Map workInProgress;

        /**
         * Create a new work cache manager.
         * 
         * @param containerType The class of the type type we cache here.
         */
        WorkCache(Class containerType) {
            // Find the constructor and initializer.
            try {
                constructor = containerType.getDeclaredConstructor(new Class[] {Class.class});
            } catch (NoSuchMethodException ex) {
                throw new IllegalArgumentException("Bad Class: " + ex.toString());
            }
            workDone = new WeakHashMap();
            workInProgress = new HashMap();
        }

        /**
         * Returns an type. If the calling thread is currently doing an type of
         * this class, an unfinished type is returned.
         */
        ContainerType getType(Class cls) {
            ContainerType ret;
            synchronized (this) {
                ret = lookupDone(cls);
                if (ret != null)
                    return ret;
                // is it work-in-progress?
                Task inProgress = (Task)workInProgress.get(cls);
                if (inProgress != null) {
                    if (inProgress.thread == Thread.currentThread())
                        return inProgress.type; // return unfinished
                    // Do not wait for the other thread: We may deadlock
                    // Double work is better than deadlock...
                }
                ret = createTask(cls);
            }
            // Do the work
            parse(cls, ret);
            // We did it
            synchronized (this) {
                workInProgress.remove(cls);
                workDone.put(cls, new SoftReference(ret));
                notifyAll();
            }
            return ret;
        }

        /**
         * Lookup an type in the fully done map.
         */
        private ContainerType lookupDone(Class cls) {
            SoftReference ref = (SoftReference)workDone.get(cls);
            if (ref == null)
                return null;
            ContainerType ret = (ContainerType)ref.get();
            if (ret == null)
                workDone.remove(cls); // clear map entry if soft ref. was
            // cleared.
            return ret;
        }

        /**
         * Create new work-in-progress.
         */
        private ContainerType createTask(Class cls) {
            try {
                ContainerType type = (ContainerType)constructor.newInstance(new Object[] {cls});
                workInProgress.put(cls, new Task(type, Thread.currentThread()));
                return type;
            } catch (Exception ex) {
                // Ignore it
                return null;
            }
        }

        private void parse(Class cls, ContainerType ret) {
            try {
                ret.parse();
            } finally {
                synchronized (this) {
                    workInProgress.remove(cls);
                }
            }
        }

    }

}
