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

package org.apache.tuscany.sca.implementation.web.runtime.utils;

import javax.servlet.ServletContext;

import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.oasisopen.sca.ComponentContext;
import org.oasisopen.sca.ServiceReference;
import org.oasisopen.sca.ServiceRuntimeException;

public class ContextHelper {
    
    public static final String COMPONENT_ATTR = "org.apache.tuscany.sca.implementation.web.RuntimeComponent";
    
    public static ComponentContext getComponentContext(ServletContext sc) {
        RuntimeComponent rc = (RuntimeComponent)sc.getAttribute(COMPONENT_ATTR);
        return rc.getComponentContext();
    }

    public static <T> T getReference(String name, Class<T> type, ServletContext sc) {
        ServiceReference<T> sr = getComponentContext(sc).getServiceReference(type, name);
        if (sr == null) {
            throw new ServiceRuntimeException("Reference '" + name + "' undefined");
        }
        return sr.getService();
    }
}
