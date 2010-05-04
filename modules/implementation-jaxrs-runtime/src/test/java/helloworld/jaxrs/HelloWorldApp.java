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
package helloworld.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class HelloWorldApp extends Application {

    /*
     * A singletons is used to serve multiple requests.  
     * It keeps domain-model objects in memory between requests.   
     */
    private Set<Object> instances = new HashSet<Object>();

    /* 
     * Per-request service classes are instantiated for each 
     * request, and disposed of after the request is processed.
    */
    private Set<Class<?>> classes = new HashSet<Class<?>>();

    /**
     * Create the singleton service implementation
     */
    public HelloWorldApp() {
        instances.add(new HelloWorld());
        // classes.add(HelloWorld.class);
    }

    /**
     * Methods used to discover the JAX-RS service objects   
     */

    // Set of singleton service objects 
    @Override
    public Set<Object> getSingletons() {
        return instances;
    }

    // Set of per-request service classes 
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

}
