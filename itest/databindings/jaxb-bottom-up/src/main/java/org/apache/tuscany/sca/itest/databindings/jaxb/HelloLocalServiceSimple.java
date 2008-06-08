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
package org.apache.tuscany.sca.itest.databindings.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The interface for HelloLocalServiceSimple.
 */
public interface HelloLocalServiceSimple {
    String getGreetings(String name);
    String[] getGreetingsArray(String[] names);
    /**
     * Add the RequestWrapper/ResponseWrapper annotations to support Collections.  These annotations are used
     * by the remotable interface that extends this interface.
     * @param names
     * @return
     */
    List<String> getGreetingsList(List<String> names);
    
    /**
     * Add the RequestWrapper/ResponseWrapper annotations to support Collections.  These annotations are used
     * by the remotable interface that extends this interface.
     * @param names
     * @return
     */
    // @RequestWrapper(className="org.apache.tuscany.sca.itest.databindings.jaxb.impl.jaxws.GetGreetingsList")
    // @ResponseWrapper(className="org.apache.tuscany.sca.itest.databindings.jaxb.impl.jaxws.GetGreetingsListResponse")
    ArrayList<String> getGreetingsArrayList(ArrayList<String> names);
    
    // @WebMethod(exclude=true)
    Map<String, String> getGreetingsMap(Map<String, String> namesMap);
    
    // @WebMethod(exclude=true)
    HashMap<String, String> getGreetingsHashMap(HashMap<String, String> namesMap);
}
