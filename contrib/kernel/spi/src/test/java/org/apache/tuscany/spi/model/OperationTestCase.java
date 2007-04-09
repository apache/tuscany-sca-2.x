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
package org.apache.tuscany.spi.model;

import java.util.ArrayList;
import java.util.List;

import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class OperationTestCase extends TestCase {

    public void testClone() throws Exception {
        DataType<Class> stringType = new DataType<Class>("xml:string", String.class, String.class);
        List<DataType<Class>> inputTypes = new ArrayList<DataType<Class>>();
        inputTypes.add(stringType);
        DataType<List<DataType<Class>>> inputType =
            new DataType<List<DataType<Class>>>("xml:string", Object[].class, inputTypes);

        DataType<Class> faultType = new DataType<Class>("xml:foo", String.class, String.class);
        List<DataType<Class>> faultTypes = new ArrayList<DataType<Class>>();
        faultTypes.add(faultType);

        Operation<Class> operation1 =
            new Operation<Class>("call", inputType, stringType, faultTypes, true, "xml:string", NO_CONVERSATION);
        Operation<Class> operation2 = operation1.clone();
        assertEquals(operation1, operation2);
        assertEquals(NO_CONVERSATION, operation2.getConversationSequence());
        assertEquals("call", operation2.getName());
    }

}
