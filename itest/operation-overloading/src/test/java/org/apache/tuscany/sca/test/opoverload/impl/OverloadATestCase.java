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
package org.apache.tuscany.sca.test.opoverload.impl;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.tuscany.api.SCARuntime;
import org.apache.tuscany.sca.test.opoverload.OverloadASourceTarget;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

public class OverloadATestCase extends TestCase {
    private OverloadASourceTarget overloadA;

    private CompositeContext context;

    
    public void testOperationAall() {
      String[] result= overloadA.operationAall();
       assertEquals(5, result.length);
       assertEquals(OverloadASourceTarget.opName , result[0]);
       assertEquals(OverloadASourceTarget.opName + 11, result[1]);
       assertEquals(OverloadASourceTarget.opName + "eleven", result[2]);
       assertEquals(OverloadASourceTarget.opName + 3 + "three", result[3]);
       assertEquals(OverloadASourceTarget.opName +  "four" + 4, result[4]);
 }

//    public void testOperationAInt() {
//        String result= overloadA.operationA(29);
//         assertEquals(OverloadASourceTarget.opName + 29, result);
//    }
//
//    public void testOperationAString() {
//        String result= overloadA.operationA("rick:-)");
//         assertEquals(OverloadASourceTarget.opName + "rick:-)", result);
//    }
//    
//    public void testOperationAIntString() {
//        String result= overloadA.operationA(123, "Tuscany");
//         assertEquals(OverloadASourceTarget.opName +123+ "Tuscany", result);
//    }
//
//    public void testOperationStringInt() {
//        String result= overloadA.operationA("StringInt", 77);
//         assertEquals(OverloadASourceTarget.opName + "StringInt" + 77, result);
//    }
//  
    
    @Override
    protected void setUp() throws Exception {
        SCARuntime.start("OperationOverload.composite");
        context = CurrentCompositeContext.getContext();
        assertNotNull(context);
        overloadA = context.locateService(OverloadASourceTarget.class, "OverloadASourceComponent");

        assertNotNull(context);
    }
}
