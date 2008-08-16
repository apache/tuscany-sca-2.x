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

package org.apache.tuscany.sca.binding.corba.testing;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.sca.binding.corba.impl.util.OperationMapper;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 * Various tests for OperationsMapping
 */
public class OperationMappingTestCase {

    /**
     * Tests if Java2IDLUtil maps methods to operations correctly
     */
    @Test
    public void test_mappingRules() {
        Map<Method, String> met2op = OperationMapper.mapMethodToOperationName(MappingTestInterface.class);
        for (Method method : met2op.keySet()) {
            String name = method.getName();
            String translatedName = met2op.get(method);
            if (name.equals("getIntField")) {
                assertEquals("_get_intField", translatedName);
            } else if (name.equals("setIntField")) {
                assertEquals("_set_intField", translatedName);
            } else if (name.equals("isBoolField")) {
                assertEquals("_get_boolField", translatedName);
            } else if (name.equals("setBoolField")) {
                assertEquals("_set_boolField", translatedName);
            } else if (name.equals("overloadedName") && method.getParameterTypes().length == 0) {
                assertEquals("overloadedName__", translatedName);
            } else if (name.equals("overloadedName") && method.getParameterTypes().length == 1) {
                assertEquals("overloadedName__CORBA_WStringValue", translatedName);
            } else if (name.equals("overloadedName") && method.getParameterTypes().length == 2) {
                assertEquals("overloadedName__CORBA_WStringValue__long", translatedName);
            } else if (name.equals("caseCollision")) {
                assertEquals("caseCollision_4", translatedName);
            } else if (name.equals("CaseCollision")) {
                assertEquals("CaseCollision_0_4", translatedName);
            }
        }
    }

}
