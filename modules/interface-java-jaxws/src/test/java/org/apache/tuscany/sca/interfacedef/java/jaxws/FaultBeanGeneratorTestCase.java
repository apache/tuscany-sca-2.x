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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;

import javax.xml.bind.JAXBContext;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.databinding.jaxb.JAXBContextHelper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;

/**
 * @version $Rev$ $Date$
 */
public class FaultBeanGeneratorTestCase extends TestCase {
    public void testGenerate() throws IOException {
        byte[] content = new FaultBeanGenerator().generate(MyException.class);
        ClassReader cr = new ClassReader(content);
        PrintWriter pw = new PrintWriter(System.out);
        CheckClassAdapter.verify(cr, false, pw);
    }

    public void testGenerateClass() throws Exception {
        Class<?> cls = FaultBeanGenerator.generateFaultBeanClass(MyException.class);
        Assert.assertEquals("org.apache.tuscany.sca.interfacedef.java.jaxws.jaxws.MyExceptionBean", cls.getName());
        for (Annotation a : cls.getAnnotations()) {
            System.out.println(a);
        }
        //        XmlType xmlType = cls.getAnnotation(XmlType.class);
        //        System.out.println(xmlType);
        Object bean = cls.newInstance();
        JAXBContext context = JAXBContextHelper.createJAXBContext(cls);
        StringWriter sw = new StringWriter();
        context.createMarshaller().marshal(bean, sw);
        System.out.println(sw.toString());

    }
}
