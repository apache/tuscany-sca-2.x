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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.ws.Holder;

import org.osoa.sca.annotations.Remotable;

/**
 * @version $Rev$ $Date$
 */
@Remotable
public interface TestInterface {
    int convert(String currency1, String currency2);

    List<Double> getRates(String currency);

    void check(boolean flag);

    String[] list(int[] list);

    int[][] map(String[][] strs);

    String getGreetings(String name);

    String[] getGreetingsArray(String[] names);

    List<String> getGreetingsList(List<String> names);

    ArrayList<String> getGreetingsArrayList(ArrayList<String> names);

    Map<String, String> getGreetingsMap(Map<String, String> namesMap);

    HashMap<String, String> getGreetingsHashMap(HashMap<String, String> namesMap);
    
    @WebMethod
    @WebResult(name = "output")
    String webMethod(@WebParam(name = "input", mode = WebParam.Mode.IN)
    String in, @WebParam(name = "holder", mode = WebParam.Mode.INOUT)
    Holder<String> holder);
}
