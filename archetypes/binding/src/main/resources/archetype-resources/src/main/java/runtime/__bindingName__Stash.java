#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package ${package}.binding.foo.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * Simplistic static Map to share service endpoints with references
 */
public class FooStash {
    
    private static Map<String, FooServiceInvoker> services = new HashMap<String, FooServiceInvoker>();
    
    public static void addService(String uri, FooServiceInvoker fooServiceInvoker) {
        services.put(uri, fooServiceInvoker);
    }

    public static FooServiceInvoker getService(String uri) {
        return services.get(uri);
    }

    public static void removeService(String uri) {
        services.remove(uri);
    }

}
