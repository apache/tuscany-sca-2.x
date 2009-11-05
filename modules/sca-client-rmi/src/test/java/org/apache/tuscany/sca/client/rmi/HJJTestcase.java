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

package org.apache.tuscany.sca.client.rmi;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;
import org.oasisopen.sca.NoSuchDomainException;
import org.oasisopen.sca.NoSuchServiceException;

import calculator.CalculatorService;

public class HJJTestcase {

    @Test
    public void foo() throws NoSuchServiceException, NoSuchDomainException {
        SCAClientFactoryImpl cf = new SCAClientFactoryImpl(URI.create("tribes:foo"));
        CalculatorService service = cf.getService(CalculatorService.class, "CalculatorServiceComponent");
        assertEquals(3, service.add(1, 2), 0);
    }
}
