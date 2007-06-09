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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Java to IDL Language Mapping Specificiation v1.3 (formal/03-09-04) IDL
 * Utilities
 */
public class Java2IDLUtil {

    /*
     * checks if the method includes java.rmi.RemoteException or its subclass in
     * its throws clause.
     */
    private static boolean throwsRemoteException(Method method) {
        Class[] exTypes = method.getExceptionTypes();
        for (int i = 0; i < exTypes.length; ++i) {
            if (RemoteException.class.isAssignableFrom(exTypes[i]))
                return true;
        }
        return false;
    }

    /*
     * checks if the given class is declared as static (inner classes only)
     */
    private static boolean isStatic(Class c) {
        return (Modifier.isStatic(c.getModifiers()));
    }

    /**
     * 1.2.1 Overview of Conforming RMI/IDL Types A conforming RMI/IDL type is a
     * Java type whose values may be transmitted across an RMI/IDL remote
     * interface at run-time. A Java data type is a conforming RMI/IDL type if
     * it is: one of the Java primitive types (see Section 1.2.2, "Primitive
     * Types," on page 1-2). a conforming remote interface (as defined in
     * Section 1.2.3, "RMI/IDL Remote Interfaces," on page 1-3). a conforming
     * value type (as defined in Section 1.2.4, "RMI/IDL Value Types," on page
     * 1-4). an array of conforming RMI/IDL types (see Section 1.2.5, "RMI/IDL
     * Arrays," on page 1-5). a conforming exception type (see Section 1.2.6,
     * "RMI/IDL Exception Types," on page 1-5). a conforming CORBA object
     * reference type (see Section 1.2.7, "CORBA Object Reference Types," on
     * page 1-6). a conforming IDL entity type (see Section 1.2.8, "IDL Entity
     * Types," on page 1-6).
     * 
     * @param type
     * @return
     */
    public static boolean isIDLType(Class type) {
        /*
         * Primitive types. Spec 28.2.2
         */
        if (isPrimitiveType(type))
            return true;

        /*
         * Conforming array. Spec 28.2.5
         */
        if (isIDLArray(type))
            return true;

        /*
         * Conforming CORBA reference type. Spec 28.2.7
         */
        if (isCORBAObjectType(type))
            return true;

        /*
         * Conforming IDL Entity type. Spec 28.2.8
         */
        if (isEntityType(type))
            return true;

        /*
         * Conforming remote interface. Spec 28.2.3
         */
        if (isRemoteInterface(type))
            return true;

        /*
         * Conforming exception. Spec 28.2.6
         */
        if (isExceptionType(type))
            return true;

        /*
         * Conforming value type. Spec 28.2.4
         */
        if (isValueType(type))
            return true;

        return false;
    }

    /**
     * Section 1.2.3 RMI/IDL Remote Interfaces
     * 
     * @param type
     * @return
     */
    public static boolean isRemoteInterface(Class type) {

        /*
         * The interface is or inherits from java.rmi.Remote either directly or
         * indirectly.
         */
        if (!Remote.class.isAssignableFrom(type))
            return false;

        /*
         * All methods in the interface are defined to throw
         * java.rmi.RemoteException or a superclass of java.rmi.RemoteException.
         * Throughout this section, references to methods in the interface
         * include methods in any inherited interfaces
         */
        Method[] methods = type.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (!throwsRemoteException(methods[i])) {
                return false;
            }

            /*
             * All checked exception classes used in method declarations (other
             * than java.rmi.RemoteException and its subclasses) are conforming
             * RMI/IDL exception types (see Section 1.2.6, "RMI/IDL Exception
             * Types," on page 1-5).1
             */
            Class[] exTypes = methods[i].getExceptionTypes();
            for (int j = 0; j < exTypes.length; j++) {
                if (!isExceptionType(exTypes[j]))
                    return false;
            }
        }

        // TODO: Check method overloading from inherited interfaces
        /*
         * Method names may be overloaded. However, when an interface directly
         * inherits from several base interfaces, it is forbidden for there to
         * be method name conflicts between the inherited interfaces. This
         * outlaws the case where an interface A defines a method "foo," an
         * interface B also defines a method "foo," and an interface C tries to
         * inherit from both A and B.
         */

