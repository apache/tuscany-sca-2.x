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
package org.apache.tuscany.sca.test.spec.impl;

import org.apache.tuscany.sca.test.spec.BasicService;
import org.apache.tuscany.sca.test.spec.MathService;
import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(BasicService.class)
public class BasicServiceImpl implements BasicService {

    @Context
    protected ComponentContext context;

    //The reference anntation is used in lieu of a component type definition
    //This makes the call to getService redundant since the reference is injected
    //but it serves the purpose to demonstrate that getService is working.
    @Reference
    protected MathService mathServiceReference;

    public int negate(int theInt) {
        return -theInt;
    }

    public int delegateNegate(int theInt) {
        mathServiceReference = context.getService(MathService.class, "mathServiceReference");
        return mathServiceReference.negate(theInt);
    }

}
