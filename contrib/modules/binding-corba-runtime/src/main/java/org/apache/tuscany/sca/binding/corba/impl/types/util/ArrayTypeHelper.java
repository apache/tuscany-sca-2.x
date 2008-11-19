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

import java.lang.reflect.Array;

import org.apache.tuscany.sca.binding.corba.impl.types.TypeTreeNode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

/**
 * @version $Rev$ $Date$
 */
public class ArrayTypeHelper implements TypeHelper {

    public Object read(TypeTreeNode node, InputStream is) {
        Object array = null;
        try {
            int size = (Integer)node.getAttributes();
            array = Array.newInstance(node.getChildren()[0].getJavaClass(), size);
            for (int i = 0; i < size; i++) {
                Array.set(array, i, TypeHelpersProxy.read(node.getChildren()[0], is));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public void write(TypeTreeNode node, OutputStream os, Object data) {
        for (int i = 0; i < (Integer)node.getAttributes(); i++) {
            TypeHelpersProxy.write(node.getChildren()[0], os, Array.get(data, i));
        }
    }

}
