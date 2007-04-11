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

package org.apache.tuscany.databinding.sdo;

import java.awt.Component;

import javax.xml.namespace.QName;

import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.util.XMLType;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.apache.tuscany.spi.databinding.TransformationContext;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;

/**
 * Helper class to get TypeHelper from the context
 */
public final class SDOContextHelper {
    private SDOContextHelper() {
    }

    public static HelperContext getHelperContext(TransformationContext context) {
        if (context == null || context.getMetadata() == null) {
            return getDefaultHelperContext();
        }
        HelperContext helperContext = null;
        Component composite = (Component)context.getMetadata().get(Component.class);
        if (composite != null) {
//            SDOHelperContext sdoContext =
//                (SDOHelperContext)composite.getExtensions().get(HelperContext.class.getName());
//            if (sdoContext != null) {
//                helperContext = sdoContext.getHelperContext();
//            }
//            AtomicComponent child = (AtomicComponent)composite.getSystemChild(HelperContext.class.getName());
//            try {
//                helperContext = (HelperContext)child.getTargetInstance();
//            } catch (TargetResolutionException e) {
//                helperContext = null;
//            }
        }
        if (helperContext == null) {
            return getDefaultHelperContext();
        } else {
            return helperContext;
        }
    }

    public static HelperContext getHelperContext(ModelObject model) {
        HelperContext helperContext = null;
        if (model instanceof Composite) {
            // HACK: Retrieve the SDO HelperContext from the
            // CompositeComponentType
            // extensions
            helperContext = (HelperContext)model.getExtensions().get(ImportSDO.IMPORT_SDO);
            if (helperContext == null) {
                helperContext = SDOUtil.createHelperContext();
                ((CompositeComponentType<?, ?, ?>)model).getExtensions().put(ImportSDO.IMPORT_SDO,
                                                                                     helperContext);
            }
        }

        if (helperContext == null) {
            helperContext = getDefaultHelperContext();
        }

        return helperContext;
    }

    protected static HelperContext getDefaultHelperContext() {
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
