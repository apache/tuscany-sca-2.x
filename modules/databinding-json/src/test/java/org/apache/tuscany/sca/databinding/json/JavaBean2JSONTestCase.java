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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.json.JSONObject;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class JavaBean2JSONTestCase {

    public static class MyBean {
        private String name;
        private int age;
        private boolean vip;
        private String friends[];
        private List<String> books;
        private YourBean you;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public boolean isVip() {
            return vip;
        }

        public void setVip(boolean vip) {
            this.vip = vip;
        }

        public String[] getFriends() {
            return friends;
        }

        public void setFriends(String[] friends) {
            this.friends = friends;
        }

        public List<String> getBooks() {
            return books;
        }

        public void setBooks(List<String> books) {
            this.books = books;
        }

        public YourBean getYou() {
            return you;
        }

        public void setYou(YourBean you) {
            this.you = you;
        }

    }

    public static class YourBean {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void testBean2JSON() throws Exception {
        MyBean me = new MyBean();
        me.setAge(30);
        me.setBooks(new ArrayList<String>());
        me.setFriends(new String[] {"John", "Mike"});
        me.setVip(true);
        me.setName("Me");
        YourBean you = new YourBean();
        you.setId(123);
        you.setName(null);
        me.setYou(you);
        JavaBean2JSON t1 = new JavaBean2JSON();
        Object result = t1.transform(me, null);
        System.out.println(result);
        JSON2JavaBean t2 = new JSON2JavaBean();
        TransformationContext context = new TransformationContextImpl();
        context.setTargetDataType(new DataTypeImpl(MyBean.class, null));
        Object v = t2.transform(new JSONObject(result.toString()), context);
        Assert.assertTrue(v instanceof MyBean);
        //        String json =
        //            "{\"age\":30,\"books\":[],\"friends\":[\"John\",\"Mike\"],\"name\":\"Me\",\"vip\":true,\"you\":{\"id\":123,\"name\":null}}";
        //        Assert.assertEquals(json, result.toString());
    }

    @Test
    public void testString2JSON() throws Exception {
        JavaBean2JSONObject t1 = new JavaBean2JSONObject();
        Object result = t1.transform("ABC", null);
        System.out.println(result);
        JSON2JavaBean t2 = new JSON2JavaBean();
        TransformationContext context = new TransformationContextImpl();
        context.setTargetDataType(new DataTypeImpl(String.class, null));
        Object v = t2.transform(result, context);
        Assert.assertTrue(v instanceof String);
        Assert.assertEquals("ABC", v);
    }

    @Test
    public void testStringArray2JSON() throws Exception {
        JavaBean2JSON t1 = new JavaBean2JSON();
        Object result = t1.transform(new String[] {"ABC", "DF"}, null);
        System.out.println(result);
        JSON2JavaBean t2 = new JSON2JavaBean();
        TransformationContext context = new TransformationContextImpl();
        context.setTargetDataType(new DataTypeImpl(String[].class, null));
        Object v = t2.transform(result, context);
        Assert.assertTrue(v instanceof String[]);
        String[] strs = (String[])v;
        Assert.assertEquals("ABC", strs[0]);
        Assert.assertEquals("DF", strs[1]);
    }

}
