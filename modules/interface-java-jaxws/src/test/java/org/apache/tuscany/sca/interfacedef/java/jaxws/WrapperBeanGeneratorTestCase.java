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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.bind.JAXBContext;

import org.apache.tuscany.sca.databinding.jaxb.JAXBContextHelper;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class WrapperBeanGeneratorTestCase {
    @Test
    public void testGenerate() throws Exception {
        List<Class<?>> classes = WrapperBeanGenerator.generateWrapperBeans(TestInterface.class);
        for (Class<?> cls : classes) {
            for (Field f : cls.getDeclaredFields()) {
                // System.out.println(f.getName());
                for (Annotation a : f.getAnnotations()) {
                    // System.out.println(a);
                }
            }
            for (Method m : cls.getDeclaredMethods()) {
                // System.out.println(m);
                for (Annotation a : m.getAnnotations()) {
                    // System.out.println(a);
                }
            }
            Object obj = cls.newInstance();
            JAXBContext context = JAXBContextHelper.createJAXBContext(cls);
            StringWriter sw = new StringWriter();
            context.createMarshaller().marshal(obj, sw);
            System.out.println(sw.toString());
        }
    }
}
