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
import java.util.Set;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.common.java.collection.LRUCache;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.util.DataTypeHelper;
import org.apache.tuscany.sca.extensibility.ClassLoaderContext;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.apache.tuscany.sdo.api.SDOUtil;

import commonj.sdo.DataObject;
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
    private static final LRUCache<Object, HelperContext> cache = new LRUCache<Object, HelperContext>(1024);
    
    private static ExtensionPointRegistry registry = null;
    private static HelperContext defaultHelperContext;

    private SDOContextHelper() {
    }
    
    /**
     * Static method used to set the registry used to locate the SDO implementation provider
     * MUST be called before using any other methods on this class
     * @param theRegistry
     */
    public static void setRegistry( ExtensionPointRegistry theRegistry ) {
    	registry = theRegistry;
    } // end setRegistry

    public static HelperContext getHelperContext(TransformationContext context, boolean source) {
        if (context == null) {
            return getDefaultHelperContext();
        }

        HelperContext helperContext = null;
        Operation op = source ? context.getSourceOperation() : context.getTargetOperation();
        if (op == null) {
            DataType<?> dt = source ? context.getSourceDataType() : context.getTargetDataType();

            helperContext = dt.getMetaData(HelperContext.class);
            if (helperContext != null) {
                return helperContext;
            }
            helperContext = SDOUtil.createHelperContext();
            boolean found = register(helperContext, dt);
            if (!found) {
                helperContext = getDefaultHelperContext();
            }
            dt.setMetaData(HelperContext.class, helperContext);
            return helperContext;
        } else {
            return getHelperContext(op);
        }

    }

    public static HelperContext getHelperContext(Operation op) {
        if (op == null) {
            return getDefaultHelperContext();
        }

        HelperContext helperContext = op.getInputType().getMetaData(HelperContext.class);

        if (helperContext != null) {
            return helperContext;
        }

        // Use the default HelperContext until a type is registered later on
        helperContext = getDefaultHelperContext();

        /*
        List<DataType> dataTypes = DataTypeHelper.getDataTypes(op, false);
        boolean found = false;
        for (DataType d : dataTypes) {
            if (register(helperContext, d)) {
                found = true;
            }
        }
        if (!found) {
            helperContext = getDefaultHelperContext();
        }
        */
        op.getInputType().setMetaData(HelperContext.class, helperContext);
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
        Set<Class<?>> classes = DataTypeHelper.findClasses(dataType);
        boolean found = false;
        for (Class<?> cls : classes) {
            found = register(helperContext, dataType.getPhysical()) || found;
        }
        return found;
    }

    /**
     * FIXME: [rfeng] This is a hack to get the factory out a SDO class
     * @param helperContext
     * @param javaType
     */

    public static boolean register(HelperContext helperContext, Class javaType) {
        if (javaType == null || DataObject.class == javaType) {
            return false;
        }
        try {
            Type type = helperContext.getTypeHelper().getType(javaType);
            return register(helperContext, type);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public static boolean register(HelperContext helperContext, Type type) {
        if (type != null && (!type.isDataType())) {
            try {
                Method method = type.getClass().getMethod("getEPackage");
                Object factory = method.invoke(type, new Object[] {});
                method = factory.getClass().getMethod("register", HelperContext.class);
                method.invoke(factory, new Object[] {helperContext});
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public static HelperContext getDefaultHelperContext( ) {
        // Return a chached value if available...
    	if( defaultHelperContext != null ) return defaultHelperContext;
    	
        // Try to set up TCCL so that SDO Helper Provider service discovery works in OSGi
    	if( registry == null ) return null;
    	
        ClassLoader oldTccl =
            ClassLoaderContext.setContextClassLoader(SDOContextHelper.class.getClassLoader(),
                                                     registry.getServiceDiscovery(),
                                                     // SDO Helper Provider
                                                     "commonj.sdo.impl.HelperProvider"
                                                     );
        try {
        	// Load the HelperProvider (using the new TCCL) and get the default HelperContext
        	// cache the returned HelperContext...
        	ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        	HelperProvider.setDefaultInstance(tccl);
        	defaultHelperContext = HelperProvider.getDefaultContext();
        	return defaultHelperContext;
        } catch (Exception e ){
        	e.printStackTrace();
        	return null;
        } finally {
            if (oldTccl != null) {
                Thread.currentThread().setContextClassLoader(oldTccl);
            }
        } // end try
    } // end getDefaultHelperContext()

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
