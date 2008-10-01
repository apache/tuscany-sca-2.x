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

package org.apache.tuscany.sca.databinding.jaxb;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jvnet.jaxb.reflection.model.annotation.RuntimeInlineAnnotationReader;
import org.jvnet.jaxb.reflection.model.core.Ref;
import org.jvnet.jaxb.reflection.model.impl.RuntimeModelBuilder;
import org.jvnet.jaxb.reflection.model.runtime.RuntimeClassInfo;
import org.jvnet.jaxb.reflection.model.runtime.RuntimePropertyInfo;
import org.jvnet.jaxb.reflection.model.runtime.RuntimeTypeInfoSet;
import org.jvnet.jaxb.reflection.runtime.IllegalAnnotationsException;
import org.jvnet.jaxb.reflection.runtime.JAXBContextImpl;

import com.example.ipo.jaxb.ObjectFactory;
import com.example.ipo.jaxb.PurchaseOrderType;
import com.example.ipo.jaxb.USAddress;
import com.example.ipo.jaxb.USState;

/**
 * @version $Rev$ $Date$
 */
public class JAXBReflectionTestCase extends TestCase {

    public void testGenerateSchema() throws Exception {
        JAXBContext context = JAXBContext.newInstance("com.example.ipo.jaxb");
        Map<String, String> schemas = JAXBTypeHelper.generateSchema(context);
        System.out.println(schemas);
    }

    /**
     * This is a workaround for the NPE bug in jaxb-reflection
     * @param classes
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private static RuntimeTypeInfoSet create(Class... classes) throws Exception {
        IllegalAnnotationsException.Builder errorListener = new IllegalAnnotationsException.Builder();
        RuntimeInlineAnnotationReader reader = new RuntimeInlineAnnotationReader();
        JAXBContextImpl context =
            new JAXBContextImpl(classes, null, Collections.<Class, Class> emptyMap(), null, false, reader, false, false);
        RuntimeModelBuilder builder =
            new RuntimeModelBuilder(context, reader, Collections.<Class, Class> emptyMap(), null);
        builder.setErrorHandler(errorListener);
        for (Class c : classes)
            builder.getTypeInfo(new Ref<Type, Class>(c));

        RuntimeTypeInfoSet r = builder.link();
        errorListener.check();
        return r;
    }

    public void testReflection() throws Exception {
        org.jvnet.jaxb.reflection.model.runtime.RuntimeTypeInfoSet model = create(PurchaseOrderType.class);
        RuntimeClassInfo type = (RuntimeClassInfo)model.getTypeInfo(PurchaseOrderType.class);
        Assert.assertEquals(new QName("http://www.example.com/IPO", "PurchaseOrderType"), type.getTypeName());
        type = (RuntimeClassInfo)model.getTypeInfo(USAddress.class);
        Assert.assertEquals(new QName("http://www.example.com/IPO", "USAddress"), type.getTypeName());
        RuntimePropertyInfo prop = type.getProperty("state");
        Assert.assertNotNull(prop);
        USAddress address = new ObjectFactory().createUSAddress();
        prop.getAccessor().set(address, USState.CA);
        Assert.assertEquals(USState.CA, address.getState());
    }
}
