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

import java.lang.reflect.Array;

import junit.framework.TestCase;

import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;

public class POJOTestCase extends TestCase {
    public void testPOJO() throws Exception {
        MyBean bean = new MyBean();
        bean.setName("Test");
        bean.setAge(20);
        bean.getNotes().add("1");
        bean.getNotes().add("2");
        bean.getMap().put("1", 1);
        MyInterface service = new MyInterfaceImpl();
        service.setId("ID001");
        bean.setService(service);
        bean.setOtherService(service);

        roundTrip(bean);
    }

    private <T> void roundTrip(T bean) {
        JavaBean2JSON t1 = new JavaBean2JSON();

        Object json = t1.transform(bean, null);
        System.out.println(json);
        JSON2JavaBean t2 = new JSON2JavaBean();

        TransformationContext context = new TransformationContextImpl();
        context.setTargetDataType(new DataTypeImpl(bean == null ? Object.class : bean.getClass(), null));
        Object newBean = t2.transform(json, context);

        if (newBean != null && newBean.getClass().isArray()) {
            int len = Array.getLength(newBean);
            assertEquals(Array.getLength(bean), len);
            for (int i = 0; i < len; i++) {
                assertEquals(Array.get(bean, i), Array.get(newBean, i));
            }
            return;
        }
        assertEquals(bean, newBean);
    }

    public void testString() throws Exception {
        roundTrip("ABC");
    }

    public void testNull() throws Exception {
        roundTrip(null);
    }

    public void testArray() throws Exception {
        roundTrip(new String[] {"123", "ABC"});
    }

    public void testByteArray() throws Exception {
        roundTrip("ABC".getBytes());
    }

    public void testPrimitive() throws Exception {
        roundTrip(123);
    }

}
