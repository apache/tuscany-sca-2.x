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
package org.apache.tuscany.sca.databinding.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.custommonkey.xmlunit.Diff;

/**
 *
 * @version $Rev$ $Date$
 */
public class BeanXMLStreamReaderTestCase extends TestCase {
    private static final String XML_RESULT =
        "<?xml version='1.0' encoding='UTF-8'?>" + "<MyBean xmlns=\"http://xml.databinding.sca.tuscany.apache.org/\">"
            + "<arr>1</arr><arr>2</arr><arr>3</arr><bean><name>Name</name></bean><i>1</i>"
            + "<list>Item1</list><list>Item2</list>"
            + "<map><entry><key>key1</key><value>value1</value></entry>"
            + "<entry><key>key2</key><value>value2</value></entry></map>"
            + "<nil xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\" />"
            + "<str>ABC</str></MyBean>";

    // The map entries can come in a different order
    private static final String XML_RESULT1 =
        "<?xml version='1.0' encoding='UTF-8'?>" + "<MyBean xmlns=\"http://xml.databinding.sca.tuscany.apache.org/\">"
            + "<arr>1</arr><arr>2</arr><arr>3</arr><bean><name>Name</name></bean><i>1</i>"
            + "<list>Item1</list><list>Item2</list>"
            + "<map><entry><key>key2</key><value>value2</value></entry>"
            + "<entry><key>key1</key><value>value1</value></entry></map>"
            + "<nil xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\" />"
            + "<str>ABC</str></MyBean>";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testTransformation() throws Exception {
        MyBean bean = new MyBean();
        bean.str = "ABC";
        bean.i = 1;
        bean.arr = new long[] {1, 2, 3};
        bean.bean = new AnotherBean();
        bean.bean.setName("Name");
        bean.list.add("Item1");
        bean.list.add("Item2");
        bean.map.put("key1", "value1");
        bean.map.put("key2", "value2");
        XMLStreamReader reader = new BeanXMLStreamReaderImpl(null, bean);
        XMLStreamReader2String t3 = new XMLStreamReader2String();
        String xml = t3.transform(reader, null);
        Diff diff = new Diff(XML_RESULT, xml);
        Diff diff1 = new Diff(XML_RESULT1, xml);
        assertTrue(diff.similar() || diff1.similar());
    }

    private static class MyBean {
        private long arr[];
        private String str;
        private int i;
        private String nil;
        private List<String> list = new ArrayList();
        private AnotherBean bean;
        private Map<String, String> map = new HashMap<String, String>();

        /**
         * @return the i
         */
        public int getI() {
            return i;
        }

        /**
         * @param i the i to set
         */
        public void setI(int i) {
            this.i = i;
        }

        /**
         * @return the arr
         */
        public long[] getArr() {
            return arr;
        }

        /**
         * @param arr the arr to set
         */
        public void setArr(long[] arr) {
            this.arr = arr;
        }

        /**
         * @return the str
         */
        public String getStr() {
            return str;
        }

        /**
         * @param str the str to set
         */
        public void setStr(String str) {
            this.str = str;
        }

        /**
         * @return the bean
         */
        public AnotherBean getBean() {
            return bean;
        }

        /**
         * @param bean the bean to set
         */
        public void setBean(AnotherBean bean) {
            this.bean = bean;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public String getNil() {
            return nil;
        }

        public void setNil(String nil) {
            this.nil = nil;
        }

        public Map<String, String> getMap() {
            return map;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }

    }

    private static class AnotherBean {
        private String name;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }
    }

}
