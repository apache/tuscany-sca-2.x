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

package org.apache.tuscany.sca.vtest.javaapi.annotations.service.impl;

import org.apache.tuscany.sca.vtest.javaapi.annotations.service.DService1;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.DService2;
import org.apache.tuscany.sca.vtest.javaapi.annotations.service.DService3;
import org.osoa.sca.annotations.Service;

@Service(interfaces = {DService1.class, DService2.class})
public class DServiceImpl implements DService1, DService2, DService3 {

    public String getName1() {
        return "DService1";
    }

    public String getName2() {
        return "DService2";
    }

    public String getName3() {
        return "DService3";
    }
}
