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
package org.apache.tuscany.sca.binding.corba.impl.util;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.omg.CORBA.portable.IDLEntity;

/**
 * @version $Rev$ $Date$
 */
public final class OperationMapper {

    private static Set<Class<?>> getAllInterfaces(Class<?> intfClass) {
        Set<Class<?>> allInterfaces = new LinkedHashSet<Class<?>>();

        LinkedList<Class<?>> stack = new LinkedList<Class<?>>();
        stack.addFirst(intfClass);

        while (!stack.isEmpty()) {
            Class<?> intf = stack.removeFirst();
            allInterfaces.add(intf);
            for (Class<?> i : intf.getInterfaces()) {
                stack.add(0, i);
            }
        }

        return allInterfaces;
    }    
    
    /**
     * Maps Java methods to operation names
     * @param intfClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<Method, String> mapMethodToOperationName(Class<?> intfClass) {
        return iiopMap(intfClass, false);
    }

    /**
     * Maps operation names to Java methods
     * @param intfClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Method> mapOperationNameToMethod(Class<?> intfClass) {
        return iiopMap(intfClass, true);
    }

    @SuppressWarnings("unchecked")
    private static Map iiopMap(Class<?> intfClass, boolean operationToMethod) {
        Method[] methods = getAllMethods(intfClass);

        // find every valid getter
        Map<Method, String> getterByMethod = new HashMap<Method, String>(methods.length);
        Map<String, Method> getterByName = new HashMap<String, Method>(methods.length);
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String methodName = method.getName();

            // no arguments allowed
            if (method.getParameterTypes().length != 0) {
                continue;
            }

            // must start with get or is
            String verb;
            if (methodName.startsWith("get") && methodName.length() > 3 && method.getReturnType() != void.class) {
                verb = "get";
            } else if (methodName.startsWith("is") && methodName.length() > 2 && method.getReturnType() == boolean.class) {
                verb = "is";
            } else {
                continue;
            }

            // must only throw Remote or Runtime Exceptions
            boolean exceptionsValid = true;
            Class[] exceptionTypes = method.getExceptionTypes();
            for (int j = 0; j < exceptionTypes.length; j++) {
                Class<?> exceptionType = exceptionTypes[j];
                if (!RemoteException.class.isAssignableFrom(exceptionType) &&
                        !RuntimeException.class.isAssignableFrom(exceptionType) &&
                        !Error.class.isAssignableFrom(exceptionType)) {
                    exceptionsValid = false;
                    break;
                }
            }
            if (!exceptionsValid) {
                continue;
            }

            String propertyName;
            if (methodName.length() > verb.length() + 1 && Character.isUpperCase(methodName.charAt(verb.length() + 1))) {
                propertyName = methodName.substring(verb.length());
            } else {
                propertyName = Character.toLowerCase(methodName.charAt(verb.length())) + methodName.substring(verb.length() + 1);
            }
            getterByMethod.put(method, propertyName);
            getterByName.put(propertyName, method);
        }

        Map<Method, String> setterByMethod = new HashMap<Method, String>(methods.length);
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String methodName = method.getName();

            // must have exactally one arg
            if (method.getParameterTypes().length != 1) {
                continue;
            }

            // must return non void
            if (method.getReturnType() != void.class) {
                continue;
            }

            // must start with set
            if (!methodName.startsWith("set") || methodName.length() <= 3) {
                continue;
            }

            // must only throw Remote or Runtime Exceptions
            boolean exceptionsValid = true;
            Class<?>[] exceptionTypes = method.getExceptionTypes();
            for (int j = 0; j < exceptionTypes.length; j++) {
                Class<?> exceptionType = exceptionTypes[j];
                if (!RemoteException.class.isAssignableFrom(exceptionType) &&
                        !RuntimeException.class.isAssignableFrom(exceptionType) &&
                        !Error.class.isAssignableFrom(exceptionType)) {
                    exceptionsValid = false;
                    break;
                }
            }
            if (!exceptionsValid) {
                continue;
            }

            String propertyName;
            if (methodName.length() > 4 && Character.isUpperCase(methodName.charAt(4))) {
                propertyName = methodName.substring(3);
            } else {
                propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
            }

            // must have a matching getter
            Method getter = (Method) getterByName.get(propertyName);
            if (getter == null) {
                continue;
            }

            // setter property must match getter return value
            if (!method.getParameterTypes()[0].equals(getter.getReturnType())) {
                continue;
            }
            setterByMethod.put(method, propertyName);
        }

        // index the methods by name... used to determine which methods are overloaded
        HashMap<String, List<Method>> overloadedMethods = new HashMap<String, List<Method>>(methods.length);
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (getterByMethod.containsKey(method) || setterByMethod.containsKey(method)) {
                continue;
            }
            String methodName = method.getName();
            List<Method> methodList = overloadedMethods.get(methodName);
            if (methodList == null) {
                methodList = new LinkedList<Method>();
                overloadedMethods.put(methodName, methodList);
            }
            methodList.add(method);
        }

        // index the methods by lower case name... used to determine which methods differ only by case
        Map<String, Set<String>> caseCollisionMethods = new HashMap<String, Set<String>>(methods.length);
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (getterByMethod.containsKey(method) || setterByMethod.containsKey(method)) {
                continue;
            }
            String lowerCaseMethodName = method.getName().toLowerCase();
            Set<String> methodSet = caseCollisionMethods.get(lowerCaseMethodName);
            if (methodSet == null) {
                methodSet = new HashSet<String>();
                caseCollisionMethods.put(lowerCaseMethodName, methodSet);
            }
            methodSet.add(method.getName());
        }

        String className = getClassName(intfClass);
        Map iiopMap = new HashMap(methods.length);
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];

            String iiopName = (String) getterByMethod.get(method);
            if (iiopName != null) {
                // if we have a leading underscore prepend with J
                if (iiopName.charAt(0) == '_') {
                    iiopName = "J_get_" + iiopName.substring(1);
                } else {
                    iiopName = "_get_" + iiopName;
                }
            } else {
                iiopName = (String) setterByMethod.get(method);
                if (iiopName != null) {
                    // if we have a leading underscore prepend with J
                    if (iiopName.charAt(0) == '_') {
                        iiopName = "J_set_" + iiopName.substring(1);
                    } else {
                        iiopName = "_set_" + iiopName;
                    }
                } else {
                    iiopName = method.getName();

                    // if we have a leading underscore prepend with J
                    if (iiopName.charAt(0) == '_') {
                        iiopName = "J" + iiopName;
                    }
                }
            }

            // if this name only differs by case add the case index to the end
            Set<String> caseCollisions = caseCollisionMethods.get(method.getName().toLowerCase());
            if (caseCollisions != null && caseCollisions.size() > 1) {
                iiopName += upperCaseIndexString(iiopName);
            }

            // if this is an overloaded method append the parameter string
            List<Method> overloads = overloadedMethods.get(method.getName());
            if (overloads != null && overloads.size() > 1) {
                iiopName += buildOverloadParameterString(method.getParameterTypes());
            }

            // if we have a leading underscore prepend with J
            iiopName = replace(iiopName, '$', "U0024");

            // if we have matched a keyword prepend with an underscore
            if (keywords.contains(iiopName.toLowerCase())) {
                iiopName = "_" + iiopName;
            }

            // if the name is the same as the class name, append an underscore
            if (iiopName.equalsIgnoreCase(className)) {
                iiopName += "_";
            }

            if (operationToMethod) {
                iiopMap.put(iiopName, method);
            } else {
                iiopMap.put(method, iiopName);
            }
        }

        return iiopMap;
    }
    
    private static Method[] getAllMethods(Class<?> intfClass) {
        List<Method> methods = new LinkedList<Method>();
        for (Iterator<Class<?>> iterator = getAllInterfaces(intfClass).iterator(); iterator.hasNext();) {
            Class<?> intf = iterator.next();
            methods.addAll(Arrays.asList(intf.getDeclaredMethods()));
        }

        return (Method[]) methods.toArray(new Method[methods.size()]);
    }

    /**
     * Return the a string containing an underscore '_' index of each uppercase
     * character in the IIOP name. This is used for distinction of names that
     * only differ by case, since CORBA does not support case sensitive names.
     */
    private static String upperCaseIndexString(String iiopName) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < iiopName.length(); i++) {
            char c = iiopName.charAt(i);
            if (Character.isUpperCase(c)) {
                stringBuffer.append('_').append(i);
            }
        }
        return stringBuffer.toString();
    }

    /**
     * Replaces any occurances of the specified "oldChar" with the new string.
     * This is used to replace occurances if '$' in CORBA names since '$' is a
     * special character
     */
    private static String replace(String source, char oldChar, String newString) {
        StringBuffer stringBuffer = new StringBuffer(source.length());
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c == oldChar) {
                stringBuffer.append(newString);
            } else {
                stringBuffer.append(c);
            }
        }
        return stringBuffer.toString();
    }

    /**
     * Return the a string containing a double underscore '__' list of parameter
     * types encoded using the Java to IDL rules. This is used for distinction
     * of methods that only differ by parameter lists.
     */
    private static String buildOverloadParameterString(Class<?>[] parameterTypes) {
        String name = "";
        if (parameterTypes.length == 0) {
            name += "__";
        } else {
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                name += buildOverloadParameterString(parameterType);
            }
        }
        return name.replace('.', '_');
    }

    /**
     * Returns a single parameter type encoded using the Java to IDL rules.
     */
    private static String buildOverloadParameterString(Class<?> parameterType) {
        String name = "_";

        int arrayDimensions = 0;
        while (parameterType.isArray()) {
            arrayDimensions++;
            parameterType = parameterType.getComponentType();
        }

        // arrays start with org_omg_boxedRMI_
        if (arrayDimensions > 0) {
            name += "_org_omg_boxedRMI";
        }

        // IDLEntity types must be prefixed with org_omg_boxedIDL_
        if (IDLEntity.class.isAssignableFrom(parameterType)) {
            name += "_org_omg_boxedIDL";
        }

        // add package... some types have special mappings in corba
        String packageName = (String)specialTypePackages.get(parameterType.getName());
        if (packageName == null) {
            packageName = getPackageName(parameterType.getName());
        }
        if (packageName.length() > 0) {
            name += "_" + packageName;
        }

        // arrays now contain a dimension indicator
        if (arrayDimensions > 0) {
            name += "_" + "seq" + arrayDimensions;
        }

        // add the class name
        String className = (String)specialTypeNames.get(parameterType.getName());
        if (className == null) {
            className = buildClassName(parameterType);
        }
        name += "_" + className;

        return name;
    }

    /**
     * Returns a string containing an encoded class name.
     */
    private static String buildClassName(Class<?> type) {
        if (type.isArray()) {
            throw new IllegalArgumentException("type is an array: " + type);
        }

        // get the classname
        String typeName = type.getName();
        int endIndex = typeName.lastIndexOf('.');
        if (endIndex < 0) {
            return typeName;
        }
        StringBuffer className = new StringBuffer(typeName.substring(endIndex + 1));

        // for innerclasses replace the $ separator with two underscores
        // we can't just blindly replace all $ characters since class names can
        // contain the $ character
        if (type.getDeclaringClass() != null) {
            String declaringClassName = getClassName(type.getDeclaringClass());
            assert className.toString().startsWith(declaringClassName + "$");
            className.replace(declaringClassName.length(), declaringClassName.length() + 1, "__");
        }

        // if we have a leading underscore prepend with J
        if (className.charAt(0) == '_') {
            className.insert(0, "J");
        }
        return className.toString();
    }
    
    private static String getClassName(Class<?> type) {
        if (type.isArray()) {
            throw new IllegalArgumentException("type is an array: " + type);
        }

        // get the classname
        String typeName = type.getName();
        int endIndex = typeName.lastIndexOf('.');
        if (endIndex < 0) {
            return typeName;
        }
        return typeName.substring(endIndex + 1);
    }

    private static String getPackageName(String interfaceName) {
        int endIndex = interfaceName.lastIndexOf('.');
        if (endIndex < 0) {
            return "";
        }
        return interfaceName.substring(0, endIndex);
    }

    private static final Map<String, String> specialTypeNames;
    private static final Map<String, String> specialTypePackages;
    private static final Set<String> keywords;

    static {
        specialTypeNames = new HashMap<String, String>();
        specialTypeNames.put("boolean", "boolean");
        specialTypeNames.put("char", "wchar");
        specialTypeNames.put("byte", "octet");
        specialTypeNames.put("short", "short");
        specialTypeNames.put("int", "long");
        specialTypeNames.put("long", "long_long");
        specialTypeNames.put("float", "float");
        specialTypeNames.put("double", "double");
        specialTypeNames.put("java.lang.Class", "ClassDesc");
        specialTypeNames.put("java.lang.String", "WStringValue");
        specialTypeNames.put("org.omg.CORBA.Object", "Object");

        specialTypePackages = new HashMap<String, String>();
        specialTypePackages.put("boolean", "");
        specialTypePackages.put("char", "");
        specialTypePackages.put("byte", "");
        specialTypePackages.put("short", "");
        specialTypePackages.put("int", "");
        specialTypePackages.put("long", "");
        specialTypePackages.put("float", "");
        specialTypePackages.put("double", "");
        specialTypePackages.put("java.lang.Class", "javax.rmi.CORBA");
        specialTypePackages.put("java.lang.String", "CORBA");
        specialTypePackages.put("org.omg.CORBA.Object", "");

        keywords = new HashSet<String>();
        keywords.add("abstract");
        keywords.add("any");
        keywords.add("attribute");
        keywords.add("boolean");
        keywords.add("case");
        keywords.add("char");
        keywords.add("const");
        keywords.add("context");
        keywords.add("custom");
        keywords.add("default");
        keywords.add("double");
        keywords.add("enum");
        keywords.add("exception");
        keywords.add("factory");
        keywords.add("false");
        keywords.add("fixed");
        keywords.add("float");
        keywords.add("in");
        keywords.add("inout");
        keywords.add("interface");
        keywords.add("long");
        keywords.add("module");
        keywords.add("native");
        keywords.add("object");
        keywords.add("octet");
        keywords.add("oneway");
        keywords.add("out");
        keywords.add("private");
        keywords.add("public");
        keywords.add("raises");
        keywords.add("readonly");
        keywords.add("sequence");
        keywords.add("short");
        keywords.add("string");
        keywords.add("struct");
        keywords.add("supports");
        keywords.add("switch");
        keywords.add("true");
        keywords.add("truncatable");
        keywords.add("typedef");
        keywords.add("union");
        keywords.add("unsigned");
        keywords.add("valuebase");
        keywords.add("valuetype");
        keywords.add("void");
        keywords.add("wchar");
        keywords.add("wstring");
    }

    @SuppressWarnings("unchecked")
    public static Map<Operation, Method> mapOperationToMethod(List<Operation> operations, Class<?> forClass) {
        return (Map<Operation, Method>)createMethod2OperationMapping(operations, forClass, false);
    }
    
    @SuppressWarnings("unchecked")
    public static Map<Method, Operation> mapMethodToOperation(List<Operation> operations, Class<?> forClass) {
        return (Map<Method, Operation>)createMethod2OperationMapping(operations, forClass, true);
    }
    
    /**
     * Maps Java methods to Tuscany operations
     */
    @SuppressWarnings("unchecked")
    private static Map createMethod2OperationMapping(List<Operation> operations, Class<?> forClass, boolean method2operation) {
        // for every operation find all methods with the same name, then
        // compare operations and methods parameters
        Map mapping = new HashMap();
        for (Operation operation : operations) {
            List<DataType> inputTypes = operation.getInputType().getLogical();
            Method[] methods = forClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(operation.getName()) && inputTypes.size() == methods[i]
                    .getParameterTypes().length) {
                    Class<?>[] parameterTypes = methods[i].getParameterTypes();
                    int j = 0;
                    boolean parameterMatch = true;
                    for (DataType dataType : inputTypes) {
                        if (!dataType.getPhysical().equals(parameterTypes[j])) {
                            parameterMatch = false;
                            break;
                        }
                        j++;
                    }
                    if (parameterMatch) {
                        // match found
                        if (method2operation) {
                            mapping.put(methods[i], operation);
                        } else {
                            mapping.put(operation, methods[i]);
                        }
                        break;
                    }
                }
            }
        }
        return mapping;
    }
    
}
