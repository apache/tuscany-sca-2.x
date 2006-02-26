/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.binding.axis.handler;

import java.lang.reflect.Array;

import org.apache.tuscany.core.context.TargetException;

/**
 * A mock client for a transport binding
 * 
 * @version $Rev$ $Date$
 */
public class ExternalWebServiceClient {

    public ExternalWebServiceClient() {
    }

    public Object invoke(Object msg) {
        if (msg!=null && msg.getClass().isArray() && Array.getLength(msg) == 1){
            return Array.get(msg,0);
        }else{
            throw new TargetException("This binding only understands operations with a single parameter");
        }
    }
}
