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

package org.apache.tuscany.sca.binding.ws.wsdlgen;

import javax.xml.ws.WebFault;

/**
 *
 * @version $Rev$ $Date$
 */
@WebFault(faultBean="org.apache.tuscany.sca.binding.ws.wsdlgen.TestFaultBean")
public class TestFault extends Exception {

    private TestFaultBean bean;

    public TestFault(TestFaultBean bean, String message) {
        super(message);
        this.bean = bean;
    }

    public TestFault(TestFaultBean bean, String message, Throwable cause) {
        super(message, cause);
        this.bean = bean;
    }

    public TestFaultBean getFaultInfo() {
        return bean;
    }
}
