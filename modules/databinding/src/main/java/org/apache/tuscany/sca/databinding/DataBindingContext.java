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

package org.apache.tuscany.sca.databinding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.apache.tuscany.sca.interfacedef.Operation;

/**
 * The context for databinding processing
 */
public class DataBindingContext {

    private String contentType;
    private Class<?> type;
    private Type genericType;
    private Annotation[] annotations;
    private Operation operation;

    public DataBindingContext(Class<?> type,
                              Type genericType,
                              Annotation[] annotations,
                              Operation operation,
                              String contentType) {
        super();
        this.type = type;
        this.genericType = genericType;
        this.annotations = annotations;
        this.operation = operation;
        this.contentType = contentType;
    }

    public DataBindingContext(Class<?> type, Type genericType, Annotation[] annotations) {
        super();
        this.type = type;
        this.genericType = genericType;
        this.annotations = annotations;
    }

    public DataBindingContext(Class<?> type) {
        super();
        this.type = type;
        this.genericType = type;
    }
    
    public Class<?> getType() {
        return type;
    }

    public Type getGenericType() {
        return genericType;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public Operation getOperation() {
        return operation;
    }

    public <A extends Annotation> A getAnnotation(Class<A> type) {
        if (annotations == null) {
            return null;
        }
        for (Annotation a : annotations) {
            if (a.annotationType() == type) {
                return type.cast(a);
            }
        }
        return null;
    }

    public String getContentType() {
        return contentType;
    }
}
