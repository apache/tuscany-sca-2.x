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

package org.apache.tuscany.sca.binding.corba.impl.types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.binding.corba.impl.exceptions.RequestConfigurationException;
import org.apache.tuscany.sca.binding.corba.meta.CorbaArray;
import org.apache.tuscany.sca.binding.corba.meta.CorbaUnionElement;
import org.apache.tuscany.sca.binding.corba.meta.CorbaUnionElementType;

/**
 * @version $Rev$ $Date$
 *          Creator of types tree.
 */
public class TypeTreeCreator {

    /**
     * Helps to determine if type is a primitive.
     */
    private static List<Class<?>> primitives = new ArrayList<Class<?>>();

    static {
        primitives.add(boolean.class);
        primitives.add(byte.class);
        primitives.add(short.class);
        primitives.add(int.class);
        primitives.add(long.class);
        primitives.add(double.class);
        primitives.add(float.class);
        primitives.add(char.class);
        primitives.add(String.class);
        primitives.add(Boolean.class);
        primitives.add(Byte.class);
        primitives.add(Short.class);
        primitives.add(Integer.class);
        primitives.add(Long.class);
        primitives.add(Double.class);
        primitives.add(Float.class);
        primitives.add(Character.class);
    }

    /**
     * Creates class for given string argument.
     * 
     * @param name name of type
     * @return type
     */
    private static Class<?> createClassFromString(String name) {
        Class<?> result = null;
        try {
            if (name.length() == 1) {
                // primitives
                switch (name.charAt(0)) {
                    case 'Z':
                        result = boolean.class;
                        break;
                    case 'C':
                        result = char.class;
                        break;
                    case 'B':
                        result = byte.class;
                        break;
                    case 'S':
                        result = short.class;
                        break;
                    case 'I':
                        result = int.class;
                        break;
                    case 'J':
                        result = long.class;
                        break;
                    case 'F':
                        result = float.class;
                        break;
                    case 'D':
                        result = double.class;
                        break;
                }
            } else {
                // class
                name = name.substring(1, name.length() - 1);
                result = Class.forName(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Reduces dimension of array.
     * 
     * @param forClass array class
     * @return reduced array
     */
    private static Class<?> reduceArrayDimension(Class<?> forClass) {
        String name = forClass.getName();
        try {
            String reduced = name.substring(1, name.length());
            if (reduced.startsWith("[")) {
                // reduced class is still an array
                return Class.forName(reduced);
            } else {
                // reduced class may be primitive or class
                return createClassFromString(reduced);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return given array without first element
     * 
     * @param array
     * @return
     */
    private static int[] removeFirstElement(int[] array) {
        int[] result = new int[array.length - 1];
        System.arraycopy(array, 1, result, 0, result.length);
        return result;
    }

    /**
     * Converts objects annotations to structure which will be used by this
     * class
     * 
     * @param notes
     * @return
     */
    private static AnnotationAttributes createAnnotationAttributes(Annotation[] notes) {
        AnnotationAttributes attrs = new AnnotationAttributes();
        for (int i = 0; notes != null && i < notes.length; i++) {
            if (notes[i].annotationType().equals(CorbaArray.class)) {
                attrs.setCorbaArray(true);
                attrs.setCorbaArrayLength(((CorbaArray)notes[i]).value());
            }
        }
        return attrs;
    }

    /**
     * Creates tree for given type.
     * 
     * @param forClass
     * @return type tree
     */
    public static TypeTree createTypeTree(Class<?> forClass, Annotation[] notes) throws RequestConfigurationException {
        TypeTree tree = new TypeTree();
        TypeTreeNode rootNode = null;
        AnnotationAttributes attrs = createAnnotationAttributes(notes);
        rootNode = inspectClassHierarchy(forClass, attrs, tree);
        tree.setRootNode(rootNode);
        return tree;

    }

    /**
     * Recurrent method which builds type tree.
     * 
     * @param forClass
     * @param tree
     * @return
     */
    private static TypeTreeNode inspectClassHierarchy(Class<?> forClass, AnnotationAttributes attributes, TypeTree tree)
        throws RequestConfigurationException {

        TypeTreeNode node = null;
        node = createTypeNode(forClass, attributes);

        NodeType nodeType = node.getNodeType();
        TypeTreeNode[] children = null;

        if (nodeType.equals(NodeType.primitive)) {
            // stop condition for recurrent method
        } else if (nodeType.equals(NodeType.array)) {
            Class<?> reduced = reduceArrayDimension(node.getJavaClass());
            children = new TypeTreeNode[1];
            int[] newLengths = removeFirstElement(attributes.getCorbaArrayLength());
            attributes.setCorbaArrayLength(newLengths);
            children[0] = inspectClassHierarchy(reduced, attributes, tree);
        } else if (nodeType.equals(NodeType.sequence)) {
            // reducing sequence dimension
            Class<?> reduced = reduceArrayDimension(node.getJavaClass());
            children = new TypeTreeNode[1];
            children[0] = inspectClassHierarchy(reduced, attributes, tree);
            // System.arraycopy(src, srcPos, dest, destPos, length)
        } else if (nodeType.equals(NodeType.struct) || nodeType.equals(NodeType.exception)) {
            // inspect types for every structure member
            Field[] fields = node.getJavaClass().getFields();
            children = new TypeTreeNode[fields.length];
            for (int i = 0; i < fields.length; i++) {
                Class<?> field = fields[i].getType();
                AnnotationAttributes fAttrs = createAnnotationAttributes(fields[i].getAnnotations());
                TypeTreeNode child = inspectClassHierarchy(field, fAttrs, tree);
                child.setName(fields[i].getName());
                children[i] = child;
            }
        } else if (nodeType.equals(NodeType.idl_enum)) {

        } else if (nodeType.equals(NodeType.union)) {
            // inspect types for every structure member
            Field[] fields = node.getJavaClass().getDeclaredFields();
            children = new TypeTreeNode[fields.length];
            for (int i = 0; i < fields.length; i++) {
                Class<?> field = fields[i].getType();
                AnnotationAttributes fAttrs = createAnnotationAttributes(fields[i].getAnnotations());
                TypeTreeNode child = inspectClassHierarchy(field, fAttrs, tree);
                child.setName(fields[i].getName());
                children[i] = child;
            }
        } else if (nodeType.equals(NodeType.reference)) {
            // TODO: CORBA references
        }

        node.setChildren(children);
        return node;
    }

    /**
     * Creating and configuring TypeTreeNode for given class.
     * 
     * @param forClass class
     * @return node
     * @throws RequestConfigurationException
     */
    private static TypeTreeNode createTypeNode(Class<?> forClass, AnnotationAttributes attributes)
        throws RequestConfigurationException {
        TypeTreeNode node = new TypeTreeNode();
        if (forClass.isArray() && !attributes.isCorbaArray()) {
            node.setNodeType(NodeType.sequence);
            node.setJavaClass(forClass);
        } else if (forClass.isArray() && attributes.isCorbaArray()) {
            node.setNodeType(NodeType.array);
            node.setJavaClass(forClass);
            try {
                // set the actual array size for further use by ArrayTypeHelper
                node.setAttributes(attributes.getCorbaArrayLength()[0]);
            } catch (ArrayIndexOutOfBoundsException e) {
                RequestConfigurationException exc =
                    new RequestConfigurationException("Annotated array size doesn't match declared arrays size");
                throw exc;
            }
        } else if (primitives.contains(forClass)) {
            node.setNodeType(NodeType.primitive);
            node.setJavaClass(forClass);
            node.setChildren(null);
        } else if (forClass.isInterface()) {
            node.setNodeType(NodeType.reference);
            node.setJavaClass(forClass);
            node.setChildren(null);
        } else if (isStructType(forClass)) {
            node.setNodeType(NodeType.struct);
            node.setJavaClass(forClass);
        } else if (isEnumType(forClass)) {
            node.setNodeType(NodeType.idl_enum);
            node.setJavaClass(forClass);
        } else if (isUserException(forClass)) {
            node.setNodeType(NodeType.exception);
            node.setJavaClass(forClass);
        } else if (isUnionType(forClass)) {
            node.setNodeType(NodeType.union);
            node.setJavaClass(forClass);
            node.setAttributes(getUnionAttributes(forClass));
        } else {
            RequestConfigurationException e =
                new RequestConfigurationException("User defined type which cannot be handled: " + forClass
                    .getCanonicalName());
            throw e;
        }
        return node;
    }

    /**
     * Tells whether given class is structure
     * 
     * @param forClass
     * @return
     */
    private static boolean isStructType(Class<?> forClass) {
        int classMods = forClass.getModifiers();
        if (!Modifier.isFinal(classMods)) {
            return false;
        }
        boolean areCtorsValid = false;
        Class<?>[] fieldsTypes = null;
        Constructor<?>[] ctors = forClass.getConstructors();
        /*
         * Do we have 2 ctors and one of them is null ctor?
         */
        if (ctors.length != 2) {
            return false;
        }
        for (int i = 0; i < ctors.length; i++) {
            Class<?>[] params = ctors[i].getParameterTypes();
            if (params.length == 0) {
                areCtorsValid = true;
            } else {
                fieldsTypes = params;
            }
        }
        if (!areCtorsValid) {
            return false;
        }
        /*
         * Are constructor args declared as class fields?
         */
        Field[] fields = forClass.getFields();
        Set<Class<?>> fieldsSet = new HashSet<Class<?>>(Arrays.asList(fieldsTypes));
        for (int i = 0; i < fields.length && !fieldsSet.isEmpty(); i++) {
            int mods = fields[i].getModifiers();
            if (Modifier.isPublic(mods) && !Modifier.isStatic(mods) && !Modifier.isFinal(mods)) {
                fieldsSet.remove(fields[i].getType());
            }

        }
        return fieldsSet.isEmpty();
    }

    /**
     * Tells whether given class is enum
     * 
     * @param forClass
     * @return
     */
    private static boolean isEnumType(Class<?> forClass) {
        boolean isValueMethod = false;
        boolean isFromIntMethod = false;
        /*
         * enum type should have value and from_int methods
         */
        try {
            Method valueMet = forClass.getMethod("value", new Class[] {});
            int modValueMet = valueMet.getModifiers();
            if (valueMet.getReturnType().equals(int.class) && Modifier.isPublic(modValueMet)) {
                isValueMethod = true;
            }
            Method fromIntMet = forClass.getMethod("from_int", new Class[] {int.class});
            int modFromIntMet = fromIntMet.getModifiers();
            if ((fromIntMet.getReturnType().equals(forClass) && Modifier.isPublic(modFromIntMet) && Modifier
                .isStatic(modFromIntMet))) {
                isFromIntMethod = true;
            }
        } catch (NoSuchMethodException e) {
        }
        if (!isFromIntMethod || !isValueMethod) {
            return false;
        }
        /*
         * enum type should also contain minimum one pair of fields: EnumType
         * field and int _field
         */
        int enumCount = 0;
        Field[] fields = forClass.getFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getType().equals(forClass)) {
                int modifiers = fields[i].getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && Modifier.isFinal(modifiers)) {
                    try {
                        Field field = forClass.getField("_" + fields[i].getName());
                        if (field.getType().equals(int.class)) {
                            enumCount++;
                        }
                    } catch (NoSuchFieldException e) {
                    }

                }
            }
        }
        return enumCount > 0;
    }

    /**
     * Tells whether given class is CORBA user exception
     * 
     * @param forClass
     * @return
     */
    private static boolean isUserException(Class<?> forClass) {
        do {
            if (forClass.equals(Exception.class)) {
                return true;
            } else {
                forClass = forClass.getSuperclass();
            }
        } while (forClass != null && !forClass.equals(Object.class));
        return false;
    }

    /**
     * Tells whether given class is CORBA union. This method validates usage of
     * unions annotations.
     * 
     * @param forClass
     * @return
     * @throws RequestConfigurationException
     */
    private static boolean isUnionType(Class<?> forClass) throws RequestConfigurationException {
        int classMods = forClass.getModifiers();
        if (!Modifier.isFinal(classMods)) {
            return false;
        }
        boolean atLeastOneOption = false;
        boolean discriminatorPresent = false;
        for (int i = 0; i < forClass.getDeclaredFields().length; i++) {
            CorbaUnionElement note = forClass.getDeclaredFields()[i].getAnnotation(CorbaUnionElement.class);
            if (note != null) {
                int fieldMod = forClass.getDeclaredFields()[i].getModifiers();
                if (Modifier.isPrivate(fieldMod) && !Modifier.isFinal(fieldMod) && !Modifier.isStatic(fieldMod)) {
                    if (note.type().equals(CorbaUnionElementType.discriminator)) {
                        if (discriminatorPresent) {
                            throw new RequestConfigurationException(
                                                                    "More than one discriminators declared on: " + forClass);
                        }
                        discriminatorPresent = true;
                    } else {
                        atLeastOneOption = true;
                    }
                } else {
                    throw new RequestConfigurationException(
                                                            "Annotated union field should be private, not final and no static on class: " + forClass);
                }
            }
        }
        if (atLeastOneOption && !discriminatorPresent) {
            throw new RequestConfigurationException("No discriminator annotation found on: " + forClass);
        } else if (!atLeastOneOption && discriminatorPresent) {
            throw new RequestConfigurationException("No union option found on: " + forClass);
        } else if (discriminatorPresent && atLeastOneOption) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets union attributes - discriminator field name, option fields etc. This
     * method relies that previously scanned class is valid (method isUnionType)
     * 
     * @param forClass
     * @return
     * @throws RequestConfigurationException
     */
    private static UnionAttributes getUnionAttributes(Class<?> forClass) throws RequestConfigurationException {
        UnionAttributes attributes = new UnionAttributes();
        for (int i = 0; i < forClass.getDeclaredFields().length; i++) {
            CorbaUnionElement note = forClass.getDeclaredFields()[i].getAnnotation(CorbaUnionElement.class);
            if (note != null) {
                if (note.type().equals(CorbaUnionElementType.discriminator)) {
                    attributes.setDiscriminatorName(forClass.getDeclaredFields()[i].getName());
                } else if (note.type().equals(CorbaUnionElementType.defaultOption)) {
                    attributes.setDefaultOptionName(forClass.getDeclaredFields()[i].getName());
                } else if (note.type().equals(CorbaUnionElementType.option)) {
                    if (attributes.getOptionsMapping().containsKey(note.optionNumber())) {
                        throw new RequestConfigurationException("In " + forClass
                            + ": field \""
                            + forClass.getDeclaredFields()[i].getName()
                            + "\" uses already used option id: "
                            + note.optionNumber());
                    } else {
                        attributes.getOptionsMapping().put(note.optionNumber(),
                                                           forClass.getDeclaredFields()[i].getName());
                    }
                }
            }
        }
        return attributes;
    }
}
