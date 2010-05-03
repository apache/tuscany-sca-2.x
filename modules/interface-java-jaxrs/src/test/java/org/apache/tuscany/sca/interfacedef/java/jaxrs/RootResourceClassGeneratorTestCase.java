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

package org.apache.tuscany.sca.interfacedef.java.jaxrs;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.ws.rs.Path;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 */
public class RootResourceClassGeneratorTestCase {
    @Test
    public void testGenerator() throws Exception {
        Class<?> cls = RootResourceClassGenerator.generateRootResourceClass(Resource.class, "myURI");
        Assert.assertTrue(cls.isAnnotationPresent(Path.class));
        Path path = cls.getAnnotation(Path.class);
        Assert.assertEquals("myURI", path.value());
        Field field = cls.getField("delegate");
        Assert.assertSame(Resource.class, field.getType());

        Assert.assertTrue(Modifier.isPublic(field.getModifiers()));
        Assert.assertTrue(Modifier.isStatic(field.getModifiers()));

        Assert.assertTrue(Resource.class.isAssignableFrom(cls));

        Resource resource = new MockedResource();
        field.set(null, resource);

        Resource resourceProxy = (Resource)cls.newInstance();
        Assert.assertNull(resourceProxy.get());
        resourceProxy.create("123");
        Assert.assertEquals("123", resourceProxy.get());
        resourceProxy.update("ABC");
        Assert.assertEquals("ABC", resourceProxy.get());
        resourceProxy.delete();
        Assert.assertNull(resourceProxy.get());
    }
}
