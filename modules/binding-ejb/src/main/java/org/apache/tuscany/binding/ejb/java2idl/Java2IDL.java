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

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class responsible for Java To IDL Mapping
 */
public class Java2IDL {

    public static Map getIDLMapping(Class type) {
        InterfaceType interfaceType = InterfaceType.getInterfaceType(type);
        Map names = new HashMap();
        AttributeType[] attrs = interfaceType.getAttributes();
        for (int i = 0; i < attrs.length; i++) {
            OperationType op = attrs[i].getReadOperationType();
            if (op != null) {
                names.put(op.getMethod(), op);
            }
            op = attrs[i].getWriteOperationType();
            if (op != null) {
                names.put(op.getMethod(), op);
            }
        }
        OperationType[] ops = interfaceType.getOperations();
        for (int i = 0; i < ops.length; i++) {
            names.put(ops[i].getMethod(), ops[i]);
        }
        // Generate the method _ids(), declared as abstract in ObjectImpl
        interfaceType.getTypeIDs();
        return names;
    }
}
