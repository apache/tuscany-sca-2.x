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
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sdo.api.SDOUtil;

import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.impl.HelperProvider;

/**
 * Helper class to get TypeHelper from the context
 *
 * @version $Rev$ $Date$
 */
public final class SDOContextHelper {
    private SDOContextHelper() {
    }

    public static HelperContext getHelperContext(TransformationContext context) {
        if (context == null) {
            return getDefaultHelperContext();
        }

        HelperContext helperContext = (HelperContext)context.getMetadata().get(HelperContext.class.getName());
        if (helperContext != null) {
            return helperContext;
        }
        helperContext = SDOUtil.createHelperContext();

        boolean found = false;
        Operation op = context.getSourceOperation();
        if (op != null) {
            found = register(helperContext, op.getInputType()) || found;
            found = register(helperContext, op.getOutputType()) || found;
        } else {
            found = register(helperContext, context.getSourceDataType()) || found;
        }

        op = context.getTargetOperation();
        if (op != null) {
            found = register(helperContext, op.getInputType()) || found;
            found = register(helperContext, op.getOutputType()) || found;
        } else {
            found = register(helperContext, context.getTargetDataType()) || found;
        }

        if (!found) {
            helperContext = getDefaultHelperContext();
        }

        context.getMetadata().put(HelperContext.class.getName(), helperContext);
        return helperContext;

    }

    /**
     * @param helperContext
     * @param dataType
     * @return
     */
    private static boolean register(HelperContext helperContext, DataType dataType) {
        if (dataType == null) {
            return false;
        }
        String db = dataType.getDataBinding();
        boolean found = false;
        if (DataBinding.IDL_INPUT.equals(db) || DataBinding.IDL_OUTPUT.equals(db)
            || DataBinding.IDL_FAULT.equals(db)
            || SDODataBinding.NAME.equals(db)) {
            Class javaType = dataType.getPhysical();
            found = register(helperContext, javaType);
            if (dataType.getLogical() instanceof DataType) {
                DataType logical = (DataType)dataType.getLogical();
                found = register(helperContext, logical.getPhysical()) || found;
            }
            if (dataType.getLogical() instanceof List) {
                List types = (List)dataType.getLogical();
                for (Object type : types) {
                    if (type instanceof DataType) {
                        found = register(helperContext, ((DataType)type)) || found;
                    }
                }
            }
        }
        return found;
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

    public static QName getElement(TransformationContext context) {
        if (context == null) {
            return SDODataBinding.ROOT_ELEMENT;
        }
        DataType<?> dataType = context.getTargetDataType();
        Object logical = dataType.getLogical();
        QName elementName = null;
        if (logical instanceof XMLType) {
            XMLType xmlType = (XMLType)logical;
            QName element = xmlType.getElementName();
            if (element != null) {
                elementName = element;
            }
        }
        if (elementName == null) {
            // Try source type
            dataType = context.getSourceDataType();
            logical = dataType.getLogical();
            if (logical instanceof XMLType) {
                XMLType xmlType = (XMLType)logical;
                QName element = xmlType.getElementName();
                if (element != null) {
                    elementName = element;
                }
            }
        }
        if (elementName != null) {
            return elementName;
        } else {
            return SDODataBinding.ROOT_ELEMENT;
        }
    }

    public static String generateSchema(HelperContext context, Class<?>[] classes) {
        TypeHelper typeHelper = context.getTypeHelper();
        List<Type> types = new ArrayList<Type>();
        for (Class<?> cls : classes) {
            Type type = typeHelper.getType(cls);
            if (type != null) {
                types.add(type);
            }
        }
        return generateSchema(context, types);
    }

    public static String generateSchema(HelperContext context, List<Type> types) {
        // FIXME: SDO throws IllegalArgumentException for types generated from existing XSDs
        return context.getXSDHelper().generate(types);
    }
}
