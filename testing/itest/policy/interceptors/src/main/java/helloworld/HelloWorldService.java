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

import javax.security.auth.Subject;

import org.oasisopen.sca.RequestContext;
import org.oasisopen.sca.annotation.Context;

public class HelloWorldService implements HelloWorld {

    @Context
    protected RequestContext requestContext;
    
    public String getGreetings(String s) {
        //Subject subject = requestContext.getSecuritySubject();
        String response = "Hello " + s;       
        StatusImpl.appendStatus("At service.getGreetings()", response);
        return response;
    }
    
    public String getGreetingsException(String s) throws HelloWorldException {
        String response = "Hello " + s;  
        StatusImpl.appendStatus("At service.getGreetingsException()", response);
        throw new HelloWorldException(response);
    }
}
