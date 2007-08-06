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

package org.apache.tuscany.sca.databinding.impl;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;

import org.apache.tuscany.sca.databinding.PushTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;

/**
 * A transformer facade allowing transformers to be lazily loaded
 * and initialized.
 *
 * @version $Rev$ $Date$
 */
public class LazyPushTransformer implements PushTransformer<Object, Object> {

    private String source;
    private String target;
    private int weight;
    private WeakReference<ClassLoader> classLoader;
    private String className;
    private PushTransformer<Object, Object> transformer;

    public LazyPushTransformer(String source, String target, String weight, ClassLoader classLoader, String className) {
        this.source = source;
        this.target = target;
        this.weight = Integer.valueOf(weight);
        this.classLoader = new WeakReference<ClassLoader>(classLoader);
        this.className = className;
    }

    /**
     * Load and instantiate the transformer class.
     * 
     * @return The transformer.
     */
    @SuppressWarnings("unchecked")
    private PushTransformer<Object, Object> getTransformer() {
        if (transformer == null) {
            try {
                Class<PushTransformer<Object, Object>> transformerClass =
                    (Class<PushTransformer<Object, Object>>)Class.forName(className, true, classLoader.get());
                Constructor<PushTransformer<Object, Object>> constructor = transformerClass.getConstructor();
                transformer = constructor.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return transformer;
    }

    public String getSourceDataBinding() {
        return source;
    }

    public String getTargetDataBinding() {
        return target;
    }

    public int getWeight() {
        return weight;
    }

    public void transform(Object source, Object sink, TransformationContext context) {
        getTransformer().transform(source, sink, context);
    }

}
