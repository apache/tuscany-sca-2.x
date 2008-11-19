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

package org.apache.tuscany.sca.implementation.resource.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;

/**
 * An invoker for a get resource operation.
 *
 * @version $Rev$ $Date$
 */
class GetResourceInvoker implements Invoker {
    private String locationURL;
    
    GetResourceInvoker(String locationURL) {
        this.locationURL = locationURL;
    }
    
    public Message invoke(Message msg) {
        
        // Get the resource id from the request message
        String id = (String)((Object[])msg.getBody())[0];
        try {
            
            // Return an input stream for the resource
            URL url = new URL(locationURL +'/' + id);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            InputStream is = connection.getInputStream();
            msg.setBody(is);
        } catch (MalformedURLException e) {

            // Report exception as a fault
            msg.setFaultBody(e);
        } catch (IOException e) {

            // Report exception as a fault
            msg.setFaultBody(e);
        }
        return msg;
    }

}
