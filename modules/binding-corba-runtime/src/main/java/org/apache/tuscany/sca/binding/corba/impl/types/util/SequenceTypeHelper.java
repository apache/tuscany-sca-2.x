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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tuscany.sca.binding.corba.impl.types.TypeTreeNode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

/**
 * @version $Rev$ $Date$
 */
public class SequenceTypeHelper implements TypeHelper {

    public Object read(TypeTreeNode node, InputStream is) {
        Object sequence = null;
        try {
            int size = is.read_long();
            sequence = Array.newInstance(node.getChildren()[0].getJavaClass(), size);
            for (int i = 0; i < size; i++) {
                Array.set(sequence, i, TypeHelpersProxy.read(node.getChildren()[0], is));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sequence;
    }

    public void write(TypeTreeNode node, OutputStream os, Object data) {
        int sum = 0;
        // determine length of array
        List<Object> array = new ArrayList<Object>();
        while (true) {
            try {
                array.add(Array.get(data, sum));
                sum++;
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        os.write_long(sum);
        Iterator<Object> iter = array.iterator();
        while (iter.hasNext()) {
            Object elem = iter.next();
            TypeHelpersProxy.write(node.getChildren()[0], os, elem);
        }

    }
}
