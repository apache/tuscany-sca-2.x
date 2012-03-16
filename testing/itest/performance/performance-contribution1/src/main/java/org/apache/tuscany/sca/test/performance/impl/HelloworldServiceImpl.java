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
package org.apache.tuscany.sca.test.performance.impl;

import org.apache.tuscany.sca.test.performance.BeanA;
import org.apache.tuscany.sca.test.performance.Helloworld;
import org.oasisopen.sca.annotation.Requires;

@Requires("{http://tuscany.apache.org/xmlns/sca/1.1}logging")
public class HelloworldServiceImpl implements Helloworld {

    public String sayHello(String name, BeanA beanA) {
        String returnStrng =  "Hello " + name + " " + beanA.getField1();
        System.out.println("At HelloworldServiceImpl - " + returnStrng);
        return returnStrng;
    }

}
