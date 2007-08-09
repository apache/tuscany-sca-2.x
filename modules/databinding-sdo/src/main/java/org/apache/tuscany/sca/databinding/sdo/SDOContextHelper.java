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

package org.apache.tuscany.sca.databinding.sdo;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sdo.api.SDOUtil;

import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;

/**
 * Helper class to get TypeHelper from the context
 */
public final class SDOContextHelper {
    private SDOContextHelper() {
    }

    public static HelperContext getHelperContext(TransformationContext context) {
        if (context == null) {
            return getDefaultHelperContext();
        }
        HelperContext helperContext = SDOUtil.createHelperContext();
        Class javaType = context.getTargetDataType().getPhysical();
        boolean found = register(helperContext, javaType);
        javaType = context.getSourceDataType().getPhysical();
        found = found || register(helperContext, javaType);
        if (found) {
            return helperContext;
        } else {
            return getDefaultHelperContext();
        }

    }

    /**
     * FIXME: [rfeng] This is a hack to get the factory out a SDO class
     * @param helperContext
     * @param javaType
     */

    private static boolean register(HelperContext helperContext, Class javaType) {
        try {
            Type type = helperContext.getTypeHelper().getType(javaType);
            if (type != null && (!type.isDataType())) {
                Method method = type.getClass().getMethod("getEPackage", new Class[] {});
                Object factory = method.invoke(type, new Object[] {});
                method = factory.getClass().getMethod("register", new Class[] {HelperContext.class});
                method.invoke(factory, new Object[] {helperContext});
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public static HelperContext getDefaultHelperContext() {
        // SDOUtil.createHelperContext();
        return HelperProvider.getDefaultContext();
    }

    public static QName getElement(DataType<?> dataType) {
        Object logical = dataType.getLogical();
        QName elementName = SDODataBinding.ROOT_ELEMENT;
        if (logical instanceof XMLType) {
            XMLType xmlType = (XMLType)logical;
            QName element = xmlType.getElementName();
            if (element != null) {
                elementName = element;
            }
        }
        return elementName;
    }
}
