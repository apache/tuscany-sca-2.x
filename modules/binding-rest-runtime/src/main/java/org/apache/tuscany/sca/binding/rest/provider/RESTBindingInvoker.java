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

package org.apache.tuscany.sca.binding.rest.provider;

import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

/**
 * Invoker for the REST Binding
 * 
 * @version $Rev: 665897 $ $Date: 2008-06-09 22:31:03 +0100 (Mon, 09 Jun 2008) $
 */
public class RESTBindingInvoker implements Invoker {
    Operation operation;
    String uri;

    public RESTBindingInvoker(Operation operation, String uri) {
        this.operation = operation;
        this.uri = uri;        
    }
    
    public Message invoke(Message msg) {
        // TODO Auto-generated method stub
        return null;
    }

}
