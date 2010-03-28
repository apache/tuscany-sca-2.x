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
package org.apache.tuscany.sca.core.databinding.transformers;

import java.util.Map;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.databinding.BaseTransformer;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.interfacedef.DataType;

/**
 * This is a special transformer to transform the output from one IDL to the
 * other one
 *
 * @version $Rev$ $Date$
 */
public class Map2MapTransformer extends BaseTransformer<Map, Map> implements PullTransformer<Map, Map> {

    private static final String JAVA_MAP = "java:map";
    protected Mediator mediator;

    public Map2MapTransformer(ExtensionPointRegistry registry) {
        super();
        this.mediator = registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(Mediator.class);
    }

    @Override
    public String getSourceDataBinding() {
        return JAVA_MAP;
    }

    @Override
    public String getTargetDataBinding() {
        return JAVA_MAP;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.BaseTransformer#getSourceType()
     */
    @Override
    protected Class<Map> getSourceType() {
        return Map.class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.BaseTransformer#getTargetType()
     */
    @Override
    protected Class<Map> getTargetType() {
        return Map.class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.Transformer#getWeight()
     */
    @Override
    public int getWeight() {
        return 10;
    }

    @SuppressWarnings("unchecked")
    public Map transform(Map sourceMap, TransformationContext context) {
        try {
            if (sourceMap == null) {
                return null;
            }
            DataType<DataType> sourceType = context.getSourceDataType();
            DataType<DataType> targetType = context.getTargetDataType();
            Map targetMap = createMap(targetType.getPhysical());
            for (Object sourceItem : sourceMap.entrySet()) {
                Map.Entry entry = (Map.Entry)sourceItem;
                Object targetValue =
                    mediator.mediate(entry.getValue(), sourceType.getLogical(), targetType.getLogical(), context
                        .getMetadata());
                targetMap.put(entry.getKey(), targetValue);
            }
            return targetMap;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    private Map createMap(Class<?> collectionClass) throws Exception {
        return (Map)collectionClass.newInstance();
    }

}
