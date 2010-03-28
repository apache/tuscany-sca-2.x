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

import java.util.Collection;

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
public class Collection2CollectionTransformer extends BaseTransformer<Collection, Collection> implements
    PullTransformer<Collection, Collection> {

    private static final String JAVA_COLLECTION = "java:collection";
    protected Mediator mediator;

    public Collection2CollectionTransformer(ExtensionPointRegistry registry) {
        super();
        this.mediator = registry.getExtensionPoint(UtilityExtensionPoint.class).getUtility(Mediator.class);
    }

    @Override
    public String getSourceDataBinding() {
        return JAVA_COLLECTION;
    }

    @Override
    public String getTargetDataBinding() {
        return JAVA_COLLECTION;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.BaseTransformer#getSourceType()
     */
    @Override
    protected Class<Collection> getSourceType() {
        return Collection.class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.BaseTransformer#getTargetType()
     */
    @Override
    protected Class<Collection> getTargetType() {
        return Collection.class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.Transformer#getWeight()
     */
    @Override
    public int getWeight() {
        return 10;
    }

    @SuppressWarnings("unchecked")
    public Collection transform(Collection sourceCollection, TransformationContext context) {
        try {
            if (sourceCollection == null) {
                return null;
            }
            DataType<DataType> sourceType = context.getSourceDataType();
            DataType<DataType> targetType = context.getTargetDataType();
            Collection targetCollection = createCollection(targetType.getPhysical());
            for (Object sourceItem : sourceCollection) {
                Object targetItem =
                    mediator.mediate(sourceItem, sourceType.getLogical(), targetType.getLogical(), context
                        .getMetadata());
                targetCollection.add(targetItem);
            }
            return targetCollection;
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    private Collection createCollection(Class<?> collectionClass) throws Exception {
        return (Collection) collectionClass.newInstance();
    }

}
