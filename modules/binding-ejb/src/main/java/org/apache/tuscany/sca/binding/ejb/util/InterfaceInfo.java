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
package org.apache.tuscany.sca.binding.ejb.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.tuscany.sca.binding.ejb.corba.Java2IDLUtil;

// import commonj.sdo.DataObject;

/**
 * The signature for a java interface
 */
public class InterfaceInfo implements Serializable {

    private static final Map<Class, InterfaceInfo> INTERFACES =
        Collections.synchronizedMap(new WeakHashMap<Class, InterfaceInfo>());

    private static final long serialVersionUID = 2314533906465094860L;
    private String name;

    private Map<String, MethodInfo> methods = new HashMap<String, MethodInfo>();

    public InterfaceInfo(final Class iface) {
        super();
        if (iface == null) {
            throw new IllegalArgumentException("The interface cannot be null");
        }
        this.name = iface.getName();
        // SECURITY
        /*
         * Permission: accessDeclaredMembers : Access denied
         * (java.lang.RuntimePermission accessDeclaredMembers)
         */
        Map idlNames = AccessController.doPrivileged(new PrivilegedAction<Map>() {
            public Map run() {
                return Java2IDLUtil.mapMethodToOperation(iface);
            }
        });
        Iterator i = idlNames.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry)i.next();
            Method method = (Method)entry.getKey();
            MethodInfo methodInfo = new MethodInfo(method);
            methodInfo.setIDLName((String) entry.getValue());
            methods.put(method.getName(), methodInfo);
        }
    }

    public static synchronized final InterfaceInfo getInstance(final Class iface) {
        InterfaceInfo info = (InterfaceInfo)INTERFACES.get(iface);
        if (info == null) {
            info = new InterfaceInfo(iface);
            INTERFACES.put(iface, info);
        }
        return info;
    }

    /**
     * @return
     */
    public Map<String, MethodInfo> getMethods() {
        return methods;
    }

    /**
     * @return
     */
    public MethodInfo getMethod(String name) {
        return (MethodInfo)methods.get(name);
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("interface ").append(name).append("{ \n");
        Iterator i = methods.values().iterator();
        while (i.hasNext()) {
            MethodInfo methodInfo = (MethodInfo)i.next();
            sb.append("\t").append(methodInfo).append("\n");
        }
        sb.append("};\n");
        return sb.toString();
    }
}
