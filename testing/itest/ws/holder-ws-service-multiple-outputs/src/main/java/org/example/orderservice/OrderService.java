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

package org.example.orderservice;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Holder;

import org.oasisopen.sca.annotation.Remotable;



@WebService
@Remotable
public interface OrderService {

    public String[] reviewOrder(
        @WebParam(name = "myData", targetNamespace = "", mode = WebParam.Mode.INOUT)
        Holder<Order> myData, 
        @WebParam(name = "myOutParam", targetNamespace = "", mode = WebParam.Mode.OUT)
        Holder<Float> myOutParam);

    public String[] reviewOrderTwoInOuts(
            @WebParam(name = "myData", targetNamespace = "", mode = WebParam.Mode.INOUT)
            Holder<Order> myData, 
            @WebParam(name = "myOutParam", targetNamespace = "", mode = WebParam.Mode.INOUT)
            Holder<Float> myOutParam);
    
    public String[] reviewOrderTwoOutHolders(
            @WebParam(name = "myData", targetNamespace = "", mode = WebParam.Mode.OUT)
            Holder<Order> myData, 
            @WebParam(name = "myOutParam", targetNamespace = "", mode = WebParam.Mode.OUT)
            Holder<Float> myOutParam);
    
    public String[] reviewOrderTwoInOutsThenIn(
            @WebParam(name = "myData", targetNamespace = "", mode = WebParam.Mode.INOUT)
            Holder<Order> myData, 
            @WebParam(name = "myOutParam", targetNamespace = "", mode = WebParam.Mode.INOUT)
            Holder<Float> myOutParam,
            Integer myCode);   
}
