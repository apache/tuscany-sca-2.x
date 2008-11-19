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

package org.apache.tuscany.sca.binding.corba.impl.types.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.binding.corba.impl.types.NodeType;
import org.apache.tuscany.sca.binding.corba.impl.types.TypeTreeNode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

/**
 * @version $Rev$ $Date$
 * Proxies getType(), write(), read() methods to appropriate TypeHelper implementation.
 */
public class TypeHelpersProxy {

    /**
     * Maps primitive types to its TypeHelper implementations
     */
    private static Map<Class<?>, TypeHelper> primitiveTypes = null;

    /**
     * Maps other types to its TypeHelper implementations
     */
    private static Map<NodeType, TypeHelper> complexTypes = null;

    static {
        // initiate type helpers
        primitiveTypes = new HashMap<Class<?>, TypeHelper>();
        complexTypes = new HashMap<NodeType, TypeHelper>();

        primitiveTypes.put(boolean.class, new BooleanTypeHelper());
        primitiveTypes.put(char.class, new CharTypeHelper());
        primitiveTypes.put(byte.class, new ByteTypeHelper());
        primitiveTypes.put(short.class, new ShortTypeHelper());
        primitiveTypes.put(int.class, new IntTypeHelper());
        primitiveTypes.put(long.class, new LongTypeHelper());
        primitiveTypes.put(float.class, new FloatTypeHelper());
        primitiveTypes.put(double.class, new DoubleTypeHelper());
        primitiveTypes.put(String.class, new StringTypeHelper());
        primitiveTypes.put(Boolean.class, primitiveTypes.get(boolean.class));
        primitiveTypes.put(Character.class, primitiveTypes.get(char.class));
        primitiveTypes.put(Byte.class, primitiveTypes.get(byte.class));
        primitiveTypes.put(Short.class, primitiveTypes.get(short.class));
        primitiveTypes.put(Integer.class, primitiveTypes.get(int.class));
        primitiveTypes.put(Long.class, primitiveTypes.get(long.class));
        primitiveTypes.put(Float.class, primitiveTypes.get(float.class));
        primitiveTypes.put(Double.class, primitiveTypes.get(double.class));
        primitiveTypes.put(String.class, primitiveTypes.get(String.class));
        complexTypes.put(NodeType.array, new ArrayTypeHelper());
        complexTypes.put(NodeType.struct, new StructTypeHelper());
        complexTypes.put(NodeType.reference, new ReferenceTypeHelper());
        complexTypes.put(NodeType.sequence, new SequenceTypeHelper());
        complexTypes.put(NodeType.idl_enum, new EnumTypeHelper());
        complexTypes.put(NodeType.exception, new StructTypeHelper());
        complexTypes.put(NodeType.union, new UnionTypeHelper());
    }

    /**
     * Gets type helper basing on given type
     * 
     * @param node
     * @return
     */
    private static TypeHelper getTypeHelper(TypeTreeNode node) {
        TypeHelper typeHelper = null;
        NodeType type = node.getNodeType();
        if (type.equals(NodeType.primitive)) {
            typeHelper = primitiveTypes.get(node.getJavaClass());
        } else {
            typeHelper = complexTypes.get(type);
        }
        return typeHelper;
    }

    /**
     * Proxies read method invocation to appropriate TypeHelper implementation.
     * 
     * @param node
     * @param is
     * @return
     */
    public static final Object read(TypeTreeNode node, InputStream is) {
        TypeHelper helper = getTypeHelper(node);
        return helper.read(node, is);
    }

    /**
     * Proxies write method invocation to appropriate TypeHelper implementation.
     * 
     * @param node
     * @param os
     * @param data
     */
    public static final void write(TypeTreeNode node, OutputStream os, Object data) {
        TypeHelper helper = getTypeHelper(node);
        helper.write(node, os, data);
    }

}
