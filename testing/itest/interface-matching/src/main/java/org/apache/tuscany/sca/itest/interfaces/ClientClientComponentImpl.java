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

package org.apache.tuscany.sca.itest.interfaces;

import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

/*
 * Test that a client can be reference using a target name containing only the component 
 * name when the client has callback services registered. 
 */
@Service(ClientComponent.class)
public class ClientClientComponentImpl implements ClientComponent{

    @Reference
    protected ClientComponent aClient;
    
    public String foo(ParameterObject po) {
        return aClient.foo(po);
    }    

    public String foo1(ParameterObject po) {
        return aClient.foo1(po);
    }

    public String foo2(String str) throws Exception {
        return str + "AComponent";
    }

    public String foo3(String str, int i) {
        return str + "AComponent" + i;
    }

    public String foo4(int i, String str) throws Exception {
        return str + "AComponent" + i;
    }

    public void callback(String str) {
    }

    public void callbackMethod(String str) {
    }
    
    public void callbackMethod1(String str) {
    }    

    public void callModifyParameter() {
    }

    public String getCallbackValue() {
        return null;
    }

    public void onewayMethod(String str) {
    }

    public String getOnewayValue() {
        return null;
    }

    public void modifyParameter(ParameterObject po) {
    }

}
