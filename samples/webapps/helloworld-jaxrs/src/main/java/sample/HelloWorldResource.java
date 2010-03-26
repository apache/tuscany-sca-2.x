/*******************************************************************************
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
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *  
 *******************************************************************************/

package sample;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.oasisopen.sca.annotation.Reference;

/**
 * Simple example - Hello World!
 * <p/>
 * The SDK dispatches HTTP requests for URI
 * <code>http://[host]:[port]/HelloWorld/rest/world</code>, where
 * <code>HelloWorld</code> is the context root, to this class. A simple Atom
 * entry is returned in HTTP response.
 * <p/>
 * The service document is available at URI
 * <code>http://[host]:[port]/HelloWorld/rest</code> but it is empty because
 * this simple demo doesn't contain any collection of resources.
 * <p/>
 * This resource must be registered within a JAX-RS application, this example
 * uses the default usage of application /WEB-INF/application
 */
@Path("/world")
public class HelloWorldResource {

    @Reference
    protected HelloworldService service;


    /**
     * This method is called by the SDK for HTTP GET method requests where the
     * Accept header allows the Atom media type application/atom+xml. A
     * SyndEntry is created with basic information. Serialization of the
     * SyndEntry to Atom entry is performed by the SDK automatically. The
     * default status code of 200 (OK) is returned in the response.
     * 
     * @return SyndEntry of the requested resource
     */
    @GET
    @Produces("text/plain") 
    public String getClichedMessage() {
       return service == null ? "SCA reference not injected" : service.sayHello("World");
    }    

}
