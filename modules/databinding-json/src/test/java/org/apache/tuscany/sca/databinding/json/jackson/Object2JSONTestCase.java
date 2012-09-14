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

package org.apache.tuscany.sca.databinding.json.jackson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.json.JSONObject;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class Object2JSONTestCase {

    public static class MyBean {
        private String name;
        private int age;
        private boolean vip;
        private String friends[];
        private List<String> books;
        private YourBean you;
        private Date date;

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

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + age;
            result = prime * result + ((books == null) ? 0 : books.hashCode());
            result = prime * result + ((date == null) ? 0 : date.hashCode());
            result = prime * result + Arrays.hashCode(friends);
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + (vip ? 1231 : 1237);
            result = prime * result + ((you == null) ? 0 : you.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            MyBean other = (MyBean)obj;
            if (age != other.age) {
                return false;
            }
            if (books == null) {
                if (other.books != null) {
                    return false;
                }
            } else if (!books.equals(other.books)) {
                return false;
            }
            if (date == null) {
                if (other.date != null) {
                    return false;
                }
            } else if (!date.equals(other.date)) {
                return false;
            }
            if (!Arrays.equals(friends, other.friends)) {
                return false;
            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            if (vip != other.vip) {
                return false;
            }
            if (you == null) {
                if (other.you != null) {
                    return false;
                }
            } else if (!you.equals(other.you)) {
                return false;
            }
            return true;
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + id;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            YourBean other = (YourBean)obj;
            if (id != other.id) {
                return false;
            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            return true;
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
        me.setDate(new Date());
        YourBean you = new YourBean();
        you.setId(123);
        you.setName(null);
        me.setYou(you);
        Object2JSON t1 = new Object2JSON();
        Object result = t1.transform(me, null);
        System.out.println(result);
        JSON2Object t2 = new JSON2Object();
        TransformationContext context = new TransformationContextImpl();
        context.setTargetDataType(new DataTypeImpl(MyBean.class, null));
        Object v = t2.transform(result.toString(), context);
        Assert.assertTrue(v instanceof MyBean);
        //        String json =
        //            "{\"age\":30,\"books\":[],\"friends\":[\"John\",\"Mike\"],\"name\":\"Me\",\"vip\":true,\"you\":{\"id\":123,\"name\":null}}";
        //        Assert.assertEquals(json, result.toString());
        Assert.assertEquals(v, me);
    }

    @Test
    public void testBean2JSONWithFilter() throws Exception {
        MyBean me = new MyBean();
        me.setAge(30);
        me.setBooks(new ArrayList<String>());
        me.setFriends(new String[] {"John", "Mike"});
        me.setVip(true);
        me.setName("Me");
        me.setDate(new Date());
        YourBean you = new YourBean();
        you.setId(123);
        you.setName("You");
        me.setYou(you);
        Object2JSON t1 = new Object2JSON();
        TransformationContext context = new TransformationContextImpl();
        Set<String> included = new HashSet<String>();
        included.add("name");
        included.add("you.name");
        // included.add("you.id");
        context.getMetadata().put("includedFields", included);
        Object result = t1.transform(me, context);
        System.out.println(result);
        JSONObject json = new JSONObject(result.toString());
        Assert.assertTrue(json.has("name"));
        Assert.assertTrue(json.has("you"));
        Assert.assertTrue(json.getJSONObject("you").has("name"));
        Assert.assertFalse(json.getJSONObject("you").has("id"));
        context = new TransformationContextImpl();
        Set<String> excluded = new HashSet<String>();
        excluded.add("you.name");
        excluded.add("age");
        context.getMetadata().put("excludedFields", excluded);
        result = t1.transform(me, context);
        System.out.println(result);
        json = new JSONObject(result.toString());
        Assert.assertTrue(json.has("name"));
        Assert.assertTrue(json.has("you"));
        Assert.assertTrue(json.getJSONObject("you").has("id"));
        Assert.assertFalse(json.getJSONObject("you").has("name"));
    }

    @Test
    public void testString2JSON() throws Exception {
        Object2JSON t1 = new Object2JSON();
        Object result = t1.transform("ABC", null);
        System.out.println(result);
        JSON2Object t2 = new JSON2Object();
        TransformationContext context = new TransformationContextImpl();
        context.setTargetDataType(new DataTypeImpl(String.class, null));
        Object v = t2.transform(result, context);
        Assert.assertTrue(v instanceof String);
        Assert.assertEquals("ABC", v);
    }

    @Test
    public void testStringArray2JSON() throws Exception {
        Object2JSON t1 = new Object2JSON();
        Object result = t1.transform(new String[] {"ABC", "DF"}, null);
        System.out.println(result);
        JSON2Object t2 = new JSON2Object();
        TransformationContext context = new TransformationContextImpl();
        context.setTargetDataType(new DataTypeImpl(String[].class, null));
        Object v = t2.transform(result, context);
        Assert.assertTrue(v instanceof String[]);
        String[] strs = (String[])v;
        Assert.assertEquals("ABC", strs[0]);
        Assert.assertEquals("DF", strs[1]);
    }

}
