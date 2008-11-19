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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;

/**
 * @version $Rev$ $Date$
 */
public class CodeGenerationHelper {
    /**
     * @param type
     * @return
     */
    public static Class<?> getErasure(Type type) {
        if (type instanceof Class) {
            return (Class<?>)type;
        } else if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType)type;
            Class<?> componentType = getErasure(arrayType.getGenericComponentType());
            return Array.newInstance(componentType, 0).getClass();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType)type;
            return getErasure(pType.getRawType());
        } else if (type instanceof WildcardType) {
            WildcardType wType = (WildcardType)type;
            Type[] types = wType.getUpperBounds();
            if (types.length == 0) {
                return Object.class;
            }
            return getErasure(types[0]);
        } else if (type instanceof TypeVariable) {
            TypeVariable<?> var = (TypeVariable<?>)type;
            Type[] types = var.getBounds();
            if (types.length == 0) {
                return Object.class;
            }
            return getErasure(types[0]);
        }
        return null;
    }

    /**
     * @param type
     * @return
     */
    public static String getJAXWSSignature(Type type) {
        Class<?> cls = getErasure(type);
        if (Collection.class.isAssignableFrom(cls) && (type instanceof ParameterizedType)) {
            ParameterizedType pType = (ParameterizedType)type;
            Type p = pType.getActualTypeArguments()[0];
            StringBuffer sb = new StringBuffer();
            sb.append(getSignature(cls));
            sb.deleteCharAt(sb.length() - 1); // Remove ;
            sb.append('<').append(getSignature(getErasure(p))).append(">;");
            return sb.toString();
        } else if (Map.class.isAssignableFrom(cls) && (type instanceof ParameterizedType)) {
            ParameterizedType pType = (ParameterizedType)type;
            Type key = pType.getActualTypeArguments()[0];
            Type value = pType.getActualTypeArguments()[1];
            StringBuffer sb = new StringBuffer();
            sb.append(getSignature(cls));
            sb.deleteCharAt(sb.length() - 1); // Remove ;
            sb.append('<').append(getSignature(getErasure(key))).append(getSignature(getErasure(value))).append(">;");
            return sb.toString();
        } else {
            return getSignature(cls);
        }
    }

    /**
     * @param type
     * @return
     */
    public static String getSignature(Type type) {
        if (!(type instanceof Class)) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType)type;
                StringBuffer sb = new StringBuffer();
                String rawType = getSignature(pType.getRawType());
                sb.append(rawType.substring(0, rawType.length() - 1));
                sb.append('<');
                for (Type t : pType.getActualTypeArguments()) {
                    String argType = getSignature(t);
                    sb.append(argType);
                }
                sb.append('>');
                sb.append(rawType.substring(rawType.length() - 1));
                return sb.toString();
            }
            if (type instanceof TypeVariable) {
                return "T" + ((TypeVariable<?>)type).getName() + ";";
            }
            if (type instanceof GenericArrayType) {
                GenericArrayType arrayType = (GenericArrayType)type;
                return "[" + getSignature(arrayType.getGenericComponentType());
            }
            if (type instanceof WildcardType) {
                WildcardType wType = (WildcardType)type;
                Type[] types = wType.getUpperBounds();
                StringBuffer sb = new StringBuffer();
                if (types.length == 0 || !(types.length == 1 && types[0] == Object.class)) {
                    sb.append('+');
                    for (Type t : types) {
                        sb.append(getSignature(t));
                    }
                }
                types = wType.getLowerBounds();
                if (types.length != 0) {
                    sb.append('-');
                    for (Type t : wType.getLowerBounds()) {
                        sb.append(getSignature(t));
                    }
                }
                if (sb.length() == 0) {
                    return "*";
                }
                return sb.toString();
            }
        }
        Class<?> cls = (Class<?>)type;
        return org.objectweb.asm.Type.getDescriptor(cls);
    }

    /**
     * Get the actual type arguments a child class has used to extend a generic base class.
     *
     * @param baseClass the base class
     * @param childClass the child class
     * @return a list of the raw classes for the actual type arguments.
     */
    public static <T> List<Class<?>> resovleTypeArguments(Class<T> baseClass, Class<? extends T> childClass) {
        Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
        Type type = childClass;
        // start walking up the inheritance hierarchy until we hit baseClass
        while (!getErasure(type).equals(baseClass)) {
            if (type instanceof Class) {
                // there is no useful information for us in raw types, so just keep going.
                type = ((Class<?>)type).getGenericSuperclass();
            } else {
                ParameterizedType parameterizedType = (ParameterizedType)type;
                Class<?> rawType = getErasure(parameterizedType.getRawType());

                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
                }

                if (!rawType.equals(baseClass)) {
                    type = rawType.getGenericSuperclass();
                }
            }
        }

        // finally, for each actual type argument provided to baseClass, determine (if possible)
        // the raw class for that type argument.
        Type[] actualTypeArguments;
        if (type instanceof Class) {
            actualTypeArguments = ((Class<?>)type).getTypeParameters();
        } else {
            actualTypeArguments = ((ParameterizedType)type).getActualTypeArguments();
        }
        List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
        // resolve types by chasing down type variables.
        for (Type baseType : actualTypeArguments) {
            while (resolvedTypes.containsKey(baseType)) {
                baseType = resolvedTypes.get(baseType);
            }
            typeArgumentsAsClasses.add(getErasure(baseType));
        }
        return typeArgumentsAsClasses;
    }

    /*
    signatures.put(boolean.class, "Z");
    signatures.put(byte.class, "B");
    signatures.put(char.class, "C");
    signatures.put(short.class, "S");
    signatures.put(int.class, "I");
    signatures.put(long.class, "J");
    signatures.put(float.class, "F");
    signatures.put(double.class, "D");
    */
    public static int getLoadOPCode(String signature) {
        if ("Z".equals(signature) || "B".equals(signature)
            || "C".equals(signature)
            || "S".equals(signature)
            || "I".equals(signature)) {
            return Opcodes.ILOAD;
        }

        if ("J".equals(signature)) {
            return Opcodes.LLOAD;
        }

        if ("F".equals(signature)) {
            return Opcodes.FLOAD;
        }

        if ("D".equals(signature)) {
            return Opcodes.DLOAD;
        }

        return Opcodes.ALOAD;

    }

    public static int getReturnOPCode(String signature) {
        if ("Z".equals(signature) || "B".equals(signature)
            || "C".equals(signature)
            || "S".equals(signature)
            || "I".equals(signature)) {
            return Opcodes.IRETURN;
        }

        if ("J".equals(signature)) {
            return Opcodes.LRETURN;
        }

        if ("F".equals(signature)) {
            return Opcodes.FRETURN;
        }

        if ("D".equals(signature)) {
            return Opcodes.DRETURN;
        }
        if ("V".equals(signature)) {
            return Opcodes.RETURN;
        }

        return Opcodes.ARETURN;

    }
    
    /**
     * Get the package prefix for generated JAXWS artifacts
     * @param cls
     * @return
     */
    public static String getPackagePrefix(Class<?> cls) {
        String name = cls.getName();
        int index = name.lastIndexOf('.');
        if (index == -1) {
            return "jaxws.";
        } else {
            return name.substring(0, index) + ".jaxws.";
        }
    }

}
