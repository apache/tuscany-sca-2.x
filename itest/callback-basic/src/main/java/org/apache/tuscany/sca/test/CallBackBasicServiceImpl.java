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
package org.apache.tuscany.sca.test;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Service;

@Service(CallBackBasicService.class)
public class CallBackBasicServiceImpl implements CallBackBasicService {

    @Callback
    protected CallBackBasicCallBack callback;

    public void knockKnock(String aString) {

        System.out.println("CallBackBasicServiceImpl message received: " + aString);
        callback.callBackMessage("Who's There");
        System.out.println("CallBackBasicServiceImpl response sent");
        return;

    }

    public void multiCallBack(String aString) {

        System.out.println("CallBackBasicServiceImpl message received: " + aString);
        callback.callBackIncrement("Who's There 1");
        System.out.println("CallBackBasicServiceImpl response sent");
        callback.callBackIncrement("Who's There 2");
        System.out.println("CallBackBasicServiceImpl response sent");
        callback.callBackIncrement("Who's There 3");
        System.out.println("CallBackBasicServiceImpl response sent");
        return;

    }

    public void noCallBack(String aString) {

        System.out.println("CallBackBasicServiceImpl message received: " + aString);
        // System.out.println("CallBackBasicServiceImpl No response desired");
        return;

    }
}
