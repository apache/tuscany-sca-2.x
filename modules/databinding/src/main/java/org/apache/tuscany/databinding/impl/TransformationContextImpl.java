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
import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.spi.databinding.TransformationContext;

public class TransformationContextImpl implements TransformationContext {
    private DataType sourceDataType;

    private DataType targetDataType;

    private final Map<Class<?>, Object> metadata = new HashMap<Class<?>, Object>();

    private WeakReference<ClassLoader> classLoaderRef;

    public TransformationContextImpl() {
        super();
        setClassLoader(Thread.currentThread().getContextClassLoader());
    }

    public TransformationContextImpl(DataType sourceDataType,
                                     DataType targetDataType,
                                     ClassLoader classLoader,
                                     Map<Class<?>, Object> metadata) {
        super();
        this.sourceDataType = sourceDataType;
        this.targetDataType = targetDataType;
        setClassLoader(classLoader);
        if (metadata != null) {
            this.metadata.putAll(metadata);
        }
    }

    public DataType getSourceDataType() {
        return sourceDataType;
    }

    public DataType getTargetDataType() {
        return targetDataType;
    }

    public void setSourceDataType(DataType sourceDataType) {
        this.sourceDataType = sourceDataType;
    }

    public void setTargetDataType(DataType targetDataType) {
        this.targetDataType = targetDataType;
    }

    public final void setClassLoader(ClassLoader classLoader) {
        this.classLoaderRef = new WeakReference<ClassLoader>(classLoader);
    }

    public ClassLoader getClassLoader() {
        return classLoaderRef.get();
    }

    public Map<Class<?>, Object> getMetadata() {
        return metadata;
    }

}
