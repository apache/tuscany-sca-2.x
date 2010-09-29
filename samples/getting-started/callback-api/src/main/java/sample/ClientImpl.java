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
package sample;

import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

@Service(Client.class)
public class ClientImpl implements Client, CallBack {

    public static final String DELIMITER = "\n----------------------------";
    
    @Reference
    protected sample.Service service;

    private static int callBackCount = 0;

    /**
     * This function prints the message received from the service
     * implementation.
     * 
     * @param String the message received from the service
     */
    public void callBackMessage(String aString) {
        System.out.println("ClientImpl - Received callback message: " + aString);
    }

    /**
     * This function increments the callBackCount variable when called from the
     * service implementation.
     */
    public void callBackIncrement() {
        System.out.println("ClientImpl - Received increment callback");
        callBackCount++;
        System.out.println("ClientImpl - Callback counter incremented to : " + getCallBackCount());
    }

    /**
     * This method runs different kinds of service calls implying callbacks.
     */
    public void run() {
        simpleCallBack();
        simpleCallBackByRef();
        noCallBack();
        multipleCallBack();
    }

    /**
     * The basic callback where the target calls back prior to returning to the
     * client.
     */
    private void simpleCallBack() {
        System.out.println(DELIMITER + "\nSimple callback" + DELIMITER);
        service.knockKnock("Knock Knock");
    }

    /**
     * The basic callback where the target calls back prior to returning to the
     * client.
     */
    private void simpleCallBackByRef() {
        System.out.println(DELIMITER + "\nSimple callback by reference" + DELIMITER);
        service.knockKnockByRef("Knock Knock by reference");
    }

    /**
     * The basic callback where the target does not call back to the client.
     */
    private void noCallBack() {
        System.out.println(DELIMITER + "\nNo callback" + DELIMITER);
        service.noCallBack("No Reply Desired");
    }

    /**
     * The basic callback where the target calls back multiple times to the
     * client.
     */
    private void multipleCallBack() {
        System.out.println(DELIMITER + "\nMultiple callbacks" + DELIMITER);
        service.multiCallBack("Call me back 3 times");
    }

    /**
     * This function returns the callBackCount variable.
     * 
     * @return Integer the callBackCount variable
     */
    public int getCallBackCount() {
        return callBackCount;
    }

}
