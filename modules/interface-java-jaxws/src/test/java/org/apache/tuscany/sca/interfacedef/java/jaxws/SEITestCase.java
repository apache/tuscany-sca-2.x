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
import java.lang.reflect.Type;
import java.util.Collections;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;

import junit.framework.TestCase;

import org.jvnet.jaxb.reflection.model.annotation.RuntimeInlineAnnotationReader;
import org.jvnet.jaxb.reflection.model.core.Ref;
import org.jvnet.jaxb.reflection.model.impl.RuntimeModelBuilder;
import org.jvnet.jaxb.reflection.model.runtime.RuntimeTypeInfoSet;
import org.jvnet.jaxb.reflection.runtime.IllegalAnnotationsException;
import org.jvnet.jaxb.reflection.runtime.JAXBContextImpl;
import org.w3c.dom.Node;

import com.example.stock.StockOffer;


/**
 * @version $Rev$ $Date$
 */
public class SEITestCase extends TestCase {
    // public void testMetadata() {
    // ServiceDescription service =
    // DescriptionFactory.createServiceDescription(AWSECommerceServicePortType.class);
    // }

    public void testGenerateSchema() throws Exception {
        JAXBContext context = JAXBContext.newInstance("com.example.stock");
        SchemaOutputResolverImpl resolver = new SchemaOutputResolverImpl();
        context.generateSchema(resolver);
        System.out.println(resolver.getSchema());
    }

    public static class SchemaOutputResolverImpl extends SchemaOutputResolver {
        private DOMResult result = new DOMResult();

        @Override
        public Result createOutput(String ns, String file) throws IOException {
            System.out.println(ns);
            System.out.println(file);
            result.setSystemId("sca:dom");
            return result;
        }

        public Node getSchema() {
            return result != null ? result.getNode() : null;
        }

    }
    
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
        org.jvnet.jaxb.reflection.model.runtime.RuntimeTypeInfoSet model = create(StockOffer.class);
        model.getTypeInfo(StockOffer.class);

    }
}
