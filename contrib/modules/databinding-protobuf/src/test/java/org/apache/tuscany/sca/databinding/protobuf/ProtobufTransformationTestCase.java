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

package org.apache.tuscany.sca.databinding.protobuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.Assert;

import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.junit.Test;

import com.example.tutorial.AddressBookProtos.AddressBook;
import com.example.tutorial.AddressBookProtos.Person;

/**
 * 
 */
public class ProtobufTransformationTestCase {
    @Test
    public void testTransform() throws Exception {
        Protobuf2OutputStream t1 = new Protobuf2OutputStream();
        TransformationContext context = new TransformationContextImpl();
        DataType dt1 = new DataTypeImpl(AddressBook.class, null);
        context.setSourceDataType(dt1);

        AddressBook addressBook =
            AddressBook.newBuilder()
                .addPerson(Person.newBuilder().setId(1).setEmail("abc@example.com").setName("John Smith").build())
                .build();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        t1.transform(addressBook, bos, context);

        InputStream2Protobuf t2 = new InputStream2Protobuf();
        context.setSourceDataType(null);
        context.setTargetDataType(dt1);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        AddressBook addressBook2 = (AddressBook)t2.transform(bis, context);
        Assert.assertEquals(addressBook, addressBook2);
    }
}
