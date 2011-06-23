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

package helloworld;

import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Requires;

@Requires("{http://tuscany.apache.org/xmlns/sca/1.1}testIntent")
public class HelloWorldClient implements HelloWorld {

    @Reference
    public HelloWorld helloWorldWS;
    
    public String getGreetings(String s) {
        StatusImpl.appendStatus("At client.getGreetings() pre-invoke", s);
        String response = helloWorldWS.getGreetings(s);
        StatusImpl.appendStatus("At client.getGreetings() post-invoke", response);
        return response;
    }
    
    public String getGreetingsException(String s) throws HelloWorldException {
        StatusImpl.appendStatus("At client.getGreetingsException() pre-invoke", s);
        try {
            String response = helloWorldWS.getGreetingsException(s);
            StatusImpl.appendStatus("At client.getGreetingsException() post-invoke", response);
            return response;    
        } catch (HelloWorldException ex){
            StatusImpl.appendStatus("At client.getGreetingsException() post-exception", ex.getMessage());
            throw ex;
        }
    }
}
