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
package echo.provider;

import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

/**
 * Invoker for the sample echo binding.
 */
class EchoBindingInvoker implements Invoker {
    
    EchoBindingInvoker() {
    }

    public Message invoke(Message msg) {
        try {
            System.out.println("Passing thro invoker...");
            Object[] args = msg.getBody();

            // echo back the first parameter, a real binding would invoke some API for flowing the request
            Object result = args[0];
                                 
            msg.setBody(result);
            
        } catch (Exception e) {
            msg.setFaultBody(e);
        }
        return msg;
    }  

}
