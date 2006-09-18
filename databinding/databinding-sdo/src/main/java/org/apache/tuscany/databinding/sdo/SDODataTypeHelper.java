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

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.sdo.ImportSDOLoader.SDOType;
import org.apache.tuscany.spi.component.CompositeComponent;

import commonj.sdo.helper.TypeHelper;

/**
 * Helper class to get TypeHelper from the transformation context
 */
public class SDODataTypeHelper {
    private SDODataTypeHelper() {
    }

    public static TypeHelper getTypeHelper(TransformationContext context) {
        TypeHelper typeHelper = null;
        if (context == null || context.getMetadata() == null) {
            return TypeHelper.INSTANCE;
        }
        CompositeComponent composite = (CompositeComponent) context.getMetadata().get(CompositeComponent.class);
        if (composite != null) {
            SDOType sdoType = (SDOType) composite.getExtensions().get(SDOType.class);
            if (sdoType != null) {
                typeHelper = sdoType.getTypeHelper();
            }
        }
        if (typeHelper == null) {
            return TypeHelper.INSTANCE;
        }
        return typeHelper;
    }
}
