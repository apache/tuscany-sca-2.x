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

/**
 * %Z% %I% %W% %G% %U% [%H% %T%]
 *
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * HISTORY
 * CMVC Ref        Date      Who       Description
 * --------------- --------- --------- --------------------------------------------
 * 446019          06/14/07  skurz     Exploit the SOAFP extension for special handling of exceptions.
 *                                     We may or may not get Tuscany to pick this up.  We'll have to discuss
 */

package org.apache.tuscany.sca.databinding;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;

/**
 * The default implementation of a data binding extension point.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultDataBindingExtensionPoint implements DataBindingExtensionPoint {
    private final Map<String, DataBinding> bindings = new HashMap<String, DataBinding>();

    public DataBinding getDataBinding(String id) {
        if (id == null) {
            return null;
        }
        return bindings.get(id.toLowerCase());
    }

    public void addDataBinding(DataBinding dataBinding) {
        bindings.put(dataBinding.getName().toLowerCase(), dataBinding);
        String[] aliases = dataBinding.getAliases();
        if (aliases != null) {
            for (String alias : aliases) {
                bindings.put(alias.toLowerCase(), dataBinding);
            }
        }
    }

    public DataBinding removeDataBinding(String id) {
        if (id == null) {
            return null;
        }
        DataBinding dataBinding = bindings.remove(id.toLowerCase());
        if (dataBinding != null) {
            String[] aliases = dataBinding.getAliases();
            if (aliases != null) {
                for (String alias : aliases) {
                    bindings.remove(alias.toLowerCase());
                }
            }
        }
        return dataBinding;
    }

    private Set<DataBinding> getDataBindings() {
        return new HashSet<DataBinding>(bindings.values());
    }

    // 446019 - SOAFP -  Keep the old signature
    public boolean introspectType(DataType dataType, Annotation[] annotations) {
        return introspectType(dataType, annotations, false);
    }

    // 446019 - SOAFP-specific
    //
    // Leverage the DataBinding ExceptionHandler to calculate the DataType of an exception DataType
    //
    public boolean introspectType(DataType dataType, Annotation[] annotations, boolean isException) {
        for (DataBinding binding : getDataBindings()) {
            // don't introspect for JavaBeansDatabinding as all javatypes will
            // anyways match to its basetype
            // which is java.lang.Object. Default to this only if no databinding
            // results
            if (!binding.getName().equals(JavaBeansDataBinding.NAME)) {
                if (binding.introspect(dataType, annotations)) {
                    return true;
                }
                if (isException) {
                    // Next look to see if the DB's exceptionHandler handles this exception
                    ExceptionHandler excHandler = binding.getExceptionHandler();
                    if (excHandler !=null &&  excHandler.getFaultType(dataType) != null) {
                        // Assymetric to have the introspect() methods set the DataBindings themselves
                        // whereas we're setting it ourselves here.   
                        dataType.setDataBinding(binding.getName()); 
                        return true;
                    }
                }
            }
        }
        // FIXME: Should we honor the databinding from operation/interface
        // level?
        Class physical = dataType.getPhysical();
        if (physical == Object.class || Throwable.class.isAssignableFrom((Class)physical)) {
            return false;
        }
        dataType.setDataBinding(JavaBeansDataBinding.NAME);
        return false;
    }

    //
    // 446019 - Didn't bother to provide special exc-handling support for this method
    //
    public DataType introspectType(Object value) {
        DataType dataType = null;
        for (DataBinding binding : getDataBindings()) {
            // don't introspect for JavaBeansDatabinding as all javatypes will
            // anyways match to its basetype
            // which is java.lang.Object. Default to this only if no databinding
            // results
            if (!binding.getName().equals(JavaBeansDataBinding.NAME)) {
                dataType = binding.introspect(value);
            }
            if (dataType != null) {
                return dataType;
            }
        }
        return new DataTypeImpl<Class>(JavaBeansDataBinding.NAME, value.getClass(), value.getClass());
    }
}