        /*
         * Constant definitions in the form of interface variables are
         * permitted. The constant value must be a compile-time constant of one
         * of the RMI/IDL primitive types or String.
         */
        Field[] fields = type.getFields();
        for (int k = 0; k < fields.length; k++) {
            Class fieldType = fields[k].getType();
            if (fieldType.isPrimitive() || fieldType == String.class)
                continue;
            return false;
        }
        return true;
    }

    /**
     * Section 1.3.11
     * 
     * @param type The java class
     * @return true if it is an IDL abstract interface
     */
    public static boolean isAbstractInterface(Class type) {
        /*
         * It must be a Java interface.
         */
        if (!type.isInterface())
            return false;

        /*
         * It must not be the interface of a CORBA object.
         */
        if (org.omg.CORBA.Object.class.isAssignableFrom(type))
            return false;

        /*
         * It must not extend java.rmi.Remote directly or indirectly.
         */
        if (Remote.class.isAssignableFrom(type))
            return false;

        Method[] methods = type.getMethods();

        for (int i = 0; i < methods.length; i++) {
            /*
             * All methods MUST throw java.rmi.RemoteException or a subclass.
             */
            if (!throwsRemoteException(methods[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 1.2.6 RMI/IDL Exception Types An RMI/IDL exception type is a checked
     * exception class (as defined by the Java Language Specification). Since
     * checked exception classes extend java.lang.Throwable, which implements
     * java.io.Serializable, it is unnecessary for an RMI/IDL exception class to
     * directly implement java.io.Serializable.
     */
    public static boolean isExceptionType(Class type) {

        /*
         * Is a checked exception
         */
        if (!Throwable.class.isAssignableFrom(type))
            return false;

        if (Error.class.isAssignableFrom(type))
            return false;

        if (RuntimeException.class.isAssignableFrom(type))
            return false;

        /*
         * meets the requirements for RMI/IDL value types defined in Section
         * 1.2.4, "RMI/IDL Value Types," on page 1-4.
         */
        if (!isValueType(type))
            return false;

        return true;
    }

    /**
     * 1.2.4 RMI/IDL Value Types An RMI/IDL value type represents a class whose
     * values can be moved between systems. So rather than transmitting a
     * reference between systems, the actual state of the object is transmitted
     * between systems. This requires that the receiving system have an
     * analogous class that can be used to hold the received value. Value types
     * may be passed as arguments or results of remote methods, or as fields
     * within other objects that are passed remotely.
     */
    public static boolean isValueType(Class type) {
        /*
         * The class must implement the java.io.Serializable interface, either
         * directly or indirectly, and must be serializable at run-time. It may
         * serialize references to other RMI/IDL types, including value types
         * and remote interfaces.
         */
        if (!Serializable.class.isAssignableFrom(type))
            return false;

        /*
         * A value type must not either directly or indirectly implement the
         * java.rmi.Remote interface. (If this were allowed, then there would be
         * potential confusion between value types and remote interface
         * references.)
         */
        if (Remote.class.isAssignableFrom(type))
            return false;

        /*
         * It cannot be a CORBA object.
         */
        if (org.omg.CORBA.Object.class.isAssignableFrom(type))
            return false;

        /*
         * If the class is a non-static inner class, then its containing class
         * must also be a conforming RMI/IDL value type.
         */
        if ((type.getDeclaringClass() != null) && (!isStatic(type)))
            if (!isValueType(type.getDeclaringClass()))
                return false;

        return true;
    }

    public static boolean isAbstractValueType(Class type) {
        if (!type.isInterface())
            return false;

        if (org.omg.CORBA.Object.class.isAssignableFrom(type))
            return false;

        boolean cannotBeRemote = false;
        boolean cannotBeAbstractInterface = false;

        if (java.rmi.Remote.class.isAssignableFrom(type)) {
            cannotBeAbstractInterface = true;
        } else {
            cannotBeRemote = true;
        }

        Method[] methods = type.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (!throwsRemoteException(methods[i])) {
                cannotBeAbstractInterface = true;
                cannotBeRemote = true;
                break;
            }

            Class[] exTypes = methods[i].getExceptionTypes();
            for (int j = 0; j < exTypes.length; j++) {
                if (!isExceptionType(exTypes[j])) {
                    cannotBeRemote = true;
                    break;
                }
            }
        }

        if (!cannotBeRemote) {
            Field[] fields = type.getFields();
            for (int k = 0; k < fields.length; k++) {
                if (fields[k].getType().isPrimitive())
                    continue;
                if (fields[k].getType().equals(java.lang.String.class))
                    continue;
                cannotBeRemote = true;
                break;
            }
        }
        return cannotBeRemote && cannotBeAbstractInterface;
    }

    /**
     * 1.2.2 Primitive Types All the standard Java primitive types are supported
     * as part of RMI/IDL. These are: void, boolean, byte, char, short, int,
     * long, float, double
     * 
     * @param type
     * @return
     */
    public static boolean isPrimitiveType(Class type) {
        return (type != null && type.isPrimitive());
    }

    /**
     * 1.2.7 CORBA Object Reference Types A conforming CORBA object reference
     * type is either
     * <ul>
     * <li>the Java interface org.omg.CORBA.Object, or
     * <li>a Java interface that extends org.omg.CORBA.Object directly or
     * indirectly and conforms to the rules specified in the Java Language
     * Mapping (i.e., could have been generated by applying the mapping to an
     * OMG IDL definition).
     * </ul>
     */
    public static boolean isCORBAObjectType(Class type) {
        if (type == org.omg.CORBA.Object.class)
            return true;
        if (type.isInterface() && org.omg.CORBA.Object.class.isAssignableFrom(type))
            return true;
        return false;
    }

    /**
     * 1.2.8 IDL Entity Types A Java class is a conforming IDL entity type if it
     * extends org.omg.CORBA.portable.IDLEntity and conforms to the rules
     * specified in the Java Language Mapping (i.e., could have been generated
     * by applying the mapping to an OMG IDL definition) and is not an OMG IDL
     * user exception.
     */
    public static boolean isEntityType(Class type) {
        if (!org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(type))
            return false;
        if (isExceptionType(type))
            return false;
        return true;
    }

    /**
     * 1.2.5 RMI/IDL Arrays Arrays of any conforming RMI/IDL type are also
     * conforming RMI/IDL types. So int[] and String[][][] are conforming
     * RMI/IDL types. Similarly if Wombat is a conforming RMI/IDL interface
     * type, then Wombat[] is a conforming RMI/IDL type.
     */
    public static boolean isIDLArray(Class type) {
        if (!type.isArray())
            return false;
        Class componentType = type.getComponentType();
        return isIDLType(componentType);
    }

}
