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
package org.apache.tuscany.sca.databinding.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;

/**
 *
 * @version $Rev$ $Date$
 */
public class DataTypeHelper {
    private DataTypeHelper() {
    }

    /**
     * Find all classes referenced by this data type though java generics
     * @param d
     * @return
     */
    public static Set<Class<?>> findClasses(DataType d) {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        Set<Type> visited = new HashSet<Type>();
        findClasses(d, classes, visited);
        return classes;
    }

    private static void findClasses(DataType d, Set<Class<?>> classes, Set<Type> visited) {
        if (d == null) {
            return;
        }
        classes.add(d.getPhysical());
        if (d.getPhysical() != d.getGenericType()) {
            findClasses(d.getGenericType(), classes, visited);
        }
    }

    /**
     * Find referenced classes in the generic type
     * @param type
     * @param classSet
     * @param visited
     */
    private static void findClasses(Type type, Set<Class<?>> classSet, Set<Type> visited) {
        if (visited.contains(type) || type == null) {
            return;
        }
        visited.add(type);
        if (type instanceof Class) {
            Class<?> cls = (Class<?>)type;
            if (!cls.isInterface()) {
                classSet.add(cls);
            }
            return;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType)type;
            findClasses(pType.getRawType(), classSet, visited);
            for (Type t : pType.getActualTypeArguments()) {
                findClasses(t, classSet, visited);
            }
        } else if (type instanceof TypeVariable) {
            TypeVariable<?> tv = (TypeVariable<?>)type;
            for (Type t : tv.getBounds()) {
                findClasses(t, classSet, visited);
            }
        } else if (type instanceof GenericArrayType) {
            GenericArrayType gType = (GenericArrayType)type;
            findClasses(gType, classSet, visited);
        } else if (type instanceof WildcardType) {
            WildcardType wType = (WildcardType)type;
            for (Type t : wType.getLowerBounds()) {
                findClasses(t, classSet, visited);
            }
            for (Type t : wType.getUpperBounds()) {
                findClasses(t, classSet, visited);
            }
        }
    }

    /**
     * Get all the data types in the interface
     * @param intf The interface
     * @param useWrapper Use wrapper classes?
     * @return A list of DataTypes
     */
    public static List<DataType> getDataTypes(Interface intf, boolean useWrapper) {
        List<DataType> dataTypes = new ArrayList<DataType>();
        for (Operation op : intf.getOperations()) {
            getDataTypes(dataTypes, op, useWrapper);
        }
        return dataTypes;
    }

    /**
     * Get all the data types in the operation
     * @param op The operaiton
     * @param useWrapper Use wrapper classes?
     * @return A list of DataTypes
     */
    public static List<DataType> getDataTypes(Operation op, boolean useWrapper) {
        List<DataType> dataTypes = new ArrayList<DataType>();
        getDataTypes(dataTypes, op, useWrapper);
        return dataTypes;
    }

    private static void getDataTypes(List<DataType> dataTypes, Operation op, boolean useWrapper) {
        WrapperInfo wrapper = op.getWrapper();
        if (useWrapper && wrapper != null) {
            DataType dt1 = wrapper.getInputWrapperType();
            if (dt1 != null) {
                dataTypes.add(dt1);
            }
            DataType dt2 = wrapper.getOutputWrapperType();
            if (dt2 != null) {
                dataTypes.add(dt2);
            }
        }
        // FIXME: [rfeng] We may need to find the referenced classes in the child types
        // else 
        {
            for (DataType dt1 : op.getInputType().getLogical()) {
                dataTypes.add(dt1);
            }
            DataType dt2 = op.getOutputType();
            if (dt2 != null) {
                dataTypes.add(dt2);
            }
        }
        for (DataType<DataType> dt3 : op.getFaultTypes()) {
            DataType dt4 = dt3.getLogical();
            if (dt4 != null) {
                dataTypes.add(dt4);
            }
        }
    }

}
