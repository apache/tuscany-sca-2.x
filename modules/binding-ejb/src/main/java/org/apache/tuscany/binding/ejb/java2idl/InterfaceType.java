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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * IDL Interface Routines here are conforming to the "Java(TM) Language to IDL
 * Mapping Specification", version 1.1 (01-06-07).
 */
public class InterfaceType extends ContainerType {

    private boolean abstractInterface;
    private String[] typeIDs;

    /**
     * Map of IDL operation names to operation parses.
     */
    private Map operationTypeMap;

    private static WorkCache cache = new WorkCache(InterfaceType.class);

    public static InterfaceType getInterfaceType(Class cls) {
        return (InterfaceType)cache.getType(cls);
    }

    protected InterfaceType(Class cls) {
        super(cls);
    }

    protected void parse() {
        super.parse();
        if (!javaClass.isInterface())
            throw new IllegalArgumentException("Class [" + javaClass.getName() + "] is not an interface.");
        abstractInterface = Java2IDLUtil.isAbstractInterface(javaClass);
        calculateOperationTypeMap();
        calculateAllTypeIds();
        fixupCaseNames();
    }

    public boolean isAbstractInterface() {
        return abstractInterface;
    }

    private boolean isRemoteInterface() {
        return (!abstractInterface);
    }

    public String[] getTypeIDs() {
        return (String[])typeIDs.clone();
    }

    /**
     * Return a list of all the entries contained here.
     * 
     * @param entries The list of entries contained here. Entries in this list
     *            are subclasses of <code>AbstractType</code>.
     */
    protected ArrayList getContainedEntries() {
        ArrayList ret = new ArrayList(constants.length + attributes.length + operations.length);
        for (int i = 0; i < constants.length; ++i)
            ret.add(constants[i]);
        for (int i = 0; i < attributes.length; ++i)
            ret.add(attributes[i]);
        for (int i = 0; i < operations.length; ++i)
            ret.add(operations[i]);
        return ret;
    }

    /**
     * Analyse operations. This will fill in the <code>operations</code>
     * array.
     */
    protected void parseOperations() {
        int operationCount = 0;
        for (int i = 0; i < methods.length; ++i)
            if ((m_flags[i] & (M_READ | M_WRITE | M_READONLY)) == 0)
                ++operationCount;
        operations = new OperationType[operationCount];
        operationCount = 0;
        for (int i = 0; i < methods.length; ++i) {
            if ((m_flags[i] & (M_READ | M_WRITE | M_READONLY)) == 0) {
                operations[operationCount] = new OperationType(methods[i]);
                ++operationCount;
            }
        }
    }

    /**
     * Calculate the map that maps IDL operation names to operation parses.
     * Besides mapped operations, this map also contains the attribute accessor
     * and mutator operations.
     */
    protected void calculateOperationTypeMap() {
        operationTypeMap = new HashMap();
        OperationType oa;
        // Map the operations
        for (int i = 0; i < operations.length; ++i) {
            oa = operations[i];
            operationTypeMap.put(oa.getIDLName(), oa);
        }
        // Map the attributes
        for (int i = 0; i < attributes.length; ++i) {
            AttributeType attr = attributes[i];
            oa = attr.getReadOperationType();
            // Not having an accessor analysis means that
            // the attribute is not in a remote interface
            if (oa != null) {
                operationTypeMap.put(oa.getIDLName(), oa);
                oa = attr.getWriteOperationType();
                if (oa != null)
                    operationTypeMap.put(oa.getIDLName(), oa);
            }
        }
    }

    /**
     * Calculate the array containing all type ids of this interface, in the
     * format that org.omg.CORBA.portable.Servant._all_interfaces() is expected
     * to return.
     */
    protected void calculateAllTypeIds() {
        if (!isRemoteInterface()) {
            typeIDs = new String[0];
        } else {
            ArrayList a = new ArrayList();
            InterfaceType[] intfs = getInterfaces();
            for (int i = 0; i < intfs.length; ++i) {
                String[] ss = intfs[i].getTypeIDs();
                for (int j = 0; j < ss.length; ++j)
                    if (!a.contains(ss[j]))
                        a.add(ss[j]);
            }
            typeIDs = new String[a.size() + 1];
            typeIDs[0] = getRepositoryId();
            for (int i = 1; i <= a.size(); ++i)
                typeIDs[i] = (String)a.get(a.size() - i);
        }
    }
}
