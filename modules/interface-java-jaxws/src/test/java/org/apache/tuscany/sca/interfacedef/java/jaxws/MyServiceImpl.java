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

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @version $Rev$ $Date$
 */
@WebService
public class MyServiceImpl {
    
    public MyServiceImpl() {
        super();
    }

    @WebMethod
    public <T extends Bean1> T getBean(T b, Bean2 b2) {
        return null;
    }
    
    @WebMethod
    public List<? extends Bean1> getBeans() {
        return null;
    }
    
    @WebMethod
    public String convert(String str, int i) throws MyException {
        return "ME";
    }
    
}
