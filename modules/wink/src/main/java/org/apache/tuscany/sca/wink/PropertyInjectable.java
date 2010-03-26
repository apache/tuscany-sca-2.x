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

package org.apache.tuscany.sca.wink;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;

import org.apache.tuscany.sca.implementation.web.runtime.utils.ContextHelper;
import org.apache.wink.common.RuntimeContext;
import org.apache.wink.common.internal.registry.Injectable;
import org.oasisopen.sca.annotation.Property;

public class PropertyInjectable extends Injectable {

    private String name;

    public PropertyInjectable(Property propertyAnnotation,
                                  ParamType paramType,
                                  Class<?> type,
                                  Type genericType,
                                  Annotation[] annotations,
                                  Member member) {
        super(paramType, type, genericType, annotations, member);
        this.name = getPropertyName(propertyAnnotation.name(), member);
    }

    @Override
    public Object getValue(RuntimeContext runtimeContext) throws IOException {
        return ContextHelper.getProperty(name, runtimeContext.getAttribute(ServletContext.class));
    }

    private String getPropertyName(String annotationName, Member member) {
        String name;
        if (annotationName != null && !annotationName.equals("")) {
            name = annotationName;
        } else {
            name = getMember().getName();
            if (name.startsWith("set")) {
                name = Introspector.decapitalize(name.substring(3));
            }
        }
        return name;
    }
}
