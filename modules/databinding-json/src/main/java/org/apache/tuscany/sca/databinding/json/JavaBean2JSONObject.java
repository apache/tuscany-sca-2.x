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

package org.apache.tuscany.sca.databinding.json;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.databinding.impl.SimpleTypeMapperImpl;
import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class JavaBean2JSONObject extends BaseTransformer<Object, Object> implements PullTransformer<Object, Object> {
    private static final Comparator<PropertyDescriptor> COMPARATOR = new Comparator<PropertyDescriptor>() {
        public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private static final SimpleTypeMapperImpl MAPPER = new SimpleTypeMapperImpl();
    private static final Object[] NULL = null;

    private static String getStringValue(Object o) {
        if (o == null) {
            return null;
        }
        TypeInfo info = SimpleTypeMapperImpl.getXMLType(o.getClass());
        if (info != null) {
            return MAPPER.toXMLLiteral(info.getQName(), o, null);
        } else {
            return String.valueOf(o);
        }
    }

    private static boolean isSimpleType(Class<?> javaType) {
        return SimpleTypeMapperImpl.getXMLType(javaType) != null;
    }

    public JavaBean2JSONObject() {
    }

    @Override
    public String getSourceDataBinding() {
        return JavaBeansDataBinding.NAME;
    }

    @Override
    protected Class<Object> getSourceType() {
        return Object.class;
    }

    @Override
    public String getTargetDataBinding() {
        return JSONDataBinding.NAME;
    }

    @Override
    protected Class<Object> getTargetType() {
        return Object.class;
    }

    public Object toJSON(Object source) throws Exception {
        if (source == null) {
            return JSONObject.NULL;
        }
        Class<?> type = source.getClass();
        if (isSimpleType(type)) {
            return source;
        } else if (type.isArray()) {
            JSONArray array = new JSONArray();
            int i1 = Array.getLength(source);
            for (int j = 0; j < i1; j++) {
                Object o = Array.get(source, j);
                array.put(toJSON(o));
            }
            return array;
        } else if (Collection.class.isAssignableFrom(type)) {
            Collection c = (Collection)source;
            JSONArray array = new JSONArray();
            for (Object element : c) {
                array.put(toJSON(element));
            }
            return array;
        }
        JSONObject json = new JSONObject();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] propDescs = beanInfo.getPropertyDescriptors();
        Collections.sort(Arrays.asList(propDescs), COMPARATOR);

        for (int i = 0; i < propDescs.length; i++) {
            PropertyDescriptor propDesc = propDescs[i];
            Class<?> pType = propDesc.getPropertyType();
            if ("class".equals(propDesc.getName())) {
                continue;
            }
            Object pValue = propDesc.getReadMethod().invoke(source, NULL);
            json.put(propDesc.getName(), toJSON(pValue));
        }
        return json;

    }

    public Object transform(Object source, TransformationContext context) {
        try {
            return toJSON(source);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

}
