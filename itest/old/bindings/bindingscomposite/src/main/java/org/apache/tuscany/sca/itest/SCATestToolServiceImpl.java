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
package org.apache.tuscany.sca.itest;

import org.apache.tuscany.sca.util.SCATestUtilityService;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

/**
 * SCA Test Service Implementation
 */

@Service(SCATestToolService.class)
public class SCATestToolServiceImpl implements SCATestToolService, SCATestToolCallbackService {
    @Reference
    public SCATestUtilityService scaTestUtil;

    private String callbackBuffer = null;

    public String doOneHopPing(String input) {
        System.out.println("Invoking SCATestToolServiceImpl.doOneHopPing()");
        StringBuffer rc = new StringBuffer();
        rc.append("doOneHopPing(): ");
        rc.append(input);
        return rc.toString();
    }

    public String doTwoHopPing(String input) {
        System.out.println("Invoking SCATestToolServiceImpl.doTwoHopPing()");
        StringBuffer rc = new StringBuffer();
        rc.append("doTwoHopPing(): ");
        rc.append(input);
        rc.append(" --> ");
        rc.append(scaTestUtil.ping(input));
        return rc.toString();
    }

    public String doDataTypeTest(String input) {
        StringBuffer rc = new StringBuffer();
        rc.append("doDataTypeTest(): ");
        rc.append(input);
        rc.append(" --> ");
        SCADataTypeHelper dataHelper = new SCADataTypeHelper(scaTestUtil);
        rc.append(dataHelper.doDataType());
        return rc.toString();
    }

    public void pingCallBack(String reply) {
        callbackBuffer = reply;
    }

    public String getCallbackBuffer() {
        return callbackBuffer;
    }

    public void clearCallbackBuffer() {
        callbackBuffer = null;
    }

}
