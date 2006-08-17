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

import java.lang.ref.WeakReference;

import org.apache.tuscany.databinding.DataBinding;
import org.apache.tuscany.databinding.TransformationContext;

public class TransformationContextImpl implements TransformationContext {
    private DataBinding sourceDataBinding;

    private DataBinding targetDataBinding;

    private WeakReference<ClassLoader> classLoaderRef;

    public TransformationContextImpl() {
        super();
        setClassLoader(Thread.currentThread().getContextClassLoader());
    }

    public TransformationContextImpl(DataBinding sourceDataBinding, DataBinding targetDataBinding, ClassLoader classLoader) {
        super();
        this.sourceDataBinding = sourceDataBinding;
        this.targetDataBinding = targetDataBinding;
        setClassLoader(classLoader);
    }

    public DataBinding getSourceDataBinding() {
        return sourceDataBinding;
    }

    public DataBinding getTargetDataBinding() {
        return targetDataBinding;
    }

    public void setSourceDataBinding(DataBinding sourceDataBinding) {
        this.sourceDataBinding = sourceDataBinding;
    }

    public void setTargetDataBinding(DataBinding targetDataBinding) {
        this.targetDataBinding = targetDataBinding;
    }

    final public void setClassLoader(ClassLoader classLoader) {
        this.classLoaderRef = new WeakReference<ClassLoader>(classLoader);
    }

    public ClassLoader getClassLoader() {
        return classLoaderRef.get();
    }

}
