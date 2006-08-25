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
package org.apache.tuscany.databinding.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.tuscany.databinding.DataPipe;
import org.apache.tuscany.databinding.Mediator;
import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.PushTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.Transformer;
import org.apache.tuscany.databinding.TransformerRegistry;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.model.DataType;
import org.osoa.sca.annotations.Scope;

/**
 * 
 */
@Scope("MODULE")
public class MediatorImpl implements Mediator {

    private TransformerRegistry transformerRegistry;

    @Autowire
    public void setTransformerRegistry(TransformerRegistry transformerRegistry) {
        this.transformerRegistry = transformerRegistry;
    }

    /**
     * @see org.apache.tuscany.databinding.Mediator#mediate(java.lang.Object, org.apache.tuscany.spi.model.DataType,
     *      org.apache.tuscany.spi.model.DataType)
     */
    @SuppressWarnings("unchecked")
    public Object mediate(Object source, DataType sourceDataType, DataType targetDataType) {
        List<Transformer> path = getTransformerChain(sourceDataType, targetDataType);
        
        Object result = source;
        for (Iterator<Transformer> i = path.iterator(); i.hasNext();) {
            Transformer transformer = i.next();
            // FIXME: We probably need to reset the context for each transformation on the path to reflect
            // the source and target type
            if (transformer instanceof PullTransformer) {
                TransformationContext context = new TransformationContextImpl(sourceDataType, targetDataType, Thread.currentThread()
                        .getContextClassLoader());
                result = ((PullTransformer) transformer).transform(result, context);
            } else if (transformer instanceof PushTransformer) {
                TransformationContext context = new TransformationContextImpl(sourceDataType, targetDataType, Thread.currentThread()
                        .getContextClassLoader());
                DataPipe dataPipe = i.hasNext() ? (DataPipe) i.next() : null;
                ((PushTransformer) transformer).transform(result, dataPipe.getSink(), context);
                result = dataPipe.getResult();
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public void mediate(Object source, Object target, DataType sourceDataType, DataType targetDataType) {
        List<Transformer> path = getTransformerChain(sourceDataType, targetDataType);

        Object result = source;
        for (Iterator<Transformer> i = path.iterator(); i.hasNext();) {
            Transformer transformer = i.next();
            if (transformer instanceof PullTransformer) {
                TransformationContext context = new TransformationContextImpl(sourceDataType, targetDataType, Thread.currentThread()
                        .getContextClassLoader());
                result = ((PullTransformer) transformer).transform(result, context);
            } else if (transformer instanceof PushTransformer) {
                TransformationContext context = new TransformationContextImpl(sourceDataType, targetDataType, Thread.currentThread()
                        .getContextClassLoader());
                DataPipe dataPipe = i.hasNext() ? (DataPipe) i.next() : null;
                Object sink = dataPipe != null ? dataPipe.getSink() : target;
                ((PushTransformer) transformer).transform(result, sink, context);
                result = (dataPipe != null) ? dataPipe.getResult() : null;
            }
        }
    }

    private List<Transformer> getTransformerChain(DataType sourceDataType, DataType targetDataType) {
        String sourceId = sourceDataType.getDataBinding();
        String targetId = targetDataType.getDataBinding();
        List<Transformer> path = transformerRegistry.getTransformerChain(sourceId, targetId);
        if (path == null) {
            TransformationException ex = new TransformationException("No path found for the transformation");
            ex.addContextName("Source: " + sourceId);
            ex.addContextName("Target: " + targetId);
            throw ex;
        }
        return path;
    }

}
